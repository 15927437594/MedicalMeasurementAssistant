package cn.com.medicalmeasurementassistant;

import android.content.Intent;
import android.view.View;

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
        PermissionHelper.hasPermission(this);

        findViewById(R.id.tv_go_to_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_add_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementFileUtils.saveMeasurementFile("这是刚刚创建的" + System.currentTimeMillis());
            }
        });
    }
}