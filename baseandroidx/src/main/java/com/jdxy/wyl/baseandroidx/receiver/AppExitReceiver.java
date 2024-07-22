package com.jdxy.wyl.baseandroidx.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

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
            String exReceiver = context.getPackageName() + ".close";
            ToolLog.efile(TAG, "收到广播：" + intent.getAction());
            if (intent.getAction().equals(exReceiver))
                AppUtils.exitApp();
        }
    }
}
