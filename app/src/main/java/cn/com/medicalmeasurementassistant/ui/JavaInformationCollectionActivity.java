package cn.com.medicalmeasurementassistant.ui;

import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.listener.DeviceInfoListener;
import cn.com.medicalmeasurementassistant.listener.OnWaveCountChangeListener;
import cn.com.medicalmeasurementassistant.manager.DeviceManager;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.manager.WaveManager;
import cn.com.medicalmeasurementassistant.protocol.send.SendStartDataCollect;
import cn.com.medicalmeasurementassistant.protocol.send.SendStopDataCollect;
import cn.com.medicalmeasurementassistant.ui.dialog.InputFileNameDialogKt;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.SocketUtils;
import cn.com.medicalmeasurementassistant.utils.ToastHelper;
import cn.com.medicalmeasurementassistant.utils.WaveUtils;

public class JavaInformationCollectionActivity extends BaseKotlinActivity implements View.OnClickListener, DeviceInfoListener, OnWaveCountChangeListener {
    // 右上角链接按钮
    private Switch mConnectionSwitch;
    // 测量状态ImageView
    private ImageView mCollectionIv;
    // 测量状态TextView
    private TextView mCollectionTv;
    // 当前测量状态，启动或者停止
    private boolean mCollectionStatus;
    private WaveView mWaveView1;
    private WaveView mWaveView2;
    private WaveView mWaveView3;
    private WaveView mWaveView4;
    private WaveUtils mWaveUtils;

    private Timer timer;
    private TimerTask timerTask;
    private float point = 0f;
    private int pointIndex = 0;
    private LinearLayout mWaveContainLL;
    private FrameLayout mEmgWaveFrameLayout,mDianrongWaveFrameLayout;
    private RadioGroup mRadioGroup;

    @Override
    public int getLayoutId() {
        return R.layout.activity_information_collection;
    }

    @Override
    public void initView() {
        timer = new Timer();
        mWaveUtils = new WaveUtils();
        mConnectionSwitch = findViewById(R.id.iv_collect_operate_top);
        mRadioGroup = findViewById(R.id.rg_contain);
        mCollectionIv = findViewById(R.id.iv_collect_operate);
        mCollectionTv = findViewById(R.id.tv_collection_status);
        mWaveContainLL = findViewById(R.id.ll_wave_contain);
        mEmgWaveFrameLayout = findViewById(R.id.frameLayout_wave_pattern);
        mDianrongWaveFrameLayout = findViewById(R.id.frameLayout_wave_pattern2);
        WaveManager.getInstance().addCallback(this);
//        mWaveView1 = findViewById(R.id.wave_view1);
//        mWaveView2 = findViewById(R.id.wave_view2);
//        mWaveView3 = findViewById(R.id.wave_view3);
//        mWaveView4 = findViewById(R.id.wave_view4);
    }

    @Override
    public void initListener() {
        DeviceManager.getInstance().setDeviceInfoListener(this);
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
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_option_one){
                    mDianrongWaveFrameLayout.setVisibility(View.INVISIBLE);
                    mEmgWaveFrameLayout.setVisibility(View.VISIBLE);
                }else{
                    mDianrongWaveFrameLayout.setVisibility(View.VISIBLE);
                    mEmgWaveFrameLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setClick(@IdRes int id) {
        findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_file_list:
                // 文件查找
                BaseKotlinActivity.Companion.launcherActivity(this, FileSearchActivity.class);
                break;
            case R.id.iv_file_save:
                // 文件保存，内容为我瞎写的，需要根据实际数据做修改
                InputFileNameDialogKt.showInputFileNameDialog(this,
                        fileName -> MeasurementFileUtils.saveMeasurementFile(fileName, "这是刚刚创建的" + System.currentTimeMillis()));
                break;
            case R.id.stv_setting_params:
                // 参数设置
                BaseKotlinActivity.Companion.launcherActivity(this, SettingParamsActivity.class);
                break;
            case R.id.stv_collect_angle:
                // 角度校准
                BaseKotlinActivity.Companion.launcherActivity(this, CalibrationAngleActivity.class);
                break;
            case R.id.iv_collect_operate:
                mCollectionStatus = !mCollectionStatus;
                if (mCollectionStatus) {
                    // 启动
                    mCollectionIv.setImageResource(R.drawable.icon_collect_stop);
                    mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.electrode_text_color_on));
                    // 此处需要开始计时
                    ServerManager.getInstance().sendData(new SendStartDataCollect().pack());
                } else {
                    // 停止
                    mCollectionIv.setImageResource(R.drawable.icon_collect_start);
                    mCollectionTv.setText(getString(R.string.text_collect_start));
                    mCollectionTv.setTextColor(ContextCompat.getColor(this, R.color.theme_color));
                    // 此处需要停止计时
                    ServerManager.getInstance().sendData(new SendStopDataCollect().pack());
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void replyHandshake(List<Integer> data) {
        runOnUiThread(() -> ToastHelper.showShort("设备打开成功"));
    }

    @Override
    public void replyStartDataCollect(List<Integer> data) {
        runOnUiThread(() -> ToastHelper.showShort("设备开始采集数据"));
    }

    @Override
    public void replySampledData(List<Integer> data) {
//        runOnUiThread(() -> ToastHelper.showShort("设备开始上传数据"));
    }

    @Override
    public void replyVoltageData(int channel, List<Float> data) {
        if (channel == 1) {
//            runOnUiThread(() -> mWaveUtils.showWaveData(mWaveView1, data));
            showWaveData(data);
        }
    }

    @Override
    public void replyStopDataCollect(List<Integer> data) {

    }

    private void showWaveData(List<Float> data) {
        pointIndex = 0;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (data != null && pointIndex < data.size()) {
                    point = data.get(pointIndex);
                    runOnUiThread(() -> mWaveView1.showLine(point));
                    pointIndex += 1;
                }
            }
        };
        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        timer.schedule(timerTask, 500, 50);
    }



    @Override
    public void waveCount(boolean isAdd, int position) {
        WaveView waveView = mWaveMap.get(position);
        if (isAdd) {
            if (waveView == null) {
                waveView = new WaveView(getActivity(), position + 1);
                waveView.setIsShow(true);
                mWaveMap.put(position, waveView);
            } else {
                waveView.setIsShow(true);
            }

            Map<Integer, WaveView> stu = new TreeMap<>(mWaveMap);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            layoutParams.weight = 1;
            layoutParams.topMargin = 5;

            for (Integer viewIncex : stu.keySet()) {
                WaveView view = stu.get(viewIncex);
                if (view != null) {
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    if (!view.isShow()) {
                        continue;
                    }
                    mWaveContainLL.addView(view, layoutParams);
                }

            }

        } else {
            if (waveView != null) {
                waveView.setIsShow(false);
                mWaveContainLL.removeView(waveView);
            }
        }
    }

    private final Map<Integer, WaveView> mWaveMap = new ArrayMap<>();
}
