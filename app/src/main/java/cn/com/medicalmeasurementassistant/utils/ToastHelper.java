package cn.com.medicalmeasurementassistant.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;

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
        showShort(msg, 0);
    }

    public static void showShort(String msg, @DrawableRes int idRes) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        Toast toast = new Toast(AppInfoUtils.getApplication());
        View inflate = View.inflate(AppInfoUtils.getApplication(), R.layout.layout_toast_custom, null);
        TextView textView = inflate.findViewById(R.id.tv_toast_msg);
        if (idRes == 0) {
            DrawablePaddingUtilKt.hideDrawable(textView);
        } else {
            DrawablePaddingUtilKt.showDrawable(textView, idRes);
        }
        textView.setText(msg);
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
        View inflate = View.inflate(AppInfoUtils.getApplication(), R.layout.layout_toast_custom, null);
        TextView textView = inflate.findViewById(R.id.tv_toast_msg);
        textView.setText(msg);
        toast.setView(inflate);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
