package geekbrains.ru.lesson5_sugarorm.test;

public class TestResult {
    private long time;
    private int count;

    public TestResult(long time, int count){
        this.time = time;
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }
}
