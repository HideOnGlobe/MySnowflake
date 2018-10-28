/**
 * @ProjectName: MySnowflake
 * @Package: PACKAGE_NAME
 * @Description: twitter.Snowflake实现
 * @Author: qiuqiu
 * @CreateDate: 2018/10/28 12:50
 * @UpdateDate: 2018/10/28 12:50
 **/
public class Snowflake {
    /*该任务开始时间,必须手动设置(差值的唯一性)*/
    private final long startTime = 1540708752694L;

    /*各个位的位数,Timestamp为41L(无需定义)*/
    private final long dataCenterIdBits = 5L;
    private final long workerIdBits = 5L;
    private final long sequenceBits = 12L;

    /*各位的最大值*/
    private final long dataCenterIdMax = ~(-1 << dataCenterIdBits);
    private final long workerIdMax = ~(-1 << workerIdBits);
    private final long sequenceMax = ~(-1 << sequenceBits);

    /*各位应该向左移动位数*/
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    private final long timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private final long workerIdShift = sequenceBits;

    /*数据中心ID*/
    private long dataCenterId;
    /*工作线程ID*/
    private long workerId;
    /*序列号*/
    private long sequence = 0L;
    /*上次时间(保证不回退)*/
    private long lastTimestamp = -1L;
    /*是否在高并发下*/
    private boolean isClock = false;

    public Snowflake(long dataCenterId, long workerId) {
        if (dataCenterId > dataCenterIdMax || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center id can't be greater than %d or less than 0", dataCenterIdMax));
        }
        if (workerId > workerIdMax || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker id can't be greater than %d or less than 0", workerIdMax));
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    public void setClock(boolean clock) {
        this.isClock = clock;
    }

    public synchronized long nextId() {
        long timestamp = this.getTime();

        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    this.wait(offset << 1);
                    timestamp = this.getTime();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards, Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards, Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            sequence = sequence + 1;
            if (sequence > sequenceMax) {
                timestamp = tilNextMillis(timestamp);
                sequence = 0;
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - startTime) << timestampShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /*该毫秒达到上限,等待到下1毫秒*/
    private long tilNextMillis(long lastTimestamp) {
        while (getTime() <= lastTimestamp) {
        }
        return this.getTime();
    }

    private long getTime() {
        if (isClock) {
            return SystemClock.currentTimeMillis();
        } else {
            return System.currentTimeMillis();
        }
    }
}
