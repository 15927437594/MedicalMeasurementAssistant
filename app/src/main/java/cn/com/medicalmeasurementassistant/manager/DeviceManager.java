package cn.com.medicalmeasurementassistant.manager;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.listener.DeviceInfoListener;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;
import cn.com.medicalmeasurementassistant.utils.LogUtils;

/**
 * user: Created by DuJi on 2021/9/25 16:53
 * email: 18571762595@13.com
 * description:
 */
public class DeviceManager {

    public static volatile DeviceManager sInstance = null;
    private DeviceInfoListener mDeviceInfoListener = null;
    private final List<Float> channel1;
    private final List<Float> channel2;
    private final List<Float> channel3;
    private final List<Float> channel4;
    private final List<Float> channel5;
    private final List<Float> channel6;
    private final List<Float> channel7;
    private final List<Float> channel8;


    private DeviceManager() {
        channel1 = new ArrayList<>();
        channel2 = new ArrayList<>();
        channel3 = new ArrayList<>();
        channel4 = new ArrayList<>();
        channel5 = new ArrayList<>();
        channel6 = new ArrayList<>();
        channel7 = new ArrayList<>();
        channel8 = new ArrayList<>();

    }

    public static DeviceManager getInstance() {
        if (null == sInstance) {
            synchronized (DeviceManager.class) {
                if (null == sInstance) {
                    sInstance = new DeviceManager();
                }
            }
        }
        return sInstance;
    }

    public void setDeviceInfoListener(DeviceInfoListener listener) {
        this.mDeviceInfoListener = listener;
    }

    public void replyHandshake(List<Integer> data) {
        LogUtils.d("replyStartDataCollect data=" + CalculateUtils.getHexStringList(data));
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyHandshake(data);
        }
    }

    public void replyStartDataCollect(List<Integer> data) {
        LogUtils.d("replyStartDataCollect data=" + CalculateUtils.getHexStringList(data));
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyStartDataCollect(data);
        }
    }

    public void replySampledData(List<Integer> data) {
        LogUtils.d("replySampledData data=" + CalculateUtils.getHexStringList(data));
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replySampledData(data);
        }
        analysisSampledData(data);
    }

    public void replyStopDataCollect(List<Integer> data) {
        LogUtils.d("replyStopDataCollect data=" + CalculateUtils.getHexStringList(data));
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyStopDataCollect(data);
        }
    }

    public float calculateVoltage(int first, int second, int third) {
        long combine = CalculateUtils.threeLongCombine(first, second, third);
        long l = -(0xFFFFFF - combine + 1);
        return (float) (l * 400) / 0x7FFFFF;
    }

    public void analysisSampledData(List<Integer> data) {
        for (int i = 0; i < data.size() - 24; i += 24) {
            channel1.add(calculateVoltage(data.get(i), data.get(i + 1), data.get(i + 2)));
            channel2.add(calculateVoltage(data.get(i + 3), data.get(i + 4), data.get(i + 5)));
            channel3.add(calculateVoltage(data.get(i + 6), data.get(i + 7), data.get(i + 8)));
            channel4.add(calculateVoltage(data.get(i + 9), data.get(i + 10), data.get(i + 11)));
            channel5.add(calculateVoltage(data.get(i + 12), data.get(i + 13), data.get(i + 14)));
            channel6.add(calculateVoltage(data.get(i + 15), data.get(i + 16), data.get(i + 17)));
            channel7.add(calculateVoltage(data.get(i + 18), data.get(i + 19), data.get(i + 20)));
            channel8.add(calculateVoltage(data.get(i + 21), data.get(i + 22), data.get(i + 23)));
        }

        LogUtils.i("channel1=" + channel1);
        LogUtils.i("channel2=" + channel2);
        LogUtils.i("channel3=" + channel3);
        LogUtils.i("channel4=" + channel4);
        LogUtils.i("channel5=" + channel5);
        LogUtils.i("channel6=" + channel6);
        LogUtils.i("channel7=" + channel7);
        LogUtils.i("channel8=" + channel8);

        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyVoltageData(1,channel1);
        }

        if (channel1.size() > 3 * 1000) {
            channel1.clear();
        }
        if (channel2.size() > 3 * 1000) {
            channel2.clear();
        }
        if (channel3.size() > 3 * 1000) {
            channel3.clear();
        }
        if (channel4.size() > 3 * 1000) {
            channel4.clear();
        }
        if (channel5.size() > 3 * 1000) {
            channel5.clear();
        }
        if (channel6.size() > 3 * 1000) {
            channel6.clear();
        }
        if (channel7.size() > 3 * 1000) {
            channel7.clear();
        }
        if (channel8.size() > 3 * 1000) {
            channel8.clear();
        }
    }
}
