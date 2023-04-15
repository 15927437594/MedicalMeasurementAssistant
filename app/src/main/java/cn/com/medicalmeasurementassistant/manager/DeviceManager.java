package cn.com.medicalmeasurementassistant.manager;

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
    private boolean mHighpassFilterState = true;
    private boolean mNotchFilterState = false;
    private List<String> mOriginalData;
    private List<String> mFilterData;
    private static final double highpassFilterA0 = 1.0;
    private static final double highpassFilterA1 = -2.937170728449890;
    private static final double highpassFilterA2 = 2.876299723479331;
    private static final double highpassFilterA3 = -0.939098940325283;
    private static final double highpassFilterB0 = 0.969071174031813;
    private static final double highpassFilterB1 = -2.907213522095439;
    private static final double highpassFilterB2 = 2.907213522095439;
    private static final double highpassFilterB3 = -0.969071174031813;
    private static final double notchFilterA0 = 1.0;
    private static final double notchFilterA1 = -1.970651249848974;
    private static final double notchFilterA2 = 0.995215665562626;
    private static final double notchFilterB0 = 0.997607832781313;
    private static final double notchFilterB1 = -1.970651249848974;
    private static final double notchFilterB2 = 0.997607832781313;
    private static final int orderOfHighpassFilter = 3;
    private static final int orderOfNotchFilter = 2;
    private boolean highpassChannel1Filtered = false;
    private boolean highpassChannel2Filtered = false;
    private boolean highpassChannel3Filtered = false;
    private boolean highpassChannel4Filtered = false;
    private boolean highpassChannel5Filtered = false;
    private boolean highpassChannel6Filtered = false;
    private boolean highpassChannel7Filtered = false;
    private boolean highpassChannel8Filtered = false;
    private final List<Float> highpassChannel1LastFilteredData;
    private final List<Float> highpassChannel1LastFilteringData;
    private final List<Float> highpassChannel2LastFilteredData;
    private final List<Float> highpassChannel2LastFilteringData;
    private final List<Float> highpassChannel3LastFilteredData;
    private final List<Float> highpassChannel3LastFilteringData;
    private final List<Float> highpassChannel4LastFilteredData;
    private final List<Float> highpassChannel4LastFilteringData;
    private final List<Float> highpassChannel5LastFilteredData;
    private final List<Float> highpassChannel5LastFilteringData;
    private final List<Float> highpassChannel6LastFilteredData;
    private final List<Float> highpassChannel6LastFilteringData;
    private final List<Float> highpassChannel7LastFilteredData;
    private final List<Float> highpassChannel7LastFilteringData;
    private final List<Float> highpassChannel8LastFilteredData;
    private final List<Float> highpassChannel8LastFilteringData;
    private boolean notchChannel1Filtered = false;
    private boolean notchChannel2Filtered = false;
    private boolean notchChannel3Filtered = false;
    private boolean notchChannel4Filtered = false;
    private boolean notchChannel5Filtered = false;
    private boolean notchChannel6Filtered = false;
    private boolean notchChannel7Filtered = false;
    private boolean notchChannel8Filtered = false;
    private final List<Float> notchChannel1LastFilteredData;
    private final List<Float> notchChannel1LastFilteringData;
    private final List<Float> notchChannel2LastFilteredData;
    private final List<Float> notchChannel2LastFilteringData;
    private final List<Float> notchChannel3LastFilteredData;
    private final List<Float> notchChannel3LastFilteringData;
    private final List<Float> notchChannel4LastFilteredData;
    private final List<Float> notchChannel4LastFilteringData;
    private final List<Float> notchChannel5LastFilteredData;
    private final List<Float> notchChannel5LastFilteringData;
    private final List<Float> notchChannel6LastFilteredData;
    private final List<Float> notchChannel6LastFilteringData;
    private final List<Float> notchChannel7LastFilteredData;
    private final List<Float> notchChannel7LastFilteringData;
    private final List<Float> notchChannel8LastFilteredData;
    private final List<Float> notchChannel8LastFilteringData;
    private float angle1 = 0;
    private float angle2 = 0;
    private float p1 = 0;
    private float p2 = 0;
    private float mCurrentCapacitance = 0;
    private boolean mCalibrateState = false;
    private final Map<Integer, List<Float>> mChannelDataMap;
    private int mSaveTime = 0;

    public void resetParams() {
        mOriginalData.clear();
        mFilterData.clear();
        highpassChannel1Filtered = false;
        highpassChannel2Filtered = false;
        highpassChannel3Filtered = false;
        highpassChannel4Filtered = false;
        highpassChannel5Filtered = false;
        highpassChannel6Filtered = false;
        highpassChannel7Filtered = false;
        highpassChannel8Filtered = false;
        highpassChannel1LastFilteredData.clear();
        highpassChannel1LastFilteringData.clear();
        highpassChannel2LastFilteredData.clear();
        highpassChannel2LastFilteringData.clear();
        highpassChannel3LastFilteredData.clear();
        highpassChannel3LastFilteringData.clear();
        highpassChannel4LastFilteredData.clear();
        highpassChannel4LastFilteringData.clear();
        highpassChannel5LastFilteredData.clear();
        highpassChannel5LastFilteringData.clear();
        highpassChannel6LastFilteredData.clear();
        highpassChannel6LastFilteringData.clear();
        highpassChannel7LastFilteredData.clear();
        highpassChannel7LastFilteringData.clear();
        highpassChannel8LastFilteredData.clear();
        highpassChannel8LastFilteringData.clear();
        notchChannel1Filtered = false;
        notchChannel2Filtered = false;
        notchChannel3Filtered = false;
        notchChannel4Filtered = false;
        notchChannel5Filtered = false;
        notchChannel6Filtered = false;
        notchChannel7Filtered = false;
        notchChannel8Filtered = false;
        notchChannel1LastFilteredData.clear();
        notchChannel1LastFilteringData.clear();
        notchChannel2LastFilteredData.clear();
        notchChannel2LastFilteringData.clear();
        notchChannel3LastFilteredData.clear();
        notchChannel3LastFilteringData.clear();
        notchChannel4LastFilteredData.clear();
        notchChannel4LastFilteringData.clear();
        notchChannel5LastFilteredData.clear();
        notchChannel5LastFilteringData.clear();
        notchChannel6LastFilteredData.clear();
        notchChannel6LastFilteringData.clear();
        notchChannel7LastFilteredData.clear();
        notchChannel7LastFilteringData.clear();
        notchChannel8LastFilteredData.clear();
        notchChannel8LastFilteringData.clear();
    }

    private DeviceManager() {
        mChannelDataMap = new HashMap<>();
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            mChannelDataMap.put(i, new ArrayList<>());
        }
        highpassChannel1LastFilteredData = new ArrayList<>();
        highpassChannel1LastFilteringData = new ArrayList<>();
        highpassChannel2LastFilteredData = new ArrayList<>();
        highpassChannel2LastFilteringData = new ArrayList<>();
        highpassChannel3LastFilteredData = new ArrayList<>();
        highpassChannel3LastFilteringData = new ArrayList<>();
        highpassChannel4LastFilteredData = new ArrayList<>();
        highpassChannel4LastFilteringData = new ArrayList<>();
        highpassChannel5LastFilteredData = new ArrayList<>();
        highpassChannel5LastFilteringData = new ArrayList<>();
        highpassChannel6LastFilteredData = new ArrayList<>();
        highpassChannel6LastFilteringData = new ArrayList<>();
        highpassChannel7LastFilteredData = new ArrayList<>();
        highpassChannel7LastFilteringData = new ArrayList<>();
        highpassChannel8LastFilteredData = new ArrayList<>();
        highpassChannel8LastFilteringData = new ArrayList<>();

        notchChannel1LastFilteredData = new ArrayList<>();
        notchChannel1LastFilteringData = new ArrayList<>();
        notchChannel2LastFilteredData = new ArrayList<>();
        notchChannel2LastFilteringData = new ArrayList<>();
        notchChannel3LastFilteredData = new ArrayList<>();
        notchChannel3LastFilteringData = new ArrayList<>();
        notchChannel4LastFilteredData = new ArrayList<>();
        notchChannel4LastFilteringData = new ArrayList<>();
        notchChannel5LastFilteredData = new ArrayList<>();
        notchChannel5LastFilteringData = new ArrayList<>();
        notchChannel6LastFilteredData = new ArrayList<>();
        notchChannel6LastFilteringData = new ArrayList<>();
        notchChannel7LastFilteredData = new ArrayList<>();
        notchChannel7LastFilteringData = new ArrayList<>();
        notchChannel8LastFilteredData = new ArrayList<>();
        notchChannel8LastFilteringData = new ArrayList<>();

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

    public void filterModeChanged() {
        LogUtils.i("filterModeChanged");
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.filterModeChanged();
        }
    }

    public List<Float> getChannel1HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel1Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel1LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel1LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel1LastFilteredData.clear();
            highpassChannel1LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel1LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel1LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel1LastFilteredData.clear();
            highpassChannel1LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel1LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel1LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel1Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel2HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel2Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel2LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel2LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel2LastFilteredData.clear();
            highpassChannel2LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel2LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel2LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel2LastFilteredData.clear();
            highpassChannel2LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel2LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel2LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel2Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel3HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel3Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel3LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel3LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel3LastFilteredData.clear();
            highpassChannel3LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel3LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel3LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel3LastFilteredData.clear();
            highpassChannel3LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel3LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel3LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel3Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel4HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel4Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel4LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel4LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel4LastFilteredData.clear();
            highpassChannel4LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel4LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel4LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel4LastFilteredData.clear();
            highpassChannel4LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel4LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel4LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel4Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel5HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel5Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel5LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel5LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel5LastFilteredData.clear();
            highpassChannel5LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel5LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel5LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel5LastFilteredData.clear();
            highpassChannel5LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel5LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel5LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel5Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel6HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel6Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel6LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel6LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel6LastFilteredData.clear();
            highpassChannel6LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel6LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel6LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel6LastFilteredData.clear();
            highpassChannel6LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel6LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel6LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel6Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel7HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel7Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel7LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel7LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel7LastFilteredData.clear();
            highpassChannel7LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel7LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel7LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredPointChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredPointChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            highpassChannel7LastFilteredData.clear();
            highpassChannel7LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel7LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel7LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel7Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel8HighPassFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (highpassChannel8Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(highpassChannel8LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(highpassChannel8LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            highpassChannel8LastFilteredData.clear();
            highpassChannel8LastFilteringData.clear();
            // 初始化channel的已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据3 + 此次待过滤数据40 = 43
            channelFilteringData.addAll(srcData);
            for (int i = orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (highpassFilterB0 * channelFilteringData.get(i) + highpassFilterB1 * channelFilteringData.get(i - 1) + highpassFilterB2 * channelFilteringData.get(i - 2) + highpassFilterB3 * channelFilteringData.get(i - 3));
                    float filteredChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel8LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel8LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第四位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfHighpassFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfHighpassFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    //通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    // 没有保存的过滤数据,高通滤波从第四个数据开始计算
                    float filteredChannelB = (float) (highpassFilterB0 * srcData.get(i) + highpassFilterB1 * srcData.get(i - 1) + highpassFilterB2 * srcData.get(i - 2) + highpassFilterB3 * srcData.get(i - 3));
                    float filteredChannelA = (float) (highpassFilterA1 * channelFilteredData.get(i - 1) + highpassFilterA2 * channelFilteredData.get(i - 2) + highpassFilterA3 * channelFilteredData.get(i - 3));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }

            highpassChannel8LastFilteredData.clear();
            highpassChannel8LastFilteringData.clear();

            // 保存最后三个已过滤的数据和最后三个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfHighpassFilter; i < channelFilteredData.size(); i++) {
                highpassChannel8LastFilteredData.add(channelFilteredData.get(i));
                highpassChannel8LastFilteringData.add(srcData.get(i));
            }
            // 设置channel的过滤状态为true
            highpassChannel8Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel1NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel1Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel1LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel1LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel1LastFilteredData.clear();
            notchChannel1LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel1LastFilteredData.add(channelFilteredData.get(i));
                notchChannel1LastFilteringData.add(channelFilteringData.get(i));
            }
            // 此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel1LastFilteredData.clear();
            notchChannel1LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel1LastFilteredData.add(channelFilteredData.get(i));
                notchChannel1LastFilteringData.add(srcData.get(i));
            }
            // 设置channel1的过滤状态为true
            notchChannel1Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel2NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel2Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel2LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel2LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel2LastFilteredData.clear();
            notchChannel2LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel2LastFilteredData.add(channelFilteredData.get(i));
                notchChannel2LastFilteringData.add(channelFilteringData.get(i));
            }
            // 此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel2LastFilteredData.clear();
            notchChannel2LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel2LastFilteredData.add(channelFilteredData.get(i));
                notchChannel2LastFilteringData.add(srcData.get(i));
            }
            // 设置channel2的过滤状态为true
            notchChannel2Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel3NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel3Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel3LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel3LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel3LastFilteredData.clear();
            notchChannel3LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel3LastFilteredData.add(channelFilteredData.get(i));
                notchChannel3LastFilteringData.add(channelFilteringData.get(i));
            }
            // 此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel3LastFilteredData.clear();
            notchChannel3LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel3LastFilteredData.add(channelFilteredData.get(i));
                notchChannel3LastFilteringData.add(srcData.get(i));
            }
            // 设置channel3的过滤状态为true
            notchChannel3Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel4NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel4Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel4LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel4LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel4LastFilteredData.clear();
            notchChannel4LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel4LastFilteredData.add(channelFilteredData.get(i));
                notchChannel4LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel4LastFilteredData.clear();
            notchChannel4LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel4LastFilteredData.add(channelFilteredData.get(i));
                notchChannel4LastFilteringData.add(srcData.get(i));
            }
            // 设置channel4的过滤状态为true
            notchChannel4Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel5NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel5Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel5LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel5LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel5LastFilteredData.clear();
            notchChannel5LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel5LastFilteredData.add(channelFilteredData.get(i));
                notchChannel5LastFilteringData.add(channelFilteringData.get(i));
            }
            // 此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel5LastFilteredData.clear();
            notchChannel5LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel5LastFilteredData.add(channelFilteredData.get(i));
                notchChannel5LastFilteringData.add(srcData.get(i));
            }
            // 设置channel5的过滤状态为true
            notchChannel5Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel6NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel6Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel6LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel6LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel6LastFilteredData.clear();
            notchChannel6LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel6LastFilteredData.add(channelFilteredData.get(i));
                notchChannel6LastFilteringData.add(channelFilteringData.get(i));
            }
            //此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel6LastFilteredData.clear();
            notchChannel6LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel6LastFilteredData.add(channelFilteredData.get(i));
                notchChannel6LastFilteringData.add(srcData.get(i));
            }
            // 设置channel6的过滤状态为true
            notchChannel6Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel7NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel7Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel7LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel7LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel7LastFilteredData.clear();
            notchChannel7LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel7LastFilteredData.add(channelFilteredData.get(i));
                notchChannel7LastFilteringData.add(channelFilteringData.get(i));
            }
            // 此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel7LastFilteredData.clear();
            notchChannel7LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel7LastFilteredData.add(channelFilteredData.get(i));
                notchChannel7LastFilteringData.add(srcData.get(i));
            }
            // 设置channel7的过滤状态为true
            notchChannel7Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getChannel8NotchFilteredData(List<Float> srcData) {
        List<Float> filteredData;
        if (notchChannel8Filtered) {
            List<Float> channelFilteredData = new ArrayList<>(notchChannel8LastFilteredData);
            List<Float> channelFilteringData = new ArrayList<>(notchChannel8LastFilteringData);
            // 每次取完上一次的已过滤数据和待过滤数据,将容器清空,用于下一次更新已过滤数据和待过滤数据
            notchChannel8LastFilteredData.clear();
            notchChannel8LastFilteringData.clear();
            // 初始化已过滤数据
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }
            // 更新待过滤数据,此时待过滤数据长度为上一次保存的待过滤数据2 + 此次待过滤数据40 = 42
            channelFilteringData.addAll(srcData);

            for (int i = orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                if (channelFilteringData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredChannelB = (float) (notchFilterB0 * channelFilteringData.get(i) + notchFilterB1 * channelFilteringData.get(i - 1) + notchFilterB2 * channelFilteringData.get(i - 2));
                    float filteredChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredChannel = filteredChannelB - filteredChannelA;
                    channelFilteredData.set(i, filteredChannel);
                }
            }
            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = channelFilteredData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel8LastFilteredData.add(channelFilteredData.get(i));
                notchChannel8LastFilteringData.add(channelFilteringData.get(i));
            }
            // 此次实际的已过滤数据为第三位到最后一位
            filteredData = new ArrayList<>(channelFilteredData.subList(orderOfNotchFilter, channelFilteredData.size()));
        } else {
            List<Float> channelFilteredData = new ArrayList<>();
            for (int i = 0; i < srcData.size(); i++) {
                channelFilteredData.add(0.0F);
            }

            for (int i = orderOfNotchFilter; i < srcData.size(); i++) {
                if (srcData.get(i) == 0.0) {
                    // 通道数据为0的情况
                    channelFilteredData.set(i, 0.0F);
                } else {
                    float filteredPointChannelB = (float) (notchFilterB0 * srcData.get(i) + notchFilterB1 * srcData.get(i - 1) + notchFilterB2 * srcData.get(i - 2));
                    float filteredPointChannelA = (float) (notchFilterA1 * channelFilteredData.get(i - 1) + notchFilterA2 * channelFilteredData.get(i - 2));
                    float filteredPointChannel = filteredPointChannelB - filteredPointChannelA;
                    channelFilteredData.set(i, filteredPointChannel);
                }
            }

            notchChannel8LastFilteredData.clear();
            notchChannel8LastFilteringData.clear();

            // 保存最后两个已过滤的数据和最后两个待过滤的数据,用作下一次过滤计算的参数
            for (int i = srcData.size() - orderOfNotchFilter; i < channelFilteredData.size(); i++) {
                notchChannel8LastFilteredData.add(channelFilteredData.get(i));
                notchChannel8LastFilteringData.add(srcData.get(i));
            }
            // 设置channel8的过滤状态为true
            notchChannel8Filtered = true;
            filteredData = new ArrayList<>(channelFilteredData);
        }
        return filteredData;
    }

    public List<Float> getHighPassFilteredData(int channel, List<Float> data) {
        List<Float> highPassFilteredData = new ArrayList<>();
        if (channel == 0) {
            highPassFilteredData = getChannel1HighPassFilteredData(data);
        } else if (channel == 1) {
            highPassFilteredData = getChannel2HighPassFilteredData(data);
        } else if (channel == 2) {
            highPassFilteredData = getChannel3HighPassFilteredData(data);
        } else if (channel == 3) {
            highPassFilteredData = getChannel4HighPassFilteredData(data);
        } else if (channel == 4) {
            highPassFilteredData = getChannel5HighPassFilteredData(data);
        } else if (channel == 5) {
            highPassFilteredData = getChannel6HighPassFilteredData(data);
        } else if (channel == 6) {
            highPassFilteredData = getChannel7HighPassFilteredData(data);
        } else if (channel == 7) {
            highPassFilteredData = getChannel8HighPassFilteredData(data);
        }
        return highPassFilteredData;
    }

    public List<Float> getNotchFilteredData(int channel, List<Float> data) {
        List<Float> notchFilteredData = new ArrayList<>();
        if (channel == 0) {
            notchFilteredData = getChannel1NotchFilteredData(data);
        } else if (channel == 1) {
            notchFilteredData = getChannel2NotchFilteredData(data);
        } else if (channel == 2) {
            notchFilteredData = getChannel3NotchFilteredData(data);
        } else if (channel == 3) {
            notchFilteredData = getChannel4NotchFilteredData(data);
        } else if (channel == 4) {
            notchFilteredData = getChannel5NotchFilteredData(data);
        } else if (channel == 5) {
            notchFilteredData = getChannel6NotchFilteredData(data);
        } else if (channel == 6) {
            notchFilteredData = getChannel7NotchFilteredData(data);
        } else if (channel == 7) {
            notchFilteredData = getChannel8NotchFilteredData(data);
        }
        return notchFilteredData;
    }

    public void replySampledData(List<Integer> data) {
        List<Integer> capacitanceData = new ArrayList<>(data.subList(960, 964));
        replyCapacitanceData(CalculateUtils.integerListToBytes(capacitanceData));

        // 清空mChannelDataMap容器中保存的数据
        for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
            List<Float> channelData = mChannelDataMap.get(i);
            if (channelData != null) {
                channelData.clear();
            }
        }

        // defaultChannelSize: 默认8个通道数据所占用字节的长度
        int defaultChannelsSize = 3 * Constant.DEFAULT_CHANNEL;
        for (int i = 0; i < data.size() - defaultChannelsSize; i += defaultChannelsSize) {
            for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                float voltage = calculateVoltage(data.get(j * 3 + i), data.get(j * 3 + i + 1), data.get(j * 3 + i + 2));
                if (getSaveDataState()) {
                    mOriginalData.add(String.valueOf(voltage));
                }
                List<Float> channelData = mChannelDataMap.get(j);
                if (channelData != null) {
                    channelData.add(voltage);
                }
            }
            if (getSaveDataState()) {
                mOriginalData.add(String.valueOf(mCurrentCapacitance));
                mOriginalData.add("\n");
            }
        }

        if (getHighpassFilterState() && !getNotchFilterState()) {
            Map<Integer, List<Float>> filteredDataMap = new HashMap<>();
            // 高通滤波开启
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Float> channelData = mChannelDataMap.get(i);
                if (channelData != null) {
                    List<Float> filteredData = getHighPassFilteredData(i, channelData);
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyVoltage(i, filteredData);
                    }
                    filteredDataMap.put(i, filteredData);
                }
            }

            if (getSaveDataState()) {
                // 一帧数据包含8个通道数据的个数
                int channelsCount = data.size() / defaultChannelsSize;
                for (int i = 0; i < channelsCount; i++) {
                    for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                        List<Float> doubles = filteredDataMap.get(j);
                        if (doubles != null) {
                            mFilterData.add(String.valueOf(doubles.get(i)));
                        }
                    }
                    mFilterData.add(String.valueOf(mCurrentCapacitance));
                    mFilterData.add("\n");
                }
            }
        } else if (getNotchFilterState() && !getHighpassFilterState()) {
            Map<Integer, List<Float>> filteredDataMap = new HashMap<>();
            // 工频陷波开启
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Float> channelData = mChannelDataMap.get(i);
                if (channelData != null) {
                    List<Float> filteredData = getNotchFilteredData(i, channelData);
                    if (mDeviceInfoListener != null) {
                        mDeviceInfoListener.replyVoltage(i, filteredData);
                    }
                    filteredDataMap.put(i, filteredData);
                }
            }

            if (getSaveDataState()) {
                // 一帧数据包含8个通道数据的个数
                int channelsCount = data.size() / defaultChannelsSize;
                for (int i = 0; i < channelsCount; i++) {
                    for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                        List<Float> doubles = filteredDataMap.get(j);
                        if (doubles != null) {
                            mFilterData.add(String.valueOf(doubles.get(i)));
                        }
                    }
                    mFilterData.add(String.valueOf(mCurrentCapacitance));
                    mFilterData.add("\n");
                }
            }
        } else if (getHighpassFilterState() && getNotchFilterState()) {
            Map<Integer, List<Float>> filteredDataMap = new HashMap<>();
            // 高通滤波和工频陷波同时开启,先计算高通滤波,再将高通滤波过滤后的数据用来做工频陷波
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Float> channelData = mChannelDataMap.get(i);
                if (channelData != null) {
                    List<Float> highPassFilteredData = getHighPassFilteredData(i, channelData);
                    if (highPassFilteredData != null) {
                        List<Float> filteredData = getNotchFilteredData(i, highPassFilteredData);
                        if (mDeviceInfoListener != null) {
                            mDeviceInfoListener.replyVoltage(i, filteredData);
                        }
                        filteredDataMap.put(i, filteredData);
                    }
                }
            }

            if (getSaveDataState()) {
                // 一帧数据包含8个通道数据的个数
                int channelsCount = data.size() / defaultChannelsSize;
                for (int i = 0; i < channelsCount; i++) {
                    for (int j = 0; j < Constant.DEFAULT_CHANNEL; j++) {
                        List<Float> doubles = filteredDataMap.get(j);
                        if (doubles != null) {
                            mFilterData.add(String.valueOf(doubles.get(i)));
                        }
                    }
                    mFilterData.add(String.valueOf(mCurrentCapacitance));
                    mFilterData.add("\n");
                }
            }
        } else {
            // 不做任何滤波操作
            for (int i = 0; i < Constant.DEFAULT_CHANNEL; i++) {
                List<Float> channelData = mChannelDataMap.get(i);
                if (mDeviceInfoListener != null) {
                    mDeviceInfoListener.replyVoltage(i, channelData);
                }
            }
        }
    }

    public void replyCapacitanceData(byte[] data) {
        float capacitance = (float) (Math.round((CalculateUtils.getFloat(data, 0) - Constant.DEFAULT_CAPACITANCE) * 10)) / 10;
        if (capacitance < 0) {
            capacitance = 0;
        }
        mCurrentCapacitance = capacitance;
        if (getCalibrateState()) {
            float angle = convertCapacitanceToAngle(capacitance);
            if (mDeviceInfoListener != null) {
                mDeviceInfoListener.replyAngle(angle);
            }
        } else {
            if (mDeviceInfoListener != null) {
                mDeviceInfoListener.replyCapacitance(capacitance);
            }
        }
    }

    public void replyStopDataCollect(List<Integer> data) {
        if (mDeviceInfoListener != null) {
            mDeviceInfoListener.replyStopDataCollect(data);
        }
    }

    public float calculateVoltage(int first, int second, int third) {
        if (first == 0x00 && second == 0x00 && third == 0x00) {
            return 0.0F;
        }

        long combine = CalculateUtils.threeLongCombine(first, second, third);
        float voltage;
        if (first >= 0x80) {
            // 第一个字节的最高位大于等于8 电压为负数(正数最大值为0x7F)
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

    public boolean getHighpassFilterState() {
        return mHighpassFilterState;
    }

    public void setHighpassFilterState(boolean state) {
        this.mHighpassFilterState = state;
    }

    public boolean getNotchFilterState() {
        return mNotchFilterState;
    }

    public void setNotchFilterState(boolean state) {
        this.mNotchFilterState = state;
    }

    public float convertCapacitanceToAngle(float capacitance) {
        float srcAngle = (angle1 - angle2) / (p1 - p2) * capacitance + angle1 - p1 * (angle1 - angle2) / (p1 - p2);
        return (float) (Math.round((srcAngle) * 10)) / 10;
    }

    public float getP1() {
        return this.p1;
    }

    public void setP1(float p) {
        this.p1 = p;
    }

    public float getP2() {
        return this.p2;
    }

    public void setP2(float p) {
        this.p2 = p;
    }

    public float getAngle1() {
        return this.angle1;
    }

    public void setAngle1(float angle) {
        this.angle1 = angle;
    }

    public float getAngle2() {
        return this.angle2;
    }

    public void setAngle2(float angle) {
        this.angle2 = angle;
    }

    public float getCurrentCapacitance() {
        return this.mCurrentCapacitance;
    }

    public void setCurrentCapacitance(float value) {
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
        return this.mHighpassFilterState || this.mNotchFilterState;
    }

}
