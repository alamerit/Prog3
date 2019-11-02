package geekbrains.ru.lesson5_sugarorm.test;

import java.util.List;

import geekbrains.ru.lesson5_sugarorm.retrofit.RetrofitModel;

public interface DBOperations<T> {
    List<T> getAll();
    void saveAll(List<RetrofitModel> modelList);
    void deleteAll();
}
