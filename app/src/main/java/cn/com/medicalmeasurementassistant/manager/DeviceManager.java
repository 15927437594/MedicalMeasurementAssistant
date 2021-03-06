package cn.com.medicalmeasurementassistant.manager;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.listener.CalibrateListener;
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
    private CalibrateListener mCalibrateListener = null;
    private boolean isDeviceOpen = false;
    private boolean isDeviceStart = false;
    private boolean mSaveDataState = false;
    private boolean mHighPassFilterState = true;
    private boolean mNotchFilterState = false;
    private List<String> mOriginalData;
    private List<String> mFilterData;
    private static final double highPassFilterA0 = 1.0;
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
    private double angle1 = 0;
    private double angle2 = 0;
    private double p1 = 0;
    private double p2 = 0;
    private double mCurrentCapacitance = 0;
    private boolean mCalibrateState = false;
    private final Map<Integer, List<Double>> mChannelDataMap;
    private static final long EMG_UPDATE_INTERVAL = 200L;
    private static final long CAP_UPDATE_INTERVAL = 50L;
    private final Map<Integer, Long> mUpdateTimeMap;
    private int mSaveTime = 0;

    public void resetParams() {
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            mHighPassFilteredMap.put(i, false);
        }

        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            mNotchFilteredMap.put(i, false);
        }

        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            List<Double> doubles = lastFilteredDataMap.get(i);
            if (doubles != null) {
                doubles.clear();
            }
        }

        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            List<Double> doubles = lastFilteringDataMap.get(i);
            if (doubles != null) {
                doubles.clear();
            }
        }

        mOriginalData.clear();
        mFilterData.clear();
    }

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

        mChannelDataMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            mChannelDataMap.put(i, new ArrayList<>());
        }

        mOriginalData = new ArrayList<>();
        mFilterData = new ArrayList<>();

        mUpdateTimeMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL + 1; i++) {
            mUpdateTimeMap.put(i, 0L);
        }
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

    public void setCalibrateListener(CalibrateListener listener) {
        this.mCalibrateListener = listener;
    }

    public void calibrateSuccess() {
        if (mCalibrateListener != null) {
            mCalibrateListener.calibrateSuccess();
        }
    }

    public void calibrateFail() {
        if (mCalibrateListener != null) {
            mCalibrateListener.calibrateFail();
        }
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

    // ????????????????????????
    public List<Double> getHighPassFilteredData(int channel, List<Double> srcData) {
        List<Double> filteredData;
        LogUtils.d("getHighPassFilteredData srcDataSize=" + srcData.size());
        // ??????channel???????????????
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
        LogUtils.i("lastFilteringData=" + lastFilteringData);
        if (lastFilteringData == null) {
            return null;
        }
        if (filtered) {
            List<Double> channelFilteredData = new ArrayList<>(lastFilteredData);
            List<Double> channelFilteringData = new ArrayList<>(lastFilteringData);
            LogUtils.i("channelFilteredData=" + channelFilteredData);
            LogUtils.i("channelFilteringData=" + channelFilteringData);
            // ?????????????????????????????????????????????????????????,???????????????,??????????????????????????????????????????????????????
            lastFilteredData.clear();
            lastFilteringData.clear();
            // ?????????channel??????????????????
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0);
            }
            // ?????????????????????,???????????????????????????????????????????????????????????????3 + ?????????????????????40 = 43
            channelFilteringData.addAll(srcData);
            if (channel == 0) {
                LogUtils.i("channelFilteredData=" + channelFilteredData);
                LogUtils.i("channelFilteringData=" + channelFilteringData);
            }
            LogUtils.d("getFilterData channelFilteredDataSize=" + channelFilteredData.size());
            LogUtils.d("getFilterData channelFilteringDataSize=" + channelFilteringData.size());
            for (int i = orderOfHighPassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //???????????????0?????????
                    channelFilteredData.set(i, 0.0);
                } else {
                    double filteredPointChannelB = highPassFilterB0 * channelFilteringData.get(i) + highPassFilterB1 * channelFilteringData.get(i - 1) + highPassFilterB2 * channelFilteringData.get(i - 2) + highPassFilterB3 * channelFilteringData.get(i - 3);
                    double filteredPointChannelA = highPassFilterA1 * channelFilteredData.get(i - 1) + highPassFilterA2 * channelFilteredData.get(i - 2) + highPassFilterA3 * channelFilteredData.get(i - 3);
                    double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    LogUtils.i("getHighPassFilteredData filteredPointChannel=" + filteredPointChannel);
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // ?????????????????????????????????????????????????????????????????????,????????????????????????????????????
            for (int i = channelFilteredData.size() - orderOfHighPassFilter; i < channelFilteredData.size(); i++) {
                lastFilteredData.add(channelFilteredData.get(i));
                lastFilteringData.add(channelFilteringData.get(i));
            }
            //?????????????????????????????????????????????????????????
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighPassFilter, channelFilteredData.size()));
        } else {
            List<Double> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0);
            }

            if (channel == 0) {
                LogUtils.i("channelFilteringData=" + srcData);
            }

            for (int i = orderOfHighPassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //???????????????0?????????
                    channelFilteredData.set(i, 0.0);
                } else {
                    // ???????????????????????????,??????????????????????????????????????????
                    double filteredPointChannelB = highPassFilterB0 * srcData.get(i) + highPassFilterB1 * srcData.get(i - 1) + highPassFilterB2 * srcData.get(i - 2) + highPassFilterB3 * srcData.get(i - 3);
                    LogUtils.i("getHighPassFilteredData filteredPointChannelB=" + filteredPointChannelB);
                    double filteredPointChannelA = highPassFilterA1 * channelFilteredData.get(i - 1) + highPassFilterA2 * channelFilteredData.get(i - 2) + highPassFilterA3 * channelFilteredData.get(i - 3);
                    LogUtils.i("getHighPassFilteredData filteredPointChannelA=" + filteredPointChannelA);
                    double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    LogUtils.i("getHighPassFilteredData filteredPointChannel=" + filteredPointChannel);
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            lastFilteredData.clear();
            lastFilteringData.clear();

            // ?????????????????????????????????????????????????????????????????????,????????????????????????????????????
            for (int i = srcData.size() - orderOfHighPassFilter; i < channelFilteredData.size(); i++) {
                lastFilteredData.add(channelFilteredData.get(i));
                lastFilteringData.add(srcData.get(i));
            }

            // ??????channel??????????????????true
            mHighPassFilteredMap.put(channel, true);
            filteredData = new ArrayList<>(channelFilteredData);
            LogUtils.i("getHighPassFilteredData filteredData=" + filteredData);
        }
        return filteredData;
    }

    // ????????????????????????(?????????????????????????????????,?????????????????????)
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
                if (channelFilteringData.get(i) == 0.0) {
                    //???????????????0?????????
                    channelFilteredData.set(i, 0.0);
                } else {
                    double filteredPointChannelB = notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2);
                    double filteredPointChannelA = notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2);
                    double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
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
                if (srcData.get(i) == 0.0) {
                    //???????????????0?????????
                    channelFilteredData.set(i, 0.0);
                } else {
                    double filteredPointChannelB = notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2);
                    double filteredPointChannelA = notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2);
                    double filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
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

