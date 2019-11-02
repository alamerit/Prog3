package geekbrains.ru.lesson5_sugarorm;

import java.util.List;

import javax.inject.Inject;

import geekbrains.ru.lesson5_sugarorm.dagger.AppComponent;
import geekbrains.ru.lesson5_sugarorm.dagger.DBTestComponent;
import geekbrains.ru.lesson5_sugarorm.dagger.NetworkComponent;
import geekbrains.ru.lesson5_sugarorm.retrofit.RetrofitModel;
import geekbrains.ru.lesson5_sugarorm.test.TestResult;
import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;


public class Presenter {
    @Inject
    Single<List<RetrofitModel>> request;
    NetworkComponent networkComponent;
    DBTestComponent testComponent;

    public Presenter(){
        OrmApp.getComponent().inject(this);
    }

    void bindView(NetworkComponent networkComponent){
        this.networkComponent = networkComponent;
    }

    void unbindView(){
    }

    private void addObserver(Single<TestResult> test, DisposableSingleObserver<String> info){
        test.map((result)->"количество = " + result.getCount() + "\n милисекунд = " + result.getTime())
                .onErrorResumeNext((e)->Single.just("ошибка БД: " + e.getMessage())).subscribeWith(info);
    }

    private String formatOutput(List<RetrofitModel> retrofitModels){
        StringBuilder sb = new StringBuilder();

        sb.append("\n Size = " + retrofitModels.size()+
                "\n-----------------");
        for (RetrofitModel curModel : retrofitModels) {
            sb.append(
                    "\nLogin = " + curModel.getLogin() +
                            "\nId = " + curModel.getId() +
                            "\nURI = " + curModel.getAvatarUrl() +
                            "\n-----------------");
        }
        return sb.toString();
    }


    public void onLoad(DisposableSingleObserver<String> info, DisposableSingleObserver<Boolean> progress) {
        if (!networkComponent.checkConnection()){
            //send toast
            info.onSuccess("");
            progress.onSuccess(false);
            return;
        }

        request.map((models)->formatOutput(models)).subscribeWith(info);

        request.map((models)->false).subscribeWith(progress);

        request.subscribe(new DisposableSingleObserver<List<RetrofitModel>>() {
            @Override
            public void onSuccess(List<RetrofitModel> models) {
                testComponent = OrmApp.getComponent().getTestComponent(new DBTestModule(models));
            }

            @Override
            public void onError(Throwable e) {
                testComponent = null;
            }
        });
    }

    void saveAllSugar(DisposableSingleObserver<String> info){
        if(testComponent == null){
            info.onSuccess("");
            return;
        }

        addObserver(testComponent.runSugarSave(), info);
    }

    void deleteAllSugar(DisposableSingleObserver<String> info){
        if(testComponent == null){
            info.onSuccess("");
            return;
        }

        addObserver(testComponent.runSugarDelete(), info);
    }

    void selectAllSugar(DisposableSingleObserver<String> info){
        if(testComponent == null){
            info.onSuccess("");
            return;
        }

        addObserver(testComponent.runSugarGet(), info);
    }

    void saveAllRoom(DisposableSingleObserver<String> info) {
        if(testComponent == null){
            info.onSuccess("");
            return;
        }

        addObserver(testComponent.runRoomSave(), info);
    }

    void selectAllRoom(DisposableSingleObserver<String> info){
        if(testComponent == null){
            info.onSuccess("");
            return;
        }

        addObserver(testComponent.runRoomGet(), info);
    }

    void deleteAllRoom(DisposableSingleObserver<String> info){
        if(testComponent == null){
            info.onSuccess("");
            return;
        }

        addObserver(testComponent.runRoomDelete(), info);
    }
}
