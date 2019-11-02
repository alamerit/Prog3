package geekbrains.ru.lesson5_sugarorm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orm.SugarContext;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class MainActivity extends AppCompatActivity{
    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private DisposableSingleObserver<String> showInfo;
    private DisposableSingleObserver<Boolean> showProgress;

    @Inject
    Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrmApp.getPresenterComponent().inject(this);
        setContentView(R.layout.activity_main);
        mInfoTextView = (TextView) findViewById(R.id.tvLoad);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ((Button) findViewById(R.id.btnLoad)).setOnClickListener((view)->presenter.onLoad(makeInfoObserver(), makeProgressObserver()));
        findViewById(R.id.btnSaveAllSugar).setOnClickListener((view)-> presenter.saveAllSugar(makeInfoObserver()));
        findViewById(R.id.btnSelectAllSugar).setOnClickListener((view)-> presenter.selectAllSugar(makeInfoObserver()));
        findViewById(R.id.btnDeleteAllSugar).setOnClickListener((view)-> presenter.deleteAllSugar(makeInfoObserver()));

        findViewById(R.id.btnSaveAllRoom).setOnClickListener((v)->presenter.saveAllRoom(makeInfoObserver()));
        findViewById(R.id.btnSelectAllRoom).setOnClickListener((v)->presenter.selectAllRoom(makeInfoObserver()));
        findViewById(R.id.btnDeleteAllRoom).setOnClickListener((v)->presenter.deleteAllRoom(makeInfoObserver()));
        SugarContext.init(this);
    }

    DisposableSingleObserver<String> makeInfoObserver(){
        if(showInfo != null && !showInfo.isDisposed()) showInfo.dispose();

        showInfo = new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String s) {
                mInfoTextView.setText(s);
            }

            @Override
            public void onError(Throwable e) {
                mInfoTextView.setText(e.getLocalizedMessage());
            }
        };
        return showInfo;
    }

    DisposableSingleObserver<Boolean> makeProgressObserver(){
        if(showProgress != null && !showProgress.isDisposed()) showProgress.dispose();
        showProgress = new DisposableSingleObserver<Boolean>() {
            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(Boolean visible) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
            }
        };
        return showProgress;
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.bindView(OrmApp.makeNetworkComponent(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(showInfo != null && !showInfo.isDisposed()) showInfo.dispose();
        if(showProgress != null && !showProgress.isDisposed()) showProgress.dispose();
        presenter.unbindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }

}
