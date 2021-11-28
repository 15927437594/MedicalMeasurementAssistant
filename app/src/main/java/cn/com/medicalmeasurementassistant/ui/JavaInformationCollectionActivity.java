package cn.com.medicalmeasurementassistant.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.entity.SettingParamsBean;
import cn.com.medicalmeasurementassistant.listener.CalibrateListener;
import cn.com.medicalmeasurementassistant.listener.DeviceInfoListener;
import cn.com.medicalmeasurementassistant.listener.OnWaveCountChangeListener;
import cn.com.medicalmeasurementassistant.manager.DeviceManager;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.manager.WaveManager;
import cn.com.medicalmeasurementassistant.protocol.send.SendStartDataCollect;
import cn.com.medicalmeasurementassistant.protocol.send.SendStopDataCollect;
import cn.com.medicalmeasurementassistant.ui.dialog.InputFileNameDialogKt;
import cn.com.medicalmeasurementassistant.ui.dialog.ScaleSettingDialogKt;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.SocketUtils;
import cn.com.medicalmeasurementassistant.utils.ToastHelper;

public class JavaInformationCollectionActivity extends BaseKotlinActivity implements View.OnClickListener, DeviceInfoListener, CalibrateListener, OnWaveCountChangeListener {
    // 右上角链接按钮
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mConnectionSwitch;
    // 记录采样数据按钮
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mSaveDataSwitch;
    // 测量状态ImageView
    private ImageView mCollectionIv;
    // 测量状态TextView
    private TextView mCollectionTv;
    // 当前测量状态，启动或者停止
    private boolean mCollectionStatus;
    private DeviceManager mDeviceManager;
    private FrameLayout mEmgWaveFrameLayout, mCapacitanceWaveFrameLayout;
    private RadioGroup mRadioGroup;
    private MyEMGWaveView mEmgWaveView;
    private MyCapWaveView mCapacitanceWaveView;
    private TextView mTvSettingTimeScale;
    private TextView mTvSettingEMGScaleRange;
    private TextView mTvSettingCapScaleRange;
    private TextView mTvSettingCapScaleRangeTip;
    private TextView mTvSettingCapUnit;
    private TextView mTvSaveTime;
    private Handler mHandler;
    private int mSaveTime = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_information_collection;
    }

    @Override
    public void initView() {
        mHandler = new Handler(Looper.getMainLooper());
        mDeviceManager = DeviceManager.getInstance();
        mConnectionSwitch = findViewById(R.id.iv_collect_operate_top);
        mSaveDataSwitch = findViewById(R.id.iv_save_data);
        mRadioGroup = findViewById(R.id.rg_contain);
        mCollectionIv = findViewById(R.id.iv_collect_operate);
        mCollectionTv = findViewById(R.id.tv_collection_status);
        mEmgWaveFrameLayout = findViewById(R.id.frameLayout_emg_wave_parent);
        mCapacitanceWaveFrameLayout = findViewById(R.id.frameLayout_cap_wave_parent);

        mTvSettingTimeScale = findViewById(R.id.tv_show_time_length);
        mTvSettingEMGScaleRange = findViewById(R.id.tv_emg_scale_range);
        mTvSettingCapScaleRangeTip = findViewById(R.id.tv_cap_scale_range_tip);
        mTvSettingCapUnit = findViewById(R.id.tv_cap_unit);
        mTvSettingCapScaleRange = findViewById(R.id.tv_cap_scale_range);
        mTvSaveTime = findViewById(R.id.tv_save_time);

        WaveManager.getInstance().addCallback(this);
        initEmgView();
        initCapacitanceView();
    }

    private final Runnable mUpdateSaveTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mSaveTime += 1;
            LogUtils.i("mSaveTime=" + mSaveTime);
            mHandler.postDelayed(this, 1000L);
            if (mSaveTime == mDeviceManager.getSaveTime()) {
                mHandler.removeCallbacks(mUpdateSaveTimeRunnable);
                stopDeviceCollect();
                mHandler.postDelayed(() -> {
                    saveSampleData();
                    mDeviceManager.setSaveDataState(false);
                }, 500L);
            }
        }
    };

    private void saveSampleData() {
        InputFileNameDialogKt.showInputFileNameDialog(this,
                fileName -> MeasurementFileUtils.saveMeasurementFile(fileName, mDeviceManager.getOriginalData(), mDeviceManager.getFilterData()));
    }

    /**
     * 开启保存采样数据按钮
     * case1: 当采样时间大于0的时候, 超过设置的采样时间之后停止采集数据并且记录采样数据
     * case2: 当采样时间等于0的时候, 一直记录采样数据
     */
    private void startRecordSampleData() {
        mDeviceManager.getOriginalData().clear();
        mDeviceManager.getFilterData().clear();
        if (mDeviceManager.getSaveDataState() && mDeviceManager.getSaveTime() > 0) {
            mSaveTime = 0;
            mHandler.removeCallbacks(mUpdateSaveTimeRunnable);
            mHandler.postDelayed(mUpdateSaveTimeRunnable, 1000L);
        }
    }

    @Override
    public void initListener() {
        mDeviceManager.setDeviceInfoListener(this);
        mDeviceManager.setCalibrateListener(this);
        setClick(R.id.iv_file_list);
        setClick(R.id.iv_file_save);
        setClick(R.id.stv_setting_params);
        setClick(R.id.stv_collect_angle);
        setClick(R.id.iv_collect_operate);
        setClick(R.id.srl_left_top);
        setClick(R.id.srl_left_bottom);
        setClick(R.id.srl_right_top);
        setClick(R.id.srl_right_bottom);

        mConnectionSwitch.setOnCheckedChangeListener((compoundButton, isConnection) -> {
            LogUtils.i("isConnection=" + isConnection);
            if (isConnection) {
                String hostIp = SocketUtils.getHostIp("192.168");
                LogUtils.i("hostIp=" + hostIp);
                ServerManager.getInstance().connectDevice();
            } else {
                ServerManager.getInstance().disconnectDevice();
            }
        });

        mSaveDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mDeviceManager.setSaveDataState(isChecked));

        mRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rb_option_one) {
                mCapacitanceWaveFrameLayout.setVisibility(View.INVISIBLE);
                mEmgWaveFrameLayout.setVisibility(View.VISIBLE);
            } else {
                mCapacitanceWaveFrameLayout.setVisibility(View.VISIBLE);
                mEmgWaveFrameLayout.setVisibility(View.INVISIBLE);
            }
        });


        mCapacitanceWaveFrameLayout.setVisibility(View.VISIBLE);
        mEmgWaveFrameLayout.setVisibility(View.INVISIBLE);
        mCapacitanceWaveFrameLayout.setVisibility(View.INVISIBLE);
        mEmgWaveFrameLayout.setVisibility(View.VISIBLE);
    }

    private void setClick(@IdRes int id) {
        findViewById(id).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_file_list:
                // 文件查找
                BaseKotlinActivity.Companion.launcherActivity(this, FileSearchActivity.class);
                break;
            case R.id.iv_file_save:
                if (mCollectionStatus) {
                    ToastHelper.showShort("请先停止数据采集");
                    return;
                }
                if (mDeviceManager.getOriginalData().size() == 0) {
                    ToastHelper.showShort("请采集数据后再保存");
                    return;
                }
                saveSampleData();
                break;
            case R.id.stv_setting_params:
                if (mCollectionStatus) {
                    ToastHelper.showShort("请先停止数据采集");
                    return;
                }
                // 参数设置
                BaseKotlinActivity.Companion.launcherActivity(this, SettingParamsActivity.class);
                break;
            case R.id.stv_collect_angle:
                // 角度校准
                if (!mDeviceManager.isDeviceStart()) {
                    ToastHelper.showShort("请启动设备采集");
                    return;
                }
                BaseKotlinActivity.Companion.launcherActivity(this, CalibrationAngleActivity.class);
                break;
            case R.id.iv_collect_operate:
                if (!mDeviceManager.isDeviceOpen()) {
                    ToastHelper.showShort("请打开设备");
                    return;
                }

                if (!mCollectionStatus) {
                    // 启动
                    startDeviceCollect();
                } else {
                    // 停止
                    stopDeviceCollect();
                }
                break;
            case R.id.srl_left_top:
                if (mCollectionStatus) {
                    ToastHelper.showShort("请先停止数据采集");
                    return;
                }
                ScaleSettingDialogKt.showTimeScaleDialog(getActivity(), Constant.SETTING_TYPE_TIME_LENGTH, (settingValue) -> {
                    mTvSettingTimeScale.setText(String.valueOf(settingValue));
                    mEmgWaveView.setShowTimeLength(settingValue);
                    mCapacitanceWaveView.setShowTimeLength(settingValue);
                });
                break;
            case R.id.srl_left_bottom:
                if (mCollectionStatus) {
                    ToastHelper.showShort("请先停止数据采集");
                    return;
                }
                ScaleSettingDialogKt.showTimeScaleDialog(getActivity(), Constant.SETTING_TYPE_CAP_SCALE_RANGE, (settingValue) -> {
                    mTvSettingCapScaleRange.setText(String.valueOf(settingValue));
                    mCapacitanceWaveView.setMaxValue(settingValue);
                });
                break;
            case R.id.srl_right_top:
                if (mCollectionStatus) {
                    ToastHelper.showShort("请先停止数据采集");
                    return;
                }
                ScaleSettingDialogKt.showTimeScaleDialog(getActivity(), Constant.SETTING_TYPE_EMG_SCALE_RANGE, (settingValue) -> {
                    mTvSettingEMGScaleRange.setText(String.valueOf(settingValue));
                    double maxValue = (double) settingValue / 2;
                    mEmgWaveView.setMaxValue(maxValue);
                });
                break;
            case R.id.srl_right_bottom:
                if (mCollectionStatus) {
                    ToastHelper.showShort("请先停止数据采集");
                    return;
                }
                ScaleSettingDialogKt.showTimeScaleDialog(getActivity(), Constant.SETTING_TYPE_RECORD_CAPTURE_TIME, (settingValue) -> {
                    mDeviceManager.setSaveTime(settingValue);
                    mTvSaveTime.setText(String.valueOf(settingValue));
                });
                break;
            default:
                break;
        }
    }

    private void startDeviceCollect() {
        ServerManager.getInstance().sendData(new SendStartDataCollect().pack());
        mCollectionIv.setImageResource(R.drawable.icon_collect_stop);
        mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.electrode_text_color_on));
        mDeviceManager.setSaveDataState(mSaveDataSwitch.isChecked());
        mDeviceManager.setCurrentCapacitance(0);
        mCollectionStatus = !mCollectionStatus;
    }

    private void stopDeviceCollect() {
        ServerManager.getInstance().sendData(new SendStopDataCollect().pack());
        mCollectionIv.setImageResource(R.drawable.icon_collect_start);
        mCollectionTv.setText(getString(R.string.text_collect_start));
        mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.theme_color));
        mSaveDataSwitch.setEnabled(true);
        mCollectionStatus = !mCollectionStatus;
    }

    /**
     * 设备回复TCP握手信号
     */
    @Override
    public void replyHandshake(List<Integer> data) {
        mDeviceManager.setDeviceOpen(true);
        mDeviceManager.resetParams();
        runOnUiThread(() -> ToastHelper.showShort("设备打开成功"));
    }

    /**
     * 设备回复开始采集数据
     */
    @Override
    public void replyStartDataCollect(List<Integer> data) {
        LogUtils.i("replyStartDataCollect");
        mDeviceManager.setDeviceStart(true);
        runOnUiThread(() -> ToastHelper.showShort("设备开始采集数据"));
        mSaveDataSwitch.setEnabled(false);
        startRecordSampleData();
    }

    /**
     * 设备回复电压值
     *
     * @param channel 电压通道 1~8
     * @param data    电压数据
     */
    @Override
    public void replyVoltage(int channel, List<Float> data) {
        List<Float> filterData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i % 20 == 0) {
                filterData.add(data.get(i));
            }
        }

        runOnUiThread(() -> {
            mEmgWaveView.addData(channel, filterData);
            mEmgWaveView.updateWaveLine();
        });
    }

    /**
     * 设备回复停止采集数据
     */
    @Override
    public void replyStopDataCollect(List<Integer> data) {
        LogUtils.i("replyStopDataCollect");
        runOnUiThread(() -> ToastHelper.showShort("设备停止采集数据"));
        mDeviceManager.setDeviceStart(false);
        mSaveDataSwitch.setEnabled(true);
    }

    /**
     * 设备回复电容值
     *
     * @param capacitance 电容值
     */
    @Override
    public void replyCapacitance(float capacitance) {
        LogUtils.d("replyCapacitance capacitance=" + capacitance);
        runOnUiThread(() -> {
            mCapacitanceWaveView.addData(capacitance);
            mCapacitanceWaveView.updateWaveLine();
        });
    }

    /**
     * 设备回复角度值
     *
     * @param angle 角度值
     */
    @Override
    public void replyAngle(float angle) {
        LogUtils.d("replyAngle angle=" + angle);
        runOnUiThread(() -> {
            mCapacitanceWaveView.addData(angle);
            mCapacitanceWaveView.updateWaveLine();
        });
    }

    /**
     * 设备回复停止
     */
    @Override
    public void replyDeviceStopped() {
        LogUtils.i("replyDeviceStopped");
        runOnUiThread(() -> {
            mSaveDataSwitch.setEnabled(true);
            mConnectionSwitch.setChecked(false);
            mDeviceManager.setDeviceOpen(false);
            mCollectionStatus = false;
            mCollectionIv.setImageResource(R.drawable.icon_collect_start);
            mCollectionTv.setText(getString(R.string.text_collect_start));
            mCollectionTv.setTextColor(ContextCompat.getColor(JavaInformationCollectionActivity.this, R.color.theme_color));
        });
        mHandler.postDelayed(() -> runOnUiThread(() -> {
            mEmgWaveView.resetView();
            mCapacitanceWaveView.resetView();
        }), 200L);
    }

    @Override
    public void filterModeChanged() {
        LogUtils.i("filterModeChanged");
        mEmgWaveView.resetView();
        mCapacitanceWaveView.resetView();
    }

    @Override
    public void waveCountChange() {
        LogUtils.i("waveCountChange");
        List<SettingParamsBean.ChannelBean> channelBeans = SettingParamsBean.getInstance().getChannelBeans();
        mEmgWaveView.changeChannelStatus(channelBeans);
    }

    private void initEmgView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mEmgWaveView = new MyEMGWaveView(getActivity());
        mEmgWaveView.setxAxisDesc("时间/s");
        mEmgWaveView.setyAxisDesc("电压/mV");
        mEmgWaveFrameLayout.addView(mEmgWaveView, layoutParams);
    }

    private void initCapacitanceView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mCapacitanceWaveView = new MyCapWaveView(getActivity());
        mCapacitanceWaveView.setxAxisDesc("时间/s");
        mCapacitanceWaveView.setyAxisDesc("电容/pF");
        mCapacitanceWaveFrameLayout.addView(mCapacitanceWaveView, layoutParams);
    }

    @Override
    public void calibrateSuccess() {
        LogUtils.i("calibrateSuccess");
        mCapacitanceWaveView.setyAxisDesc("角度/度");
        mTvSettingCapScaleRangeTip.setText("角度刻度范围");
        mTvSettingCapUnit.setText("度");
        mTvSettingCapScaleRange.setText(String.valueOf(90));
        mCapacitanceWaveView.setMinValue(-20);
        mCapacitanceWaveView.setMaxValue(90);
        mCapacitanceWaveView.setWaveType(MyCapWaveView.ANGLE);
        mCapacitanceWaveView.resetView();
        mEmgWaveView.resetView();
    }

    @Override
    public void calibrateFail() {
        LogUtils.i("calibrateFail");
        mCapacitanceWaveView.setyAxisDesc("电容/pF");
        mTvSettingCapScaleRangeTip.setText(getString(R.string.cap_scale_range));
        mTvSettingCapUnit.setText(getString(R.string.pf));
        mTvSettingCapScaleRange.setText(String.valueOf(60));
        mCapacitanceWaveView.setMinValue(0);
        mCapacitanceWaveView.setMaxValue(60);
        mCapacitanceWaveView.setWaveType(MyCapWaveView.CAP);
        mCapacitanceWaveView.resetView();
        mEmgWaveView.resetView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}