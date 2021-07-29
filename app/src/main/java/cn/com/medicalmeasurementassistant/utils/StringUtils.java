package cn.com.medicalmeasurementassistant.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.math.BigDecimal;

public class StringUtils {

    public static Spanned fromHtml(String text) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
            } else {
                return Html.fromHtml(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int parseInt(String intString) {
        try {
            return Integer.parseInt(intString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 分转元，转换为bigDecimal在toString
     *
     * @return
     */
    public static String changeF2Y(int price) {
        try {
            return BigDecimal.valueOf(Long.valueOf(price)).divide(new BigDecimal(100)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isEmpty(String content) {
        return content == null || content.length() == 0||"null".equals(content);
    }
}
