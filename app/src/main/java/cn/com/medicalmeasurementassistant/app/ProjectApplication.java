package cn.com.medicalmeasurementassistant.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;
import cn.com.medicalmeasurementassistant.utils.LogUtils;
import cn.com.medicalmeasurementassistant.utils.PathUtils;
import cn.com.medicalmeasurementassistant.utils.SocketUtils;

public class ProjectApplication extends Application {
    private static ProjectApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Utils.init(this);
        PathUtils.initPathConfig(this);
        LogUtils.LOG_PATH = getExternalFilesDir(null).getPath() + "/logs";

        String hostIp = SocketUtils.getHostIp("");
        LogUtils.i("hostIp=" + hostIp);
        ServerManager.getInstance().createServerSocket();
    }

    public static ProjectApplication getApp() {
        return application;
    }
}
