package cn.com.medicalmeasurementassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

import cn.com.medicalmeasurementassistant.app.ProjectApplication;

public class TestUtil {

    // 调用系統方法分享文件

    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);

            Uri uri = null;

            // 判断版本大于等于7.0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "项目包名.fileprovider"即是在清单文件中配置的authorities

                uri = FileProvider.getUriForFile(ProjectApplication.getApp(), ProjectApplication.getApp().getPackageName()+".MeasurementFileProvider", file);

                // 给目标应用一个临时授权

                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                uri = Uri.fromFile(file);

            }

            share.putExtra(Intent.EXTRA_STREAM, uri);

            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件

            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(share, "分享文件"));

        }

    }

// 根据文件后缀名获得对应的MIME类型。

    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        String mime = "*/*";

        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);

            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }
}
