package cn.com.medicalmeasurementassistant;

import android.content.Intent;

import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.ui.FileSelectorActivity;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.PermissionHelper;
import cn.com.medicalmeasurementassistant.utils.SocketUtils;

public class MainActivity extends BaseKotlinActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        boolean hasPermission = PermissionHelper.hasPermission(this);
        LogUtils.i("hasPermission=" + hasPermission);
        String hostIp = SocketUtils.getHostIp("");
        LogUtils.i("hostIp=" + hostIp);
        ServerManager.getInstance().createServerSocket();

        findViewById(R.id.tv_go_to_file).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.tv_add_file).setOnClickListener(v -> MeasurementFileUtils.saveMeasurementFile("这是刚刚创建的" + System.currentTimeMillis()));
    }

    private void launcher2( Class<?> clz) {


    }
}