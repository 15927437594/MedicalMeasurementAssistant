package cn.com.medicalmeasurementassistant;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.ui.FileSelectorActivity;
import cn.com.medicalmeasurementassistant.utils.MeasurementFileUtils;
import cn.com.medicalmeasurementassistant.utils.PermissionHelper;

public class MainActivity extends BaseKotlinActivity {

    private TextView msgTv;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        PermissionHelper.hasPermission(this);
        msgTv = findViewById(R.id.msg_tv);

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyClientSocketManager.INSTANCE.sendMsg(System.currentTimeMillis()+"");
                Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.tv_add_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyServerSocketManager.INSTANCE.setTextView(msgTv);

//                MyWifiManager.INSTANCE.openWifi(getActivity());
//                NetworkUtils.setWifiEnabled(true);


                MeasurementFileUtils.saveMeasurementFile("这是刚刚创建的" + System.currentTimeMillis());
            }
        });

        findViewById(R.id.tv_go_to_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyClientSocketManager.INSTANCE.setTextView(msgTv);
//                Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
//                startActivity(intent);
            }
        });
    }


}