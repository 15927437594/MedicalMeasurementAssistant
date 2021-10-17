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
    private boolean isDeviceOpen = false;
    private boolean isDeviceStart = false;
    private boolean mSaveSampleData = false;
    private List<Float> mOriginalData;
    private List<Float> mFilterData;

    private DeviceManager() {
        mOriginalData = new ArrayList<>();
        mFilterData = new ArrayList<>();
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
        LogUtils.i("replyStartDataCollect data=" + CalculateUtils.getHexStringList(data));
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
        if (first == 0x00 && second == 0x00 && third == 0x00) {
            return 0.0F;
        }
        long combine = CalculateUtils.threeLongCombine(first, second, third);
        long l = -(0xFFFFFF - combine + 1);
        return (float) (l * 400) / 0x7FFFFF;
    }

    public void analysisSampledData(List<Integer> data) {
        for (int i = 0; i < data.size() - 24; i += 24) {
            float pointChannel1 = calculateVoltage(data.get(i), data.get(i + 1), data.get(i + 2));
            float pointChannel2 = calculateVoltage(data.get(i + 3), data.get(i + 4), data.get(i + 5));
            float pointChannel3 = calculateVoltage(data.get(i + 6), data.get(i + 7), data.get(i + 8));
            float pointChannel4 = calculateVoltage(data.get(i + 9), data.get(i + 10), data.get(i + 11));
            float pointChannel5 = calculateVoltage(data.get(i + 12), data.get(i + 13), data.get(i + 14));
            float pointChannel6 = calculateVoltage(data.get(i + 15), data.get(i + 16), data.get(i + 17));
            float pointChannel7 = calculateVoltage(data.get(i + 18), data.get(i + 19), data.get(i + 20));
            float pointChannel8 = calculateVoltage(data.get(i + 21), data.get(i + 22), data.get(i + 23));
            if (isSaveSampleData()){
                mOriginalData.add(pointChannel1);
                mOriginalData.add(pointChannel2);
                mOriginalData.add(pointChannel3);
                mOriginalData.add(pointChannel4);
                mOriginalData.add(pointChannel5);
                mOriginalData.add(pointChannel6);
                mOriginalData.add(pointChannel7);
                mOriginalData.add(pointChannel8);

                mFilterData.add(pointChannel1);
                mFilterData.add(pointChannel2);
                mFilterData.add(pointChannel3);
                mFilterData.add(pointChannel4);
                mFilterData.add(pointChannel5);
                mFilterData.add(pointChannel6);
                mFilterData.add(pointChannel7);
                mFilterData.add(pointChannel8);
            }

            if (mDeviceInfoListener != null) {
                mDeviceInfoListener.replyVoltageData(1, pointChannel1);
                mDeviceInfoListener.replyVoltageData(2, pointChannel2);
                mDeviceInfoListener.replyVoltageData(3, pointChannel3);
                mDeviceInfoListener.replyVoltageData(4, pointChannel4);
                mDeviceInfoListener.replyVoltageData(5, pointChannel5);
                mDeviceInfoListener.replyVoltageData(6, pointChannel6);
                mDeviceInfoListener.replyVoltageData(7, pointChannel7);
                mDeviceInfoListener.replyVoltageData(8, pointChannel8);
            }
        }
    }

    public boolean isSaveSampleData() {
        return mSaveSampleData;
    }

    public void setSaveSampleData(boolean save) {
        this.mSaveSampleData = save;
    }

    public List<Float> getOriginalData() {
        return this.mOriginalData;
    }

    public void setOriginData(List<Float> data) {
        this.mOriginalData = data;
    }

    public List<Float> getFilterData() {
        return this.mFilterData;
    }

    public void setFilterData(List<Float> data) {
        this.mFilterData = data;
    }

    public boolean isDeviceStart() {
        return isDeviceStart;
    }

    public void setDeviceStart(boolean deviceStart) {
        isDeviceStart = deviceStart;
    }

    public boolean isDeviceOpen() {
        return isDeviceOpen;
    }

    public void setDeviceOpen(boolean deviceOpen) {
        isDeviceOpen = deviceOpen;
    }
}
