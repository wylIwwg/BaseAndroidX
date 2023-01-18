package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by wyl on 2018/5/14.
 */

public class ToolSP {
    private static final String TAG = " ToolSP ";
    static ToolSP mToolSP = new ToolSP();
    static SharedPreferences sp;

    public static ToolSP Init(Context context, String pkg) {
        if (sp == null) {
            sp = context.getSharedPreferences(pkg, Context.MODE_PRIVATE);
        }
        return mToolSP;
    }

    public static Map<String, ?> getAll() {
        return sp.getAll();
    }

    public static ToolSP instance() {
        if (mToolSP == null)
            mToolSP = new ToolSP();
        return mToolSP;
    }

    public void putString(String key, String value) {
        if (sp != null)
            ToolLog.efile(TAG, "提交数据string:key= " + key + "  value=" + value + "  " + sp.edit().putString(key, value).commit());
    }

    public void putBoolean(String key, boolean value) {
        if (sp != null)
            ToolLog.efile(TAG, "提交数据boolean: key= " + key + "  value=" + value + "  " + sp.edit().putBoolean(key, value).commit());
    }

    public void putInt(String key, int value) {
        if (sp != null)
            ToolLog.efile(TAG, "提交数据int: key= " + key + "  value=" + value + "  " + sp.edit().putInt(key, value).commit());
    }

    public static ToolSP putDIYString(String key, String value) {
        if (sp != null)
            ToolLog.efile(TAG, "提交数据string:key= " + key + "  value=" + value + "  " + sp.edit().putString(key, value).commit());
        return mToolSP;
    }


    public static ToolSP putDIYBoolean(String key, boolean value) {
        if (sp != null)
            ToolLog.efile(TAG, "提交数据boolean: key= " + key + "  value=" + value + "  " + sp.edit().putBoolean(key, value).commit());
        return mToolSP;
    }

    public static ToolSP putDIYInt(String key, int value) {
        if (sp != null)
            ToolLog.efile(TAG, "提交数据int: key= " + key + "  value=" + value + "  " + sp.edit().putInt(key, value).commit());
        return mToolSP;
    }

    public static String getDIYString(String key) {
        return sp.getString(key, "");
    }

    public static boolean getDIYBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static int getDIYInt(String key) {
        return sp.getInt(key, -1);
    }

    public static void clearAll() {
        if (sp != null) {
            ToolLog.efile(TAG, "手动删除本地数据");
            sp.edit().clear().apply();

        }
    }
}
