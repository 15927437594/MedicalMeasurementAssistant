package cn.com.medicalmeasurementassistant.utils;

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
//        if (StringUtils.isEmpty(msg)) {
//            return;
//        }
//        Toast toast = new Toast(AppInfoUtils.getApplication());
//        TextView inflate = (TextView) View.inflate(AppInfoUtils.getApplication(), R.layout.charge_lay_toast_custom, null);
//        inflate.setText(msg);
//        toast.setView(inflate);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.show();
    }

    public static void showLong(String msg) {
//        if (StringUtils.isEmpty(msg)) {
//            return;
//        }
//        Toast toast = new Toast(AppInfoUtils.getApplication());
//        TextView inflate = (TextView) View.inflate(AppInfoUtils.getApplication(), R.layout.charge_lay_toast_custom, null);
//        inflate.setText(msg);
//        toast.setView(inflate);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
    }

    public static void showBatteryLong(int duration) {
//        Toast toast = new Toast(AppInfoUtils.getApplication());
//        View inflate = View.inflate(AppInfoUtils.getApplication(), R.layout.charge_lay_toast_battery_info, null);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        inflate.findViewById(R.id.tv_tip_content).setOnClickListener(v->{
//            AppUtils.launchApp(AppInfoUtils.getApplication().getPackageName());
//        });
//        inflate.setLayoutParams(layoutParams);
//        toast.setView(inflate);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//        toast.show();
    }

}
