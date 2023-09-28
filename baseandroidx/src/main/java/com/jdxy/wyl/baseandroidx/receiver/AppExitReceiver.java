package com.jdxy.wyl.baseandroidx.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;

public class AppExitReceiver extends BroadcastReceiver {
    /*
    * 必须添加 intent action
    *   <intent-filter>
    com.jdxy.mediaplayer.close
                    <action android:name="com.jdxy.qbjydhospwsy.doorplate" />
                </intent-filter>
    * */
    private static final String TAG = "  AppExitReceiver ";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(context.getPackageName()) || intent.getAction().equals("com.jdxy.mediaplayer.close"))
                AppUtils.exitApp();
        }
    }
}
