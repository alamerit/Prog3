package geekbrains.ru.lesson5_sugarorm

import javax.inject.Inject

import geekbrains.ru.lesson5_sugarorm.dagger.AppComponent
import geekbrains.ru.lesson5_sugarorm.dagger.DBTestComponent
import geekbrains.ru.lesson5_sugarorm.dagger.DBTestModule
import geekbrains.ru.lesson5_sugarorm.dagger.NetworkComponent
import geekbrains.ru.lesson5_sugarorm.retrofit.RetrofitModel
import geekbrains.ru.lesson5_sugarorm.test.TestResult
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver


class Presenter {
    @Inject
    internal var request: Single<List<RetrofitModel>>? = null
    internal var networkComponent: NetworkComponent
    internal var testComponent: DBTestComponent? = null

    init {
        OrmApp.getComponent().inject(this)
    }

    internal fun bindView(networkComponent: NetworkComponent) {
        this.networkComponent = networkComponent
    }

    internal fun unbindView() {}

    private fun addObserver(test: Single<TestResult>, info: DisposableSingleObserver<String>) {
        test.map { result -> "количество = " + result.count + "\n милисекунд = " + result.time }
                .onErrorResumeNext { e -> Single.just("ошибка БД: " + e.message) }.subscribeWith(info)
    }

    private fun formatOutput(retrofitModels: List<RetrofitModel>): String {
        val sb = StringBuilder()

        sb.append("\n Size = " + retrofitModels.size +
                "\n-----------------")
        for (curModel in retrofitModels) {
            sb.append(
                    "\nLogin = " + curModel.login +
                            "\nId = " + curModel.id +
                            "\nURI = " + curModel.avatarUrl +
                            "\n-----------------")
        }
        return sb.toString()
    }


    fun onLoad(info: DisposableSingleObserver<String>, progress: DisposableSingleObserver<Boolean>) {
        if (!networkComponent.checkConnection()) {
            //send toast
            info.onSuccess("")
            progress.onSuccess(false)
            return
        }

        request!!.map { models -> formatOutput(models) }.subscribeWith(info)

        request!!.map { models -> false }.subscribeWith(progress)

        request!!.subscribe(object : DisposableSingleObserver<List<RetrofitModel>>() {
            override fun onSuccess(models: List<RetrofitModel>) {
                testComponent = OrmApp.getComponent().getTestComponent(DBTestModule(models))
            }

            override fun onError(e: Throwable) {
                testComponent = null
            }
        })
    }

    internal fun saveAllSugar(info: DisposableSingleObserver<String>) {
        if (testComponent == null) {
            info.onSuccess("")
            return
        }

        addObserver(testComponent!!.runSugarSave(), info)
    }

    internal fun deleteAllSugar(info: DisposableSingleObserver<String>) {
        if (testComponent == null) {
            info.onSuccess("")
            return
        }

        addObserver(testComponent!!.runSugarDelete(), info)
    }

    internal fun selectAllSugar(info: DisposableSingleObserver<String>) {
        if (testComponent == null) {
            info.onSuccess("")
            return
        }

        addObserver(testComponent!!.runSugarGet(), info)
    }

    internal fun saveAllRoom(info: DisposableSingleObserver<String>) {
        if (testComponent == null) {
            info.onSuccess("")
            return
        }

        addObserver(testComponent!!.runRoomSave(), info)
    }

    internal fun selectAllRoom(info: DisposableSingleObserver<String>) {
        if (testComponent == null) {
            info.onSuccess("")
            return
        }

        addObserver(testComponent!!.runRoomGet(), info)
    }

    internal fun deleteAllRoom(info: DisposableSingleObserver<String>) {
        if (testComponent == null) {
            info.onSuccess("")
            return
        }

        addObserver(testComponent!!.runRoomDelete(), info)
    }
}
