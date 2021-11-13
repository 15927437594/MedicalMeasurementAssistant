package cn.com.medicalmeasurementassistant;

import android.content.Intent;

import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.ui.FileSelectorActivity;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.PermissionHelper;

public class MainActivity extends BaseKotlinActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        boolean hasPermission = PermissionHelper.hasPermission(this);
        LogUtils.i("hasPermission=" + hasPermission);
        findViewById(R.id.tv_go_to_file).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
            startActivity(intent);
        });
    }
}