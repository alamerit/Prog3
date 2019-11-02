package geekbrains.ru.lesson5_sugarorm.dagger;

import javax.inject.Named;

import dagger.Subcomponent;
import geekbrains.ru.lesson5_sugarorm.test.TestResult;
import io.reactivex.Single;

@Subcomponent(modules = DBTestModule.class)
public interface DBTestComponent {
    @GetSugar Single<TestResult> runSugarGet();
    @GetRoom Single<TestResult> runRoomGet();
    @DeleteSugar Single<TestResult> runSugarDelete();
    @DeleteRoom Single<TestResult> runRoomDelete();
    @SaveSugar Single<TestResult> runSugarSave();
    @SaveRoom Single<TestResult> runRoomSave();
}
