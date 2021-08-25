package cn.com.medicalmeasurementassistant.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;

public class PathUtils {

    private static String UPLOAD_PIC_PATH;
    private static String MEASUREMENT_DATA_PATH;

    public static String getSDPath(Context context) {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            if (Build.VERSION.SDK_INT >= 29) {
//Android10之后
                sdDir = context.getExternalFilesDir(null);
            } else {
                sdDir = Environment.getExternalStorageDirectory();// 获取SD卡根目录
            }
        } else {
            sdDir = Environment.getRootDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }


    public static void initPathConfig(Application application) {
        String path = getSDPath(application)+ "/";
        MEASUREMENT_DATA_PATH = path + "measurement_data";
        UPLOAD_PIC_PATH = path + "upload";
//        if (SDCardUtils.isSDCardEnable()) {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + application.getString(R.string.app_name) + "/";
//            MEASUREMENT_DATA_PATH = path + "measurement_data";
//            UPLOAD_PIC_PATH = path + "upload";
//        } else {
//            String path = application.getApplicationInfo().dataDir;
//            MEASUREMENT_DATA_PATH = path + "/measurement_data";
//            UPLOAD_PIC_PATH = path + "/upload";
//        }
    }


    public static String getMeasurementDataPath() {
        mkDirs(MEASUREMENT_DATA_PATH);
        return MEASUREMENT_DATA_PATH;
    }


    public static String getSavePicPath() {
        mkDirs(UPLOAD_PIC_PATH);
        return UPLOAD_PIC_PATH;
    }


    public static void mkDirs(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (mkdirs) {
                    LogUtils.i("ssfsf");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
