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
     * 默认量程
     */
    public static final int DEFAULT_RANGE = 400;
    public static final int DEFAULT_CHANNEL = 8;

    /**
     * 首页设置类型
     * SETTING_TYPE_TIME_LENGTH 时间范围
     * SETTING_TYPE_EMG_SCALE_RANGE EMG刻度
     * SETTING_TYPE_CAP_SCALE_RANGE 电容刻度
     */
    public static final int SETTING_TYPE_TIME_LENGTH = 1;
    public static final int SETTING_TYPE_EMG_SCALE_RANGE = 2;
    public static final int SETTING_TYPE_CAP_SCALE_RANGE = 3;
    public static final int SETTING_TYPE_RECORD_CAPTURE_TIME = 4;
    public static final double DEFAULT_CAPACITANCE = 34.4;

}
