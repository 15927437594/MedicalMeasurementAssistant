package cn.com.medicalmeasurementassistant.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.medicalmeasurementassistant.R;

public class ToastHelper {
    public static void init() {
//        ToastUtils.setBgColor(Color.parseColor("#44000000"));
//        ToastUtils.setMsgColor(Color.parseColor("#44ffffff"));

    }

    public static void showShortDebug(String msg) {
        if (AppInfoUtils.isDebug()) {
            showShort(msg);
        }

    }
    public static void showLongDebug(String msg) {
        if (AppInfoUtils.isDebug()) {
            showLong(msg);
        }

    }
    public static void showShortRelease(String msg) {
        if (!AppInfoUtils.isDebug()) {
            showShort(msg);
        }

    }

    public static void showShort(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        Toast toast = new Toast(AppInfoUtils.getApplication());
        TextView inflate = (TextView) View.inflate(AppInfoUtils.getApplication(), R.layout.layout_toast_custom, null);
        inflate.setText(msg);
        toast.setView(inflate);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLong(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        Toast toast = new Toast(AppInfoUtils.getApplication());
        TextView inflate = (TextView) View.inflate(AppInfoUtils.getApplication(), R.layout.layout_toast_custom, null);
        inflate.setText(msg);
        toast.setView(inflate);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
