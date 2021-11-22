package cn.com.medicalmeasurementassistant.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import cn.com.medicalmeasurementassistant.R;
import cn.com.medicalmeasurementassistant.app.ProjectApplication;


public class StringSpanUtils {

    public static void spanColorOperate(TextView textView, String content, @ColorRes int colorRes, int start, int end) {
        SpannableString spannableString = new SpannableString(content);
        ForegroundColorSpan span = new ForegroundColorSpan(ProjectApplication.getApp().getResources().getColor(colorRes));
        spannableString.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannableString);
    }

    public static void spanBoldOperate(TextView textView, String content, int start, int end) {
        SpannableString spannableString = new SpannableString(content);
        StyleSpan span = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannableString);
    }

    public static void spanProtocol(TextView textView, String content) {
        String userStr = "《用户协议》";
        String privateStr = "《隐私政策》";
        int userIndex = content.indexOf(userStr);
        int privateIndex = content.indexOf(privateStr);
        SpannableString spannableString = new SpannableString(content);
        // 文字加粗
        StyleSpan span = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(span, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // 变色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(ProjectApplication.getApp(), R.color.text_color_search_high_light));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(ContextCompat.getColor(ProjectApplication.getApp(),R.color.text_color_search_high_light));
        spannableString.setSpan(colorSpan, userIndex, userIndex+userStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan2, privateIndex, privateIndex+privateStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // 超链接
        spannableString.setSpan(getClickableSpan(ProjectApplication.getApp(),1), userIndex, userIndex+userStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(getClickableSpan(ProjectApplication.getApp(),2), privateIndex, privateIndex+privateStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        if (textView != null) {
            textView.setHighlightColor(ProjectApplication.getApp().getResources().getColor(android.R.color.transparent));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableString);
            textView.setText(spannableString);
        }
    }

    private static ClickableSpan getClickableSpan(Context context, int type) {
        return new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (type == 1) {
//                    WebViewActivity.launchUserServiceProtocol(context);
                } else {
//                    WebViewActivity.launchPrivateProtocol(context);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                // 文字不变色
                ds.setUnderlineText(false);

            }
        };
    }

}
