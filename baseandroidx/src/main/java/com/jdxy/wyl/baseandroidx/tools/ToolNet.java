package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.preference.PreferenceManager;

public class ToolNet {
    public static boolean isTimeout(Context context, String key, Long costTime) {
        int value = getSettingTimeout(context, key);
        if (value <= 0 || costTime == null) {
            return false;
        }
        return costTime > value;
    }

    private static int getSettingTimeout(Context context, String key) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "0");
        try {
            int i = Integer.parseInt(string);
            return i;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
