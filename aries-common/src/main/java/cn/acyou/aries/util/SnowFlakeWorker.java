package cn.acyou.aries.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * twitter的snowflake算法变种
 * @date 2019/2/27 15:42
 **/
public class SnowFlakeWorker {

    /**
     * 起始的时间戳
     */
    private static final long START_TIMESTAMP = 1480166465631L;

    /**
     * 序列号占用的位数
     */
    private static final long SEQUENCE_BIT = 14;
    /**
     * 机器IP占用的位数
     */
    private static final long WORK_ID_BIT = 8;

    /**
     * 业务数据位
     */
    private static long TRANSACTION_BIT = 0;

    /**
     * 每一部分的最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private static long WORK_ID_LEFT = SEQUENCE_BIT + TRANSACTION_BIT;
    private static long TIMESTAMP_LEFT = WORK_ID_LEFT + WORK_ID_BIT;

    /**
     * 工作机器标识：IP地址
     */
    private long workId = 0x000000FF & getLastIP();

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上一次时间戳
     */
    private long lastStamp = -1L;

    /**
     * 左侧补齐
     */
    public static final String LEFT_PADDING = "0000000000000000";

    /**
     * 长度
     */
    private static final String PADDING_FORMAT = "%04d";
    private static final int BIZ_ID_LENGTH = 4;

    /**
     * 加入业务部分：指定业务位数
     * @param transactionBits 数据位
     */
    public SnowFlakeWorker(long transactionBits) {
        TRANSACTION_BIT = transactionBits;
        WORK_ID_LEFT = SEQUENCE_BIT + TRANSACTION_BIT;
        TIMESTAMP_LEFT = WORK_ID_LEFT + WORK_ID_BIT;
    }

    /**
     * 默认
     */
    public SnowFlakeWorker() {
    }

    /**
     * 带业务key编号
     * @param bizId 业务主键
     * @return NO
     */
    public synchronized String nextBizId(Long bizId) {
        String id = String.valueOf(nextId());
        String bizStr = String.format(PADDING_FORMAT, bizId);
        bizStr = bizStr.substring(bizStr.length() - BIZ_ID_LENGTH);
        return String.format("%s%s",id,bizStr);
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStamp = getNewstmp();
        if (currStamp < lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        this.genSeq(currStamp);

        lastStamp = currStamp;
        return (currStamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | workId << WORK_ID_LEFT
                | sequence;
    }

    public synchronized long nextId(long transactionId) {
        long currStmp = getNewstmp();
        if (currStmp < lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        String binaryId = Long.toBinaryString(transactionId);
        binaryId = LEFT_PADDING + binaryId;
        long processId = Long.parseLong(binaryId.substring((int) (binaryId.length() - TRANSACTION_BIT)), Character.MIN_RADIX);

        this.genSeq(currStmp);

        lastStamp = currStmp;
        return (currStmp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | workId << WORK_ID_LEFT
                | processId << TRANSACTION_BIT
                | sequence;
    }

    private void genSeq(long currStamp){
        if (currStamp == lastStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStamp = this.getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStamp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    protected byte getLastIP() {
        byte lastip = 0;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            byte[] ipByte = ip.getAddress();
            lastip = ipByte[ipByte.length - 1];
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return lastip;
    }

}
