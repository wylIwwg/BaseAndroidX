package com.jdxy.wyl.baseandroidx.network;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.widget.Toast;


import com.jdxy.wyl.baseandroidx.R;

import java.util.LinkedHashMap;
import java.util.Map;

public final class NetWorkUtils {

    public final static long SECOND = 1000;
    public final static long MINUTE = SECOND * 60;
    public final static long HOUR = MINUTE * 60;
    public final static long DAY = HOUR * 24;

    public static void copyToClipBoard(Context context , String content){
        if (!TextUtils.isEmpty(content)){
            //获取剪贴版
            ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            //创建ClipData对象
            //第一个参数只是一个标记，随便传入。
            //第二个参数是要复制到剪贴版的内容
            ClipData clip = ClipData.newPlainText("", content);
            //传入clipdata对象.
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static LinkedHashMap<String, Long> transformToTraceDetail(Map<String, Long> eventsTimeMap){
        LinkedHashMap<String, Long> traceDetailList = new LinkedHashMap<>();
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_TOTAL,getEventCostTime(eventsTimeMap, NetworkTraceBean.CALL_START, NetworkTraceBean.CALL_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_DNS,getEventCostTime(eventsTimeMap, NetworkTraceBean.DNS_START, NetworkTraceBean.DNS_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_SECURE_CONNECT,getEventCostTime(eventsTimeMap, NetworkTraceBean.SECURE_CONNECT_START, NetworkTraceBean.SECURE_CONNECT_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_CONNECT,getEventCostTime(eventsTimeMap, NetworkTraceBean.CONNECT_START, NetworkTraceBean.CONNECT_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_REQUEST_HEADERS,getEventCostTime(eventsTimeMap, NetworkTraceBean.REQUEST_HEADERS_START, NetworkTraceBean.REQUEST_HEADERS_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_REQUEST_BODY,getEventCostTime(eventsTimeMap, NetworkTraceBean.REQUEST_BODY_START, NetworkTraceBean.REQUEST_BODY_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_RESPONSE_HEADERS,getEventCostTime(eventsTimeMap, NetworkTraceBean.RESPONSE_HEADERS_START, NetworkTraceBean.RESPONSE_HEADERS_END));
        traceDetailList.put(NetworkTraceBean.TRACE_NAME_RESPONSE_BODY,getEventCostTime(eventsTimeMap, NetworkTraceBean.RESPONSE_BODY_START, NetworkTraceBean.RESPONSE_BODY_END));
        return traceDetailList;
    }

    public static long getEventCostTime(Map<String , Long> eventsTimeMap, String startName, String endName){
        if (!eventsTimeMap.containsKey(startName) || !eventsTimeMap.containsKey(endName)) {
            return 0;
        }
        Long endTime = eventsTimeMap.get(endName);
        Long start = eventsTimeMap.get(startName);
        long result = endTime - start;
        return result;
    }



    public static boolean isTimeout(Context context , String key , Long costTime){
        int value = getSettingTimeout(context, key);
        if (value <= 0 || costTime == null) {
            return false;
        }
        return costTime > value;
    }

    private static int getSettingTimeout(Context context , String key){
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "0");
        try {
            int i = Integer.parseInt(string);
            return i;
        } catch (NumberFormatException e){
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * sp 转 px
     * @param spValue               sp 值
     * @return                      px 值
     */
    public static int sp2px(Context context, final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 获取屏幕宽
     * @param context               上下文
     * @return
     */
    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高
     * @param context               上下文
     * @return
     */
    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }



    public static SpannableString getPrintSizeForSpannable(long size) {
        SpannableString spannableString;
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
        if (size < 1024) {
            spannableString = new SpannableString(String.valueOf(size) + "B");
            spannableString.setSpan(sizeSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            spannableString = new SpannableString(String.valueOf(size) + "KB");
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            String string = String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
            spannableString = new SpannableString(string);
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            size = size * 100 / 1024;
            String string = String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
            spannableString = new SpannableString(string);
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    /**
     * 使用外部浏览器打开链接
     * @param context
     * @param content
     */
    public static void openLink(Context context, String content) {
        Uri issuesUrl = Uri.parse(content);
        Intent intent = new Intent(Intent.ACTION_VIEW, issuesUrl);
        context.startActivity(intent);
    }

}
