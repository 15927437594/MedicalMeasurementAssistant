package cn.com.medicalmeasurementassistant.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * user: Created by jid on 2020/11/2
 * email: jid@hwtc.com.cn
 * description:Log工具,打印当前log方法名和当前行数(LogLevel>=Info时将log写入内置存储中)
 */
public class LogUtils {
    // 自定义Tag的前缀，可以是作者名
    public static String customTagPrefix = "MedicalMeasurementAssistant";
    // SD卡中的根目录
    public static String LOG_PATH = "";

    private static final DateFormat fileFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
    private static final DateFormat logFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.SIMPLIFIED_CHINESE);

    // 容许打印日志的类型,默认是true,设置为false则不打印
    public static boolean allowD = true;
    public static boolean allowE = true;
    public static boolean allowI = true;
    public static boolean allowV = true;
    public static boolean allowW = true;
    public static boolean allowWtf = true;

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s[line:%d]";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(Locale.SIMPLIFIED_CHINESE, tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    /**
     * 自定义的logger
     */
    public static CustomLogger customLogger;

    public interface CustomLogger {
        void d(String tag, String content);

        void d(String tag, String content, Throwable e);

        void e(String tag, String content);

        void e(String tag, String content, Throwable e);

        void i(String tag, String content);

        void i(String tag, String content, Throwable e);

        void v(String tag, String content);

        void v(String tag, String content, Throwable e);

        void w(String tag, String content);

        void w(String tag, String content, Throwable e);

        void w(String tag, Throwable tr);

        void wtf(String tag, String content);

        void wtf(String tag, String content, Throwable e);

        void wtf(String tag, Throwable tr);
    }

    public static void d(String content) {
        if (!allowD) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content);
        } else {
            Log.d(tag, content);
        }
    }

    public static void d(String content, Throwable e) {
        if (!allowD) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content, e);
        } else {
            Log.d(tag, content, e);
        }
    }

    public static void e(String content) {
        if (!allowE) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            Log.e(tag, content);
        }
        point(LOG_PATH, tag, content);
    }

    public static void e(Throwable e) {
        if (!allowE) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, "error", e);
        } else {
            Log.e(tag, e.getMessage(), e);
        }
        point(LOG_PATH, tag, e.getMessage());
    }

    public static void e(String content, Throwable e) {
        if (!allowE) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content, e);
        } else {
            Log.e(tag, content, e);
        }
        point(LOG_PATH, tag, e.getMessage());
    }

    public static void i(String content) {
        if (!allowI) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            Log.i(tag, content);
        }
        point(LOG_PATH, tag, content);
    }

    public static void i(String content, Throwable e) {
        if (!allowI) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content, e);
        } else {
            Log.i(tag, content, e);
        }
        point(LOG_PATH, tag, content);
    }

    public static void v(String content) {
        if (!allowV) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content);
        } else {
            Log.v(tag, content);
        }
    }

    public static void v(String content, Throwable e) {
        if (!allowV) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content, e);
        } else {
            Log.v(tag, content, e);
        }
    }

    public static void w(String content) {
        if (!allowW) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content);
        } else {
            Log.w(tag, content);
        }
        point(LOG_PATH, tag, content);
    }

    public static void w(String content, Throwable e) {
        if (!allowW) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content, e);
        } else {
            Log.w(tag, content, e);
        }
        point(LOG_PATH, tag, content);
    }

    public static void w(Throwable e) {
        if (!allowW) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, e);
        } else {
            Log.w(tag, e);
        }
        point(LOG_PATH, tag, e.toString());
    }

    public static void wtf(String content) {
        if (!allowWtf) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content);
        } else {
            Log.wtf(tag, content);
        }
        point(LOG_PATH, tag, content);
    }

    public static void wtf(String content, Throwable e) {
        if (!allowWtf) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, content, e);
        } else {
            Log.wtf(tag, content, e);
        }
        point(LOG_PATH, tag, content);
    }

    public static void wtf(Throwable e) {
        if (!allowWtf) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.wtf(tag, e);
        } else {
            Log.wtf(tag, e);
        }
        point(LOG_PATH, tag, e.toString());
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void point(String path, String tag, String msg) {
        if (isSDAvailable()) {
            String fileTime = fileFormatter.format(new Date());
            String logTime = logFormatter.format(new Date());
            path = path + "/log-" + fileTime + ".log";

            File file = new File(path);
            if (!file.exists()) {
                FileUtils.createDipPath(path);
            }
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                out.write(logTime + " " + tag + " " + msg + "\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ReusableFormatter {

        private final Formatter formatter;
        private final StringBuilder builder;

        public ReusableFormatter() {
            builder = new StringBuilder();
            formatter = new Formatter(builder);
        }

        public String format(String msg, Object... args) {
            formatter.format(msg, args);
            String s = builder.toString();
            builder.setLength(0);
            return s;
        }
    }

    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || TextUtils.isEmpty(LOG_PATH);
    }
}

