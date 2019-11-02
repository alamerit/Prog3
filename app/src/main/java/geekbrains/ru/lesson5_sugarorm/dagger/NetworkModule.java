package geekbrains.ru.lesson5_sugarorm.dagger;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {
    final Activity activity;

    public NetworkModule(Activity activity){
        this.activity = activity;
    }
    @Provides
    public NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) return null;
        return connectivityManager.getActiveNetworkInfo();
    }

    @Provides
    Boolean checkConnection(NetworkInfo info)
    {
        return info != null && info.isConnected();
    }
}
