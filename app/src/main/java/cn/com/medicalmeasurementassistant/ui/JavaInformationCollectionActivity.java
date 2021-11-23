package cn.com.medicalmeasurementassistant.ui;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
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
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;
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
    private CountDownTimer countDownTimer;

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

//        byte[] bytes = new byte[4];
//        bytes[0] = (byte) 0x41;
//        bytes[1] = (byte) 0xE1;
//        bytes[2] = (byte) 0x99;
//        bytes[3] = (byte) 0x9A;
//        float capacitance = CalculateUtils.getFloat(bytes, 0);
//        LogUtils.d("capacitance=" + capacitance);
//        double i = (double) (Math.round((28.12296541941 - Constant.DEFAULT_CAPACITANCE) * 100)) / 100;
//        LogUtils.d("capacitance=" + i);
//
//        long combine = CalculateUtils.threeLongCombine(0x04, 0x89, 0x62);
//        LogUtils.i("combine=" + combine);
//        float voltage = (float) combine * 400 / 0x7FFFFF;
//        LogUtils.i("voltage=" + voltage);
//        LogUtils.i("voltage=" + DeviceManager.getInstance().calculateVoltage(0xFF, 0xA3, 0x48));
//        LogUtils.i("voltage=" + DeviceManager.getInstance().calculateVoltage(0x04, 0x89, 0x62));

//        List<Double> readResult = new ArrayList<>();
//        for (int a = 0; a < 40; a++) {
//            double srcVoltage = 1 + 0.1 * Math.sin(a / 4.0);
//            LogUtils.i("srcVoltage=" + srcVoltage);
//            readResult.add(srcVoltage);
//        }

//        double[] temp1 = { 1.4094830751419067, 1.4128209352493286, 1.420498013496399, 1.4195443391799927, 1.4171124696731567,
//                1.4119149446487427, 1.4077187776565552, 1.3998032808303833, 1.3961316347122192, 1.3917447328567505,
//                1.3915539979934692, 1.3865472078323364, 1.3694287538528442, 1.3534547090530396, 1.3447285890579224,
//                1.3357640504837036, 1.3245583772659302, 1.3184548616409302, 1.3093949556350708, 1.2971402406692505,
//                1.2947560548782349, 1.2948037385940552, 1.293563961982727, 1.2915135622024536, 1.2864114046096802,
//                1.2867928743362427, 1.2899876832962036, 1.2927533388137817, 1.2975693941116333, 1.303625226020813,
//                1.3031007051467896, 1.3096333742141724, 1.3215066194534302, 1.3450623750686646, 1.3655186891555786,
//                1.3800145387649536, 1.3844491243362427, 1.3870717287063599, 1.387405514717102, 1.3948918581008911 };
//
//        double[] temp2 = { 1.3972760438919067, 1.3962270021438599, 1.3977528810501099, 1.4006139039993286, 1.4024258852005005,
//                1.3995648622512817, 1.3979912996292114, 1.386165738105774, 1.3812543153762817, 1.382351040840149,
//                1.3854981660842896, 1.3865948915481567, 1.3697148561477661, 1.3491631746292114, 1.3399602174758911,
//                1.3312817811965942, 1.3253213167190552, 1.319503903388977, 1.314449429512024, 1.296615719795227,
//                1.284408688545227, 1.2928487062454224, 1.2972832918167114, 1.2979985475540161, 1.296138882637024,
//                1.293850064277649, 1.291275143623352, 1.2933732271194458, 1.299047589302063, 1.2979985475540161,
//                1.3005257844924927, 1.3042927980422974, 1.3119221925735474, 1.3316155672073364, 1.3437272310256958,
//                1.354742169380188, 1.3633252382278442, 1.3722420930862427, 1.3784886598587036, 1.3864041566848755 };
//
//        List<Double> list1= new ArrayList<>();
//        for (double v : temp1) {
//            list1.add(v);
//        }
//
//        List<Double> list2= new ArrayList<>();
//        for (double v : temp2) {
//            list2.add(v);
//        }

//        List<Double> highPassFilteredData = mDeviceManager.getHighPassFilteredData(0, list1);
//        LogUtils.i("highPassFilteredData=" + highPassFilteredData);
//        List<Double> highPassFilteredData1 = mDeviceManager.getHighPassFilteredData(0, list2);
//        LogUtils.i("highPassFilteredData=" + highPassFilteredData1);
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
//                if(countDownTimer != null){
//                    countDownTimer.cancel();
//                    countDownTimer = null;
//                    return;
//                }
//                Random random = new Random();
//                countDownTimer = new CountDownTimer(10_000, 50){
//                    @Override
//                    public void onTick(long l) {
//                        mEmgWaveView.addData(0,random.nextFloat());
//                        mEmgWaveView.updateWaveLine();
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                    }
//                };
//                countDownTimer.start();

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
                    DeviceManager.getInstance().setSaveTime(settingValue);
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
        startRecordSampleData();
        mCollectionStatus = !mCollectionStatus;
    }

    private void stopDeviceCollect() {
        ServerManager.getInstance().sendData(new SendStopDataCollect().pack());
        DeviceManager.getInstance().setCurrentCapacitance(0);
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
    }

    /**
     * 设备回复电压值
     *
     * @param channel 电压通道 1~8
     * @param data    电压数据
     */
    @Override
    public void replyVoltage(int channel, List<Double> data) {
        LogUtils.d(String.format("channel=%s, point=%s", channel, data));
        List<Double> filterData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i % 2 == 0) {
                filterData.add(data.get(i));
            }
        }
        mEmgWaveView.addData(channel, filterData);
        if (channel == Constant.DEFAULT_CHANNEL - 1) {
            runOnUiThread(() -> mEmgWaveView.updateWaveLine());
        }
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
    public void replyCapacitance(double capacitance) {
        LogUtils.i("capacitance=" + capacitance);
        mCapacitanceWaveView.addData(capacitance);
        mCapacitanceWaveView.updateWaveLine();
    }

    /**
     * 设备回复角度值
     *
     * @param angle 角度值
     */
    @Override
    public void replyAngle(double angle) {
        LogUtils.i("angle=" + angle);
        mCapacitanceWaveView.addData(angle);
        mCapacitanceWaveView.updateWaveLine();
    }

    /**
     * 设备回复停止
     */
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
    }
}