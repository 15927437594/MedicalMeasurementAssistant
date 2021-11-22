package cn.com.medicalmeasurementassistant.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class PermissionHelper {
    public static final int PERMISSION_CODE = 1001;

    private static String[] forcePermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static boolean isRequest = true;

    public static boolean hasPermission(Activity activity) {
        return hasPermission(activity, null);
    }

    public static boolean hasPermission(Activity activity, String[] newPermissions) {

        if (newPermissions != null) {
            forcePermissions = newPermissions;
        }

        try {
            // 有所有权限
            boolean hasAllPermission = true;
            for (String forcePermission : forcePermissions) {
                if (ActivityCompat.checkSelfPermission(activity, forcePermission) != PermissionChecker.PERMISSION_GRANTED) {
                    hasAllPermission = false;
                    break;
                }
            }

            if (!hasAllPermission) {
                isRequest = true;
                ActivityCompat.requestPermissions(activity, forcePermissions, PERMISSION_CODE);
            }
            return hasAllPermission;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean onRequestPermissionsResult(Activity activity, String[] permissions, int[] grantResults) {
        if (!isRequest) {
            return false;
        }
        isRequest = false;
        try {
            for (int i = 0; i < permissions.length; i++) {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                    if (grantResults == null || grantResults[i] != PermissionChecker.PERMISSION_GRANTED) {
                        showPermissionTipDialog(activity, 1);
                    }
                } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
                    if (grantResults == null || grantResults[i] != PermissionChecker.PERMISSION_GRANTED) {
                        showPermissionTipDialog(activity, 2);
                    }
                } else if (Manifest.permission.CAMERA.equals(permissions[i])) {
                    if (grantResults == null || grantResults[i] != PermissionChecker.PERMISSION_GRANTED) {
                        showPermissionTipDialog(activity, 3);
                    }
                }
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void showPermissionTipDialog(Activity activity, int type) {
        String massage;
        switch (type) {
            case 1:
                massage = "我们需要读取手机状态权限，如果不开启，可能会影响您的奖励";
                break;
            case 2:
                massage = "我们需要内存卡存储权限，如果不开启，部分功能可能无法使用！";
                break;
            default:
                massage = "我们需要摄像头开启权限，如果不开启，部分功能可能无法使用！";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示")
                .setMessage(massage)
                .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtils.launchAppDetailsSettings(AppInfoUtils.getApplication(), AppInfoUtils.getApplication().getPackageName());
                    }
                }).show();
    }
}
