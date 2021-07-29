package cn.com.medicalmeasurementassistant.utils;

import android.app.Application;
import android.os.Environment;

import com.blankj.utilcode.util.SDCardUtils;

import java.io.File;

import cn.com.medicalmeasurementassistant.R;

public class PathUtils {

    private static String UPLOAD_PIC_PATH;
    private static String MEASUREMENT_DATA_PATH;

    public static void initPathConfig(Application application) {
        if (SDCardUtils.isSDCardEnable()) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + application.getString(R.string.app_name) + "/";
            MEASUREMENT_DATA_PATH = path + "measurement_data";
            UPLOAD_PIC_PATH = path + "upload/";
        } else {
            String path = application.getApplicationInfo().dataDir;
            MEASUREMENT_DATA_PATH = path + "/measurement_data";
            UPLOAD_PIC_PATH = path + "/upload/";
        }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
