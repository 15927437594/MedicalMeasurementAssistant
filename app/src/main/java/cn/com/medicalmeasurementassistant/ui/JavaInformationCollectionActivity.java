package cn.com.medicalmeasurementassistant.ui;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
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
import cn.com.medicalmeasurementassistant.entity.SettingParamsBean;
import cn.com.medicalmeasurementassistant.listener.DeviceInfoListener;
import cn.com.medicalmeasurementassistant.listener.OnWaveCountChangeListener;
import cn.com.medicalmeasurementassistant.manager.DeviceManager;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.manager.WaveManager;
import cn.com.medicalmeasurementassistant.protocol.send.SendStartDataCollect;
import cn.com.medicalmeasurementassistant.protocol.send.SendStopDataCollect;
import cn.com.medicalmeasurementassistant.ui.dialog.InputFileNameDialogKt;
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
    private MyWaveView mEmgWaveView;
    private MyWaveView mCapacitanceWaveView;
    private CountDownTimer timer1;

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
        mEmgWaveFrameLayout = findViewById(R.id.frameLayout_wave_pattern);
        mCapacitanceWaveFrameLayout = findViewById(R.id.frameLayout_wave_pattern2);
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
    }

    @Override
    public void initListener() {
        mDeviceManager.setDeviceInfoListener(this);
        setClick(R.id.iv_file_list);
        setClick(R.id.iv_file_save);
        setClick(R.id.stv_setting_params);
        setClick(R.id.stv_collect_angle);
        setClick(R.id.iv_collect_operate);

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
                        fileName -> MeasurementFileUtils.saveMeasurementFile(fileName, mDeviceManager.getOriginalData(), mDeviceManager.getFilterData()));
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
                if (!mDeviceManager.isDeviceOpen()) {
                    ToastHelper.showShort("请打开设备");
                    return;
                }
                mCollectionStatus = !mCollectionStatus;
                if (mCollectionStatus) {
                    // 启动
                    mCollectionIv.setImageResource(R.drawable.icon_collect_stop);
                    mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.electrode_text_color_on));
                    // 此处需要开始计时
                    ServerManager.getInstance().sendData(new SendStartDataCollect().pack());
//                    Random random = new Random();
//                    if (timer1 != null) {
//                        timer1.cancel();
//                    }
//                    timer1 = new CountDownTimer(10_000, 50) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//                            for (int i = 0; i < 8; i++) {
//                                float v = random.nextFloat() * 2 - 2;
//                                replyVoltage(i + 1, v);
//                            }
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            for (int i = 0; i < 8; i++) {
//                                float v = random.nextFloat() * 2 - 2;
//                                replyVoltage(i + 1, v);
//                            }
//                        }
//                    };
//                    timer1.start();


                } else {
                    // 停止
                    mCollectionIv.setImageResource(R.drawable.icon_collect_start);
                    mCollectionTv.setText(getString(R.string.text_collect_start));
                    mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.theme_color));
                    // 此处需要停止计时
                    ServerManager.getInstance().sendData(new SendStopDataCollect().pack());

                    if (timer1 != null) {
                        timer1.cancel();
                    }

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void replyHandshake(List<Integer> data) {
        mDeviceManager.setDeviceOpen(true);
        runOnUiThread(() -> ToastHelper.showShort("设备打开成功"));
    }

    @Override
    public void replyStartDataCollect(List<Integer> data) {
        mDeviceManager.setDeviceStart(true);
        mDeviceManager.getOriginalData().clear();
        mDeviceManager.getFilterData().clear();
        runOnUiThread(() -> ToastHelper.showShort("设备开始采集数据"));
    }

    @Override
    public void replyVoltage(int channel, float point) {
        mEmgWaveView.addData(channel - 1, point);
        //TODO 最好在更新所有通道的数据后调用,避免调用太多次
        if (channel == 8) {
            mEmgWaveView.postInvalidate();
        }
    }

    @Override
    public void replyStopDataCollect(List<Integer> data) {
        runOnUiThread(() -> ToastHelper.showShort("设备停止采集数据"));
        mDeviceManager.setDeviceStart(false);
    }

    @Override
    public void replyCapacitance(float capacitance) {
        LogUtils.i("capacitance=" + capacitance);
    }

    @Override
    public void waveCountChange() {
        List<SettingParamsBean.ChannelBean> chanelBeans = SettingParamsBean.getInstance().getChanelBeans();
        mEmgWaveView.clearChannelData();
        for (int position = 0, l = chanelBeans.size(); position < l; position++) {
            mEmgWaveView.changeChannelStatus(position, chanelBeans.get(position).getChannelStatus());
        }
    }

    private void initEmgView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mEmgWaveView = new MyWaveView(getActivity());
        mEmgWaveView.setxAxisDesc("时间/s");
        mEmgWaveView.setyAxisDesc("电压/mV");
        mEmgWaveFrameLayout.addView(mEmgWaveView, layoutParams);
    }

    private void initCapacitanceView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mCapacitanceWaveView = new MyWaveView(getActivity());
        mCapacitanceWaveView.setxAxisDesc("时间/s");
        mCapacitanceWaveView.setyAxisDesc("电容/mV");
        for (int i = 0; i < 8; i++) {
            mCapacitanceWaveView.changeChannelStatus(i, false);
        }
        mCapacitanceWaveFrameLayout.addView(mCapacitanceWaveView, layoutParams);
    }
}