import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ProjectName: MySnowflake
 * @Package: PACKAGE_NAME
 * @Description: 由于高并发,在同一毫秒中会多次获取currentTimeMillis,而每次使用System.currentTimeMillis都会占用CPU(native方法).
 *               于是自定义类(single)来获取currentTimeMillis,实现方法是在此类中定义时间并且<b>设置一个周期任务(定时线程)1毫秒更新类中的时间</b>.
 * @Author: qiuqiu
 * @CreateDate: 2018/10/28 14:41
 * @UpdateDate: 2018/10/28 14:41
 **/
public class SystemClock {
    /*更新时间的时间间隔,默认为1毫秒*/
    private final long period;
    /*当前时间*/
    private final AtomicLong now;

    private SystemClock(long period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdate();
    }

    /*单例(静态类实现)*/
    private static class SingleInstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }

    private static SystemClock getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    /*定时任务(设置为守护线程,1毫秒后开始更新)
    * scheduleAtFixedRate: 每次开始间隔为1毫秒
    * scheduleWithFixedDelay: 每次结束与开始为1毫秒
    **/
    private void scheduleClockUpdate() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "System Clock");
                thread.setDaemon(true);
                return thread;
            }
        });
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                now.set(System.currentTimeMillis());
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    public static long currentTimeMillis() {
        return getInstance().now.get();
    }

}
