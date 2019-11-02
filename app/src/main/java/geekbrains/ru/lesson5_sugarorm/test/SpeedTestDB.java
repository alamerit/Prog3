package geekbrains.ru.lesson5_sugarorm.test;

public interface SpeedTestDB{
    void before();
    void test();
    void after();
    int getCount();
}

interface ShortenedSpeedTestDB extends SpeedTestDB{
    @Override
    default void before(){}
    @Override
    void test();
    @Override
    default void after(){}
    @Override
    int getCount();
}
