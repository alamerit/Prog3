package geekbrains.ru.lesson5_sugarorm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import androidx.test.runner.AndroidJUnit4;
import dagger.Component;
import geekbrains.ru.lesson5_sugarorm.dagger.DaggerNetModule;
import geekbrains.ru.lesson5_sugarorm.retrofit.RetrofitModel;
import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.HttpException;

@Component(modules = DaggerNetModule.class)
interface RetrofitModelTestComponent {
    void inject(MockServerTest test);
}

@RunWith(AndroidJUnit4.class)
public class MockServerTest {
    private MockWebServer mockWebServer;

    @Inject
    Single<List<RetrofitModel>> request;

    private static final String login = "mojombo";
    private static final String avatarUrl = "test_url";
    private static final String id = "1";

    @Before
    public void prepare() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        DaggerRetrofitModelTestComponent.builder()
                .daggerNetModule(new DaggerNetModule(){
                    @Override
                    public String provideEndpoint() {
                        return mockWebServer.url("/").toString();
                    }})
                .build()
                .inject(this);
    }

    @Test
    public void getUsers_success(){
        mockWebServer.enqueue(createMockResponse(login, avatarUrl, id));
            TestObserver<List<RetrofitModel>> observer = TestObserver.create();
            request.subscribeWith(observer);

        observer.awaitTerminalEvent();
        observer.assertValue((modelList)->{
            if(modelList.size() == 0) return false;
            RetrofitModel model = modelList.get(0);
            return model.getLogin().equals(login) &&
                    model.getAvatarUrl().equals(avatarUrl) &&
                    model.getId().equals(id);
        });
        observer.dispose();
    }

    @Test
    public void getUsers_with_error_code(){
        mockWebServer.enqueue(errorResponse(404, "page not found"));
        TestObserver<List<RetrofitModel>> observer = TestObserver.create();
        request.subscribeWith(observer);
        observer.awaitTerminalEvent();
        observer.assertError((e)->(e instanceof HttpException) && ((HttpException)e).code() == 404);
        observer.dispose();
    }

    @Test
    public void getUsers_with_invalide_json(){
        mockWebServer.enqueue(errorResponse(200, "{ \"dsad\" : }"));
        TestObserver<List<RetrofitModel>> observer = TestObserver.create();
        request.subscribeWith(observer);

        observer.awaitTerminalEvent();
        observer.assertError((e)->e instanceof IllegalStateException);
        observer.dispose();
    }

    private MockResponse createMockResponse(String login, String avatarUrl, String id) {
        return new MockResponse().setBody("[{\n" +
                "\"login\": \"" + login + "\",\n" +
                "\"avatar_url\": \"" + avatarUrl + "\",\n" +
                "\"id\": \"" + id + "\"\n" +
                "}]");
    }

    private MockResponse errorResponse(int responseCode, String message) {
        return new MockResponse().setResponseCode(responseCode).setBody(message);
    }

    private Subscription createSubscription(){
        return new Subscription() {
            @Override
            public void request(long n) {}
            @Override
            public void cancel() {}
        };
    }

    @After
    public void shutdownServer() throws IOException {
        mockWebServer.shutdown();
    }
}