package cn.com.medicalmeasurementassistant.manager;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.medicalmeasurementassistant.entity.Constant;
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
    private Handler mHandler;
    private boolean isDeviceOpen = false;
    private boolean isDeviceStart = false;
    private boolean mSaveSampleData = false;
    private boolean mHighPassFilterState = true;
    private boolean mNotchFilterState = false;
    private List<Double> mOriginalData;
    private List<Double> mFilterData;
    private final double highPassFilterA0 = 1.0;
    private static final double highPassFilterA1 = -2.937170728449890;
    private static final double highPassFilterA2 = 2.876299723479331;
    private static final double highPassFilterA3 = -0.939098940325283;
    private static final double highPassFilterB0 = 0.969071174031813;
    private static final double highPassFilterB1 = -2.907213522095439;
    private static final double highPassFilterB2 = 2.907213522095439;
    private static final double highPassFilterB3 = -0.969071174031813;
    private static final double notchFilterA0 = 1.0;
    private static final double notchFilterA1 = -1.970651249848974;
    private static final double notchFilterA2 = 0.995215665562626;
    private static final double notchFilterB0 = 0.997607832781313;
    private static final double notchFilterB1 = -1.970651249848974;
    private static final double notchFilterB2 = 0.997607832781313;
    private static final int orderOfHighPassFilter = 3;
    private static final int orderOfNotchFilter = 2;
    private final Map<Integer, Boolean> mHighPassFilteredMap;
    private final Map<Integer, Boolean> mNotchFilteredMap;
    private final Map<Integer, List<Double>> lastFilteredDataMap;
    private final Map<Integer, List<Double>> lastFilteringDataMap;


    private DeviceManager() {
        mHighPassFilteredMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            mHighPassFilteredMap.put(i, false);
        }
        mNotchFilteredMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            mNotchFilteredMap.put(i, false);
        }

        List<Double> lastFilteredData = new ArrayList<>();
        lastFilteredDataMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            lastFilteredDataMap.put(i, lastFilteredData);
        }

        List<Double> lastFilteringData = new ArrayList<>();
        lastFilteringDataMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            lastFilteringDataMap.put(i, lastFilteringData);
        }
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

    public void replyDeviceStopped() {
        LogUtils.i("replyDeviceStopped");
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyDeviceStopped();
        }
    }

    public List<Double> getHighPassFilteredData(int channel, List<Double> srcData) {
        List<Double> filteredData;
        LogUtils.d("getHighPassFilteredData srcDataSize=" + srcData.size());
        boolean filtered = false;
        Boolean highPassFiltered = mHighPassFilteredMap.get(channel);
        if (highPassFiltered != null) {
            filtered = highPassFiltered;
        }
        List<Double> lastFilteredData = lastFilteredDataMap.get(channel);
        if (lastFilteredData == null) {
            return null;
        }
        List<Double> lastFilteringData = lastFilteringDataMap.get(channel);
        if (lastFilteringData == null) {
            return null;
        }
        if (filtered) {
            List<Double> channelFilteredData = new ArrayList<>(lastFilteredData);
            List<Double> channelFilteringData = new ArrayList<>(lastFilteringData);
            lastFilteredData.clear();
            lastFilteringData.clear();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0);
            }
            channelFilteringData.addAll(srcData);
            LogUtils.d("getFilterData channelFilteredDataSize=" + channelFilteredData.size());
            LogUtils.d("getFilterData channelFilteringData=" + channelFilteringData.size());
            for (int i = orderOfHighPassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    channelFilteredData.set(i, 0.0);
                } else {
                    double filteredPointChannelB = highPassFilterB0 * channelFilteringData.get(i) + highPassFilterB1 * channelFilteringData.get(i - 1) + highPassFilterB2 * channelFilteringData.get(i - 2) + highPassFilterB3 * channelFilteringData.get(i - 3);
                    double filteredPointChannelA = highPassFilterA1 * channelFilteredData.get(i - 1) + highPassFilterA2 * channelFilteredData.get(i - 2) + highPassFilterA3 * channelFilteredData.get(i - 3);
                    double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            for (int i = channelFilteredData.size() - orderOfHighPassFilter; i < channelFilteredData.size(); i++) {
                lastFilteredData.add(channelFilteredData.get(i));
                lastFilteringData.add(channelFilteringData.get(i));
            }
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighPassFilter, channelFilteredData.size()));
        } else {
            List<Double> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0);
            }

            for (int i = orderOfHighPassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    channelFilteredData.set(i, 0.0);
                } else {
                    double filteredPointChannelB = highPassFilterB0 * srcData.get(i) + highPassFilterB1 * srcData.get(i - 1) + highPassFilterB2 * srcData.get(i - 2) + highPassFilterB3 * srcData.get(i - 3);
                    double filteredPointChannelA = highPassFilterA1 * channelFilteredData.get(i - 1) + highPassFilterA2 * channelFilteredData.get(i - 2) + highPassFilterA3 * channelFilteredData.get(i - 3);
                    double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            for (int i = srcData.size() - orderOfHighPassFilter; i < channelFilteredData.size(); i++) {
                lastFilteredData.add(channelFilteredData.get(i));
                lastFilteringData.add(srcData.get(i));
            }
            mHighPassFilteredMap.put(channel, true);
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Double> getNotchFilteredData(int channel, List<Double> srcData) {
        List<Double> filteredData;
        LogUtils.d("getNotchFilteredData srcDataSize=" + srcData.size());
        boolean filtered = false;
        Boolean notchFiltered = mNotchFilteredMap.get(channel);
        if (notchFiltered != null) {
            filtered = notchFiltered;
        }
        List<Double> lastFilteredData = lastFilteredDataMap.get(channel);
        if (lastFilteredData == null) {
            return null;
        }
        List<Double> lastFilteringData = lastFilteringDataMap.get(channel);
        if (lastFilteringData == null) {
            return null;
        }
        if (filtered) {
            List<Double> channelFilteredData = new ArrayList<>(lastFilteredData);
            List<Double> channelFilteringData = new ArrayList<>(lastFilteringData);
            lastFilteredData.clear();
            lastFilteringData.clear();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0);
            }
            channelFilteringData.addAll(srcData);
            LogUtils.d("getFilterData channelFilteredDataSize=" + channelFilteredData.size());
            LogUtils.d("getFilterData channelFilteringData=" + channelFilteringData.size());
            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                double filteredPointChannelB = notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2);
                double filteredPointChannelA = notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2);
                double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                channelFilteredData.set(i, filteredPointChannel);
            }

            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                lastFilteredData.add(channelFilteredData.get(i));
                lastFilteringData.add(channelFilteringData.get(i));
            }
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Double> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                double filteredPointChannelB = notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2);
                double filteredPointChannelA = notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2);
                double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                channelFilteredData.set(i, filteredPointChannel);
            }
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                lastFilteredData.add(channelFilteredData.get(i));
                lastFilteringData.add(srcData.get(i));
            }
            mNotchFilteredMap.put(channel, true);
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public void replySampledData(List<Integer> data) {
        LogUtils.d("replySampledData data=" + CalculateUtils.getHexStringList(data));
        Map<Integer, List<Double>> map = new HashMap<>();
        List<Double> channel1Data = new ArrayList<>();
        List<Double> channel2Data = new ArrayList<>();
        List<Double> channel3Data = new ArrayList<>();
        List<Double> channel4Data = new ArrayList<>();
        List<Double> channel5Data = new ArrayList<>();
        List<Double> channel6Data = new ArrayList<>();
        List<Double> channel7Data = new ArrayList<>();
        List<Double> channel8Data = new ArrayList<>();

        for (int i = 0; i < data.size() - 24; i += 24) {
            double pointChannel1 = calculateVoltage(data.get(i), data.get(i + 1), data.get(i + 2));
            double pointChannel2 = calculateVoltage(data.get(i + 3), data.get(i + 4), data.get(i + 5));
            double pointChannel3 = calculateVoltage(data.get(i + 6), data.get(i + 7), data.get(i + 8));
            double pointChannel4 = calculateVoltage(data.get(i + 9), data.get(i + 10), data.get(i + 11));
            double pointChannel5 = calculateVoltage(data.get(i + 12), data.get(i + 13), data.get(i + 14));
            double pointChannel6 = calculateVoltage(data.get(i + 15), data.get(i + 16), data.get(i + 17));
            double pointChannel7 = calculateVoltage(data.get(i + 18), data.get(i + 19), data.get(i + 20));
            double pointChannel8 = calculateVoltage(data.get(i + 21), data.get(i + 22), data.get(i + 23));
            if (isSaveSampleData()) {
                mOriginalData.add(pointChannel1);
                mOriginalData.add(pointChannel2);
                mOriginalData.add(pointChannel3);
                mOriginalData.add(pointChannel4);
                mOriginalData.add(pointChannel5);
                mOriginalData.add(pointChannel6);
                mOriginalData.add(pointChannel7);
                mOriginalData.add(pointChannel8);
            }

            channel1Data.add(pointChannel1);
            channel2Data.add(pointChannel2);
            channel3Data.add(pointChannel3);
            channel4Data.add(pointChannel4);
            channel5Data.add(pointChannel5);
            channel6Data.add(pointChannel6);
            channel7Data.add(pointChannel7);
            channel8Data.add(pointChannel8);
        }
        map.put(0, channel1Data);
        map.put(1, channel2Data);
        map.put(2, channel3Data);
        map.put(3, channel4Data);
        map.put(4, channel5Data);
        map.put(5, channel6Data);
        map.put(6, channel7Data);
        map.put(7, channel8Data);

        if (getHighPassFilterState()) {
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = map.get(i);
                if (channelData != null) {
                    List<Double> filteredData = getHighPassFilteredData(i, channelData);
                    LogUtils.d(String.format("filteredData i=%s, filteredData=%s", i, filteredData));
                    LogUtils.d("filteredData=" + filteredData.size());
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyVoltage(i, filteredData);
                    }
                }
            }
        } else if (getNotchFilterState()) {
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = map.get(i);
                if (channelData != null) {
                    List<Double> filteredData = getNotchFilteredData(i, channelData);
                    LogUtils.d(String.format("filteredData i=%s, filteredData=%s", i, filteredData));
                    LogUtils.d("filteredData=" + filteredData.size());
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyVoltage(i, filteredData);
                    }
                }
            }
        } else {
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = map.get(i);
                if (channelData != null) {
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyVoltage(i, channelData);
                    }
                }
            }
        }
        List<Integer> capacitanceData = new ArrayList<>(data.subList(960, 964));
        replyCapacitanceData(CalculateUtils.integerListToBytes(capacitanceData));
    }

    public void replyCapacitanceData(byte[] data) {
        float capacitance = CalculateUtils.getFloat(data, 0) - 69.7F;
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyCapacitance(capacitance);
        }
    }

    public void replyStopDataCollect(List<Integer> data) {
        LogUtils.d("replyStopDataCollect data=" + CalculateUtils.getHexStringList(data));
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyStopDataCollect(data);
        }
    }

    public void replyChannelVoltage(int channel, List<Double> data) {
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyVoltage(channel, data);
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


    public void analysisSampledData2(List<Integer> data) {
        Map<Integer, List<Double>> map = new HashMap<>();
        ArrayList<Double> objects = new ArrayList<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            map.put(i, objects);
        }
        for (int i = 0; i < data.size() - 24; i += 24) {
            for (int m = 0; m < 8; m++) {
                double pointChannel = calculateVoltage(data.get(m * 3 + i), data.get(m * 3 + i + 1), data.get(m * 3 + i + 2));
                if (isSaveSampleData()) {
                    mOriginalData.add(pointChannel);
                    mFilterData.add(pointChannel);
                }
                List<Double> doubles = map.get(i);
                if (doubles != null) {
                    doubles.add(pointChannel);
                }
            }
        }

        List<Integer> capacitanceData = new ArrayList<>(data.subList(960, 964));
        replyCapacitanceData(CalculateUtils.integerListToBytes(capacitanceData));
    }

    public boolean isSaveSampleData() {
        return mSaveSampleData;
    }

    public void setSaveSampleData(boolean save) {
        this.mSaveSampleData = save;
    }

    public List<Double> getOriginalData() {
        return this.mOriginalData;
    }

    public void setOriginData(List<Double> data) {
        this.mOriginalData = data;
    }

    public List<Double> getHighPassFilterData() {
        return this.mFilterData;
    }

    public void setFilterData(List<Double> data) {
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

    public boolean getHighPassFilterState() {
        return mHighPassFilterState;
    }

    public void setHighPassFilterState(boolean state) {
        this.mHighPassFilterState = state;
    }

    public boolean getNotchFilterState() {
        return mNotchFilterState;
    }

    public void setNotchFilterState(boolean state) {
        this.mNotchFilterState = state;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}
