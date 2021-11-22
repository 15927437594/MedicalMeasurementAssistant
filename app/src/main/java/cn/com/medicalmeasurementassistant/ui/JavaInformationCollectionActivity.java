package cn.com.medicalmeasurementassistant.ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import java.util.List;


import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.entity.SettingParamsBean;
import cn.com.medicalmeasurementassistant.listener.DeviceInfoListener;
import cn.com.medicalmeasurementassistant.listener.OnWaveCountChangeListener;
import cn.com.medicalmeasurementassistant.manager.DeviceManager;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.manager.WaveManager;
import cn.com.medicalmeasurementassistant.protocol.send.SendStartDataCollect;
import cn.com.medicalmeasurementassistant.protocol.send.SendStopDataCollect;
import cn.com.medicalmeasurementassistant.ui.dialog.InputFileNameDialogKt;
import cn.com.medicalmeasurementassistant.ui.dialog.ScaleSettingDialogKt;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.SocketUtils;
import cn.com.medicalmeasurementassistant.utils.ToastHelper;

public class JavaInformationCollectionActivity extends BaseKotlinActivity implements View.OnClickListener, DeviceInfoListener, OnWaveCountChangeListener {
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_information_collection;
    }

    @Override
    public void initView() {
        mDeviceManager = DeviceManager.getInstance();
        mConnectionSwitch = findViewById(R.id.iv_collect_operate_top);
        mSaveDataSwitch = findViewById(R.id.iv_save_data);
        mRadioGroup = findViewById(R.id.rg_contain);
        mCollectionIv = findViewById(R.id.iv_collect_operate);
        mCollectionTv = findViewById(R.id.tv_collection_status);
        mDeviceManager.setSaveSampleData(mSaveDataSwitch.isChecked());
        mEmgWaveFrameLayout = findViewById(R.id.frameLayout_wave_parent);
        mCapacitanceWaveFrameLayout = findViewById(R.id.frameLayout_cap_wave_parent);

        mTvSettingTimeScale = findViewById(R.id.tv_show_time_length);
        mTvSettingEMGScaleRange = findViewById(R.id.tv_emg_scale_range);
        mTvSettingCapScaleRange = findViewById(R.id.tv_cap_scale_range);


        WaveManager.getInstance().addCallback(this);
        initEmgView();
        initCapacitanceView();
        byte[] bytes = new byte[4];
        bytes[0] = (byte) 0x41;
        bytes[1] = (byte) 0xE1;
        bytes[2] = (byte) 0x99;
        bytes[3] = (byte) 0x9A;
        float capacitance = CalculateUtils.getFloat(bytes, 0);
        LogUtils.d("capacitance=" + capacitance);

//        simulateVoltageData();
    }

    @Override
    public void initListener() {
        mDeviceManager.setDeviceInfoListener(this);
        setClick(R.id.iv_file_list);
        setClick(R.id.iv_file_save);
        setClick(R.id.stv_setting_params);
        setClick(R.id.stv_collect_angle);
        setClick(R.id.iv_collect_operate);

        setClick(R.id.srl_left_top);
        setClick(R.id.srl_left_bottom);
        setClick(R.id.srl_right_top);


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

        mSaveDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mDeviceManager.setSaveSampleData(isChecked));
        mRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rb_option_one) {
                mCapacitanceWaveFrameLayout.setVisibility(View.INVISIBLE);
                mEmgWaveFrameLayout.setVisibility(View.VISIBLE);
            } else {
                mCapacitanceWaveFrameLayout.setVisibility(View.VISIBLE);
                mEmgWaveFrameLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    int index;
    int lastValue;

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
                InputFileNameDialogKt.showInputFileNameDialog(this,
                        fileName -> MeasurementFileUtils.saveMeasurementFile(fileName, mDeviceManager.getOriginalData(), mDeviceManager.getHighPassFilterData()));
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
                BaseKotlinActivity.Companion.launcherActivity(this, CalibrationAngleActivity.class);
                break;
            case R.id.iv_collect_operate:
//                if (!mDeviceManager.isDeviceOpen()) {
//                    ToastHelper.showShort("请打开设备");
//                    return;
//                }

                if (!mCollectionStatus) {
                    // 启动
                    // 此处需要开始计时
                    ServerManager.getInstance().sendData(new SendStartDataCollect().pack());

                    mCollectionIv.setImageResource(R.drawable.icon_collect_stop);
                    mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.electrode_text_color_on));


                } else {
                    // 停止
                    // 此处需要停止计时
                    ServerManager.getInstance().sendData(new SendStopDataCollect().pack());

                    mCollectionIv.setImageResource(R.drawable.icon_collect_start);
                    mCollectionTv.setText(getString(R.string.text_collect_start));
                    mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.theme_color));
                }
                mCollectionStatus = !mCollectionStatus;
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
                    mEmgWaveView.setMaxValue(settingValue);
                });
                break;
            default:
                break;
        }
    }

