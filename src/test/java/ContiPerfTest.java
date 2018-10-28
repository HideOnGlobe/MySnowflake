import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @ProjectName: MySnowflake
 * @Package: PACKAGE_NAME
 * @Description: java类作用描述
 * @Author: qiuqiu
 * @CreateDate: 2018/10/28 15:39
 * @UpdateDate: 2018/10/28 15:39
 **/
public class ContiPerfTest {
    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    Snowflake snowflake = new Snowflake(0, 0);

    @Test
    @PerfTest(invocations = 200000000, threads = 16)
    public void test1() throws Exception {
        snowflake.nextId();
    }
}
