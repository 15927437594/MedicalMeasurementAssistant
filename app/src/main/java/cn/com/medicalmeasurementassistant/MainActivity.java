package cn.com.medicalmeasurementassistant;

import android.view.View;
import android.widget.TextView;

import cn.com.medicalmeasurementassistant.base.BaseKotlinActivity;
import cn.com.medicalmeasurementassistant.utils.PermissionHelper;
import cn.com.medicalmeasurementassistant.utils.communijava.ServerSocketJava;
import cn.com.medicalmeasurementassistant.utils.communijava.SocketJava;

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
                SocketJava.sendMsg(System.currentTimeMillis()+"");
//                MyServerSocketManager.INSTANCE.sendMsg(System.currentTimeMillis()+"");
//                MyClientSocketManager.INSTANCE.sendMsg(System.currentTimeMillis()+"");
//                Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
//                startActivity(intent);
            }
        });

        findViewById(R.id.tv_add_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyServerSocketManager.INSTANCE.setTextView(msgTv);
                ServerSocketJava.startServerSocket(msgTv);
//                MyWifiManager.INSTANCE.openWifi(getActivity());
//                NetworkUtils.setWifiEnabled(true);


//                MeasurementFileUtils.saveMeasurementFile("这是刚刚创建的" + System.currentTimeMillis());
            }
        });

        findViewById(R.id.tv_go_to_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketJava.startSocket(msgTv);
//                MyClientSocketManager.INSTANCE.setTextView(msgTv);
//                Intent intent = new Intent(MainActivity.this, FileSelectorActivity.class);
//                startActivity(intent);
            }
        });
    }


}