//    @SuppressLint("DefaultLocale")
    public void replySampledData(List<Integer> data) {
        LogUtils.d("replySampledData startTime=" + System.currentTimeMillis());
        List<Integer> capacitanceData = new ArrayList<>(data.subList(960, 964));
        replyCapacitanceData(CalculateUtils.integerListToBytes(capacitanceData));

        // ??????mChannelDataMap????????????????????????
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            List<Double> channelData = mChannelDataMap.get(i);
            if (channelData != null) {
                channelData.clear();
            }
        }

        LogUtils.d("replySampledData data=" + CalculateUtils.getHexStringList(data));
        // defaultChannelSize: ??????8???????????????????????????????????????
        int defaultChannelsSize = 3 * Constant.DEFAULT_CHANNEL;
        for (int i = 0; i < data.size() - defaultChannelsSize; i += defaultChannelsSize) {
            for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                double voltage = calculateVoltage(data.get(j * 3 + i), data.get(j * 3 + i + 1), data.get(j * 3 + i + 2));
                if (getSaveDataState()) {
                    mOriginalData.add(String.valueOf(voltage));
                }
                List<Double> channelData = mChannelDataMap.get(j);
                if (channelData != null) {
                    channelData.add(voltage);
                }
            }
            if (getSaveDataState()) {
                mOriginalData.add(String.valueOf(mCurrentCapacitance));
                mOriginalData.add("\n");
            }
        }

        if (getHighPassFilterState() && !getNotchFilterState()) {
            Map<Integer, List<Double>> filteredDataMap = new HashMap<>();
            // ??????????????????
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = mChannelDataMap.get(i);
                LogUtils.i(String.format("i=%d, channelData=%s", i, channelData));
                if (channelData != null) {
                    LogUtils.i(String.format("i=%d, channelData=%s", i, channelData.size()));
                    List<Double> filteredData = getHighPassFilteredData(i, channelData);
                    filteredDataMap.put(i, filteredData);

                    Long updateTime = mUpdateTimeMap.get(i);
                    if (updateTime != null) {
                        if (Math.abs(System.currentTimeMillis() - updateTime) > EMG_UPDATE_INTERVAL) {
                            mUpdateTimeMap.put(i, System.currentTimeMillis());
                            if (mDeviceInfoListener != null) {
                                mDeviceInfoListener.replyVoltage(i, filteredData);
                            }
                        }
                    }
                }
            }

            if (getSaveDataState()) {
                // ??????????????????8????????????????????????
                int channelsCount = data.size() / defaultChannelsSize;
                for (int i = 0; i < channelsCount; i++) {
                    for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                        List<Double> doubles = filteredDataMap.get(j);
                        if (doubles != null) {
                            mFilterData.add(String.valueOf(doubles.get(i)));
                        }
                    }
                    mFilterData.add(String.valueOf(mCurrentCapacitance));
                    mFilterData.add("\n");
                }
            }
        } else if (getNotchFilterState() && !getHighPassFilterState()) {
            Map<Integer, List<Double>> filteredDataMap = new HashMap<>();
            // ??????????????????
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = mChannelDataMap.get(i);
                if (channelData != null) {
                    List<Double> filteredData = getNotchFilteredData(i, channelData);
                    filteredDataMap.put(i, filteredData);

                    Long updateTime = mUpdateTimeMap.get(i);
                    if (updateTime != null) {
                        if (Math.abs(System.currentTimeMillis() - updateTime) > EMG_UPDATE_INTERVAL) {
                            mUpdateTimeMap.put(i, System.currentTimeMillis());
                            if (mDeviceInfoListener != null) {
                                mDeviceInfoListener.replyVoltage(i, filteredData);
                            }
                        }
                    }
                }
            }

            if (getSaveDataState()) {
                // ??????????????????8????????????????????????
                int channelsCount = data.size() / defaultChannelsSize;
                for (int i = 0; i < channelsCount; i++) {
                    for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                        List<Double> doubles = filteredDataMap.get(j);
                        if (doubles != null) {
                            mFilterData.add(String.valueOf(doubles.get(i)));
                        }
                    }
                    mFilterData.add(String.valueOf(mCurrentCapacitance));
                    mFilterData.add("\n");
                }
            }
        } else if (getHighPassFilterState() && getNotchFilterState()) {
            Map<Integer, List<Double>> filteredDataMap = new HashMap<>();
            // ???????????????????????????????????????,?????????????????????,?????????????????????????????????????????????????????????
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = mChannelDataMap.get(i);
                if (channelData != null) {
                    List<Double> highPassFilteredData = getHighPassFilteredData(i, channelData);
                    if (highPassFilteredData != null) {
                        List<Double> filteredData = getNotchFilteredData(i, highPassFilteredData);
                        filteredDataMap.put(i, filteredData);

                        Long updateTime = mUpdateTimeMap.get(i);
                        if (updateTime != null) {
                            if (Math.abs(System.currentTimeMillis() - updateTime) > EMG_UPDATE_INTERVAL) {
                                mUpdateTimeMap.put(i, System.currentTimeMillis());
                                if (mDeviceInfoListener != null) {
                                    mDeviceInfoListener.replyVoltage(i, filteredData);
                                }
                            }
                        }
                    }
                }
            }

            if (getSaveDataState()) {
                // ??????????????????8????????????????????????
                int channelsCount = data.size() / defaultChannelsSize;
                for (int i = 0; i < channelsCount; i++) {
                    for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                        List<Double> doubles = filteredDataMap.get(j);
                        if (doubles != null) {
                            mFilterData.add(String.valueOf(doubles.get(i)));
                        }
                    }
                    mFilterData.add(String.valueOf(mCurrentCapacitance));
                    mFilterData.add("\n");
                }
            }
        } else {
            // ????????????????????????
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Double> channelData = mChannelDataMap.get(i);
                Long updateTime = mUpdateTimeMap.get(i);
                if (updateTime != null && channelData != null) {
                    if (Math.abs(System.currentTimeMillis() - updateTime) > EMG_UPDATE_INTERVAL) {
                        mUpdateTimeMap.put(i, System.currentTimeMillis());
                        if (mDeviceInfoListener != null) {
                            mDeviceInfoListener.replyVoltage(i, channelData);
                        }
                    }
                }
            }
        }

        LogUtils.d("replySampledData startTime=" + System.currentTimeMillis());
    }

    public void replyCapacitanceData(byte[] data) {
        double capacitance = (double) (Math.round((CalculateUtils.getFloat(data, 0) - Constant.DEFAULT_CAPACITANCE) * 100)) / 100;
        LogUtils.d("replyCapacitanceData capacitance=" + capacitance);
        if (capacitance < 0) {
            capacitance = 0;
        }
        mCurrentCapacitance = capacitance;

        Long updateTime = mUpdateTimeMap.get(Constant.DEFAULT_CHANNEL);
        if (updateTime != null) {
            if (Math.abs(System.currentTimeMillis() - updateTime) > CAP_UPDATE_INTERVAL) {
                mUpdateTimeMap.put(Constant.DEFAULT_CHANNEL, System.currentTimeMillis());
                if (getCalibrateState()) {
                    double angle = convertCapacitanceToAngle(capacitance);
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyAngle(angle);
                    }
                } else {
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyCapacitance(capacitance);
                    }
                }
            }
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
        float voltage;
        if (first >= 0x80) {
            // ???????????????????????????????????????8 ???????????????(??????????????????0x7F)
            long l = -(0xFFFFFF - combine + 1);
            voltage = (float) (l * Constant.DEFAULT_RANGE) / 0x7FFFFF;
        } else {
            voltage = (float) (combine * Constant.DEFAULT_RANGE) / 0x7FFFFF;
        }

        return voltage;
    }

    public boolean getSaveDataState() {
        return mSaveDataState;
    }

    public void setSaveDataState(boolean save) {
        this.mSaveDataState = save;
    }

    public List<String> getOriginalData() {
        return this.mOriginalData;
    }

    public void setOriginData(List<String> data) {
        this.mOriginalData = data;
    }

    public List<String> getFilterData() {
        return this.mFilterData;
    }

    public void setFilterData(List<String> data) {
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

    public double convertCapacitanceToAngle(double capacitance) {
        double srcAngle = (angle1 - angle2) / (p1 - p2) * capacitance + angle1 - p1 * (angle1 - angle2) / (p1 - p2);
        return (double) (Math.round((srcAngle) * 100)) / 100;
    }

    public double getP1() {
        return this.p1;
    }

    public void setP1(double p) {
        this.p1 = p;
    }

    public double getP2() {
        return this.p2;
    }

    public void setP2(double p) {
        this.p2 = p;
    }

    public double getAngle1() {
        return this.angle1;
    }

    public void setAngle1(double angle) {
        this.angle1 = angle;
    }

    public double getAngle2() {
        return this.angle2;
    }

    public void setAngle2(double angle) {
        this.angle2 = angle;
    }

    public double getCurrentCapacitance() {
        return this.mCurrentCapacitance;
    }

    public void setCurrentCapacitance(double value) {
        this.mCurrentCapacitance = value;
    }

    public void resetCalibrate() {
        this.angle1 = 0;
        this.angle2 = 0;
        this.p1 = 0;
        this.p2 = 0;
    }

    public boolean getCalibrateState() {
        return mCalibrateState;
    }

    public void setCalibrateState(boolean mCalibrateState) {
        this.mCalibrateState = mCalibrateState;
    }

    public int getSaveTime() {
        return this.mSaveTime;
    }

    public void setSaveTime(int value) {
        this.mSaveTime = value;
    }

    public boolean getFilterState() {
        return this.mHighPassFilterState || this.mNotchFilterState;
    }

}
