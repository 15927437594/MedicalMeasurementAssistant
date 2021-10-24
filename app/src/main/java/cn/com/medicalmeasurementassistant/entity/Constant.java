package cn.com.medicalmeasurementassistant.entity;

/**
 * user: Created by DuJi on 2021/8/25 21:21
 * email: 18571762595@13.com
 * description:
 */
public class Constant {
    public static final int SOURCE_APP = 0x01;
    public static final int SOURCE_MEASURE_DEVICE = 0x02;
    public static final int CATEGORY_COMMON = 0x01;
    public static final int CATEGORY_DATA_COLLECT = 0x04;
    public static final int FUNCTION_REPLY_HANDSHAKE_SIGNAL = 0x05;
    public static final int FUNCTION_REPLY_START_DATA_COLLECT = 0x02;
    public static final int FUNCTION_REPLY_SAMPLED_DATA = 0x03;
    public static final int FUNCTION_REPLY_STOP_DATA_COLLECT = 0x05;
    public static final int TCP_SERVER_PORT = 8081;

    /**
     *  默认量程
     */
    public static final int DEFAULT_ANGLE = 400;
    public static final int DEFAULT_CHANNEL = 8;
    public static final int MSG_WHAT_DISTRIBUTE_VOLTAGE = 0;
}
