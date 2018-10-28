import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @ProjectName: MySnowflake
 * @Package: PACKAGE_NAME
 * @Description: java类作用描述
 * @Author: qiuqiu
 * @CreateDate: 2018/10/28 15:42
 * @UpdateDate: 2018/10/28 15:42
 **/
public class SnowflakeTest2 {
    @Test
    public void name() {
        try {
            int times = 0, maxTimes = 1000;
            Snowflake snowflake = new Snowflake(0, 0);
            for (int i = 0; i < maxTimes; i++) {
                long id = snowflake.nextId();
                if(id%2==0){
                    times++;
                }
                System.out.println(id);
            }
            System.out.println("偶数:" + times + ",奇数:" + (maxTimes - times) + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}