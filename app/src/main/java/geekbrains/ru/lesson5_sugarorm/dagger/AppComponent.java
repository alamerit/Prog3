package geekbrains.ru.lesson5_sugarorm.dagger;

import javax.inject.Singleton;

import dagger.Component;
import geekbrains.ru.lesson5_sugarorm.Presenter;

@Singleton
@Component(modules = DaggerNetModule.class)
public interface AppComponent {
    void inject(Presenter presenter);
    NetworkComponent getNetworkComponent(NetworkModule module);
    DBTestComponent getTestComponent(DBTestModule module);
}
