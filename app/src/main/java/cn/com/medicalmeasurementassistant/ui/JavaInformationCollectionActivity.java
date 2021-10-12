package cn.com.medicalmeasurementassistant.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.protocol.send.SendStartDataCollect;
import cn.com.medicalmeasurementassistant.protocol.send.SendStopDataCollect;
import cn.com.medicalmeasurementassistant.ui.dialog.InputFileNameDialogKt;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.SocketUtils;

public class JavaInformationCollectionActivity extends BaseKotlinActivity implements View.OnClickListener {
    // 右上角链接按钮
    private Switch mConnectionSwitch;
    // 测量状态ImageView
    private ImageView mCollectionIv;
    // 测量状态TextView
    private TextView mCollectionTv;
    // 当前测量状态，启动或者停止
    private boolean mCollectionStatus;

    @Override
    public int getLayoutId() {
        return R.layout.activity_information_collection;
    }

    @Override
    public void initView() {
        mConnectionSwitch = findViewById(R.id.iv_collect_operate_top);
        mCollectionIv = findViewById(R.id.iv_collect_operate);
        mCollectionTv = findViewById(R.id.tv_collection_status);

    }

    @Override
    public void initListener() {
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
}