//    private void simulateVoltageData() {
//        Random random = new Random();
//        if (timer1 != null) {
//            timer1.cancel();
//        }
//        timer1 = new CountDownTimer(10000_000, 1) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                for (int i = 0; i < 8; i++) {
//                    double v = random.nextFloat() * 2 - 2;
////                    replyVoltage(i, v);
//                    mEmgWaveView.addData(i, v);
//                    mEmgWaveView.updateWaveLine();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                for (int i = 0; i < 8; i++) {
//                    float v = random.nextFloat() * 2 - 2;
////                    replyVoltage(i, v);
//                    mEmgWaveView.addData(i , v);
//                    mEmgWaveView.updateWaveLine();
//                }
//            }
//        };
//        timer1.start();
//    }

    @Override
    public void replyHandshake(List<Integer> data) {
        mDeviceManager.setDeviceOpen(true);
        runOnUiThread(() -> ToastHelper.showShort("设备打开成功"));
    }

    @Override
    public void replyStartDataCollect(List<Integer> data) {
        mDeviceManager.setDeviceStart(true);
        mDeviceManager.getOriginalData().clear();
        mDeviceManager.getHighPassFilterData().clear();
        runOnUiThread(() -> ToastHelper.showShort("设备开始采集数据"));
    }

    @Override
    public void replyVoltage(int channel, List<Double> data) {
        LogUtils.d(String.format("channel=%s, point=%s", channel, data));
        mEmgWaveView.addData(channel, data);
        if (channel == Constant.DEFAULT_CHANNEL - 1) {
            mEmgWaveView.updateWaveLine();
        }
    }

    public void replyVoltage(int channel, Double data) {
        LogUtils.d(String.format("channel=%s, point=%s", channel, data));
        mEmgWaveView.addData(channel, data);
//        if (channel == Constant.DEFAULT_CHANNEL - 1) {
        mEmgWaveView.updateWaveLine();
//        }
    }

    @Override
    public void replyStopDataCollect(List<Integer> data) {
        runOnUiThread(() -> ToastHelper.showShort("设备停止采集数据"));
        mDeviceManager.setDeviceStart(false);
    }

    @Override
    public void replyCapacitance(float capacitance) {
        LogUtils.i("capacitance=" + capacitance);
        mCapacitanceWaveView.addData(capacitance);
        mCapacitanceWaveView.updateWaveLine();
    }

    @Override
    public void replyDeviceStopped() {
        LogUtils.i("replyDeviceStopped");
        DeviceManager.getInstance().setDeviceOpen(false);
        mCollectionStatus = false;
        mCollectionIv.setImageResource(R.drawable.icon_collect_start);
        mCollectionTv.setText(getString(R.string.text_collect_start));
        mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.theme_color));
    }

    @Override
    public void waveCountChange() {
        List<SettingParamsBean.ChannelBean> chanelBeans = SettingParamsBean.getInstance().getChanelBeans();
        mEmgWaveView.changeChannelStatus(chanelBeans);
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
}