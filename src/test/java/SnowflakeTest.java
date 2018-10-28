import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * @ProjectName: MySnowflake
 * @Package: PACKAGE_NAME
 * @Description: java类作用描述
 * @Author: qiuqiu
 * @CreateDate: 2018/10/28 13:08
 * @UpdateDate: 2018/10/28 13:08
 **/
public class SnowflakeTest {

    public static void main(String[] args) {
        Set<Long> set = new HashSet<>();
        Snowflake snowflake1 = new Snowflake(0,0);
        Snowflake snowflake2 = new Snowflake(1,0);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new workThread(set, snowflake1));
        executorService.execute(new workThread(set, snowflake2));
    }
    static class workThread implements Runnable {
        private Set<Long> set;
        private Snowflake snowflake;
        public workThread(Set<Long> set, Snowflake snowflake) {
            this.set = set;
            this.snowflake = snowflake;
        }

        @Override
        public void run() {
            while (true) {
                long id = snowflake.nextId();
                if (!set.add(id)) {
                    System.out.println("duplicate:" + id);
                }
            }
        }
    }
}