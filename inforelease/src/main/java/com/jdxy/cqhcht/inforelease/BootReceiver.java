package com.jdxy.cqhcht.inforelease;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by wyl on 2018/7/30.
 */
/*

  <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

* <receiver android:name=".BootReceiver">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>

                <category android:name="android.intent.category.HOME"/>
            </intent-filter>
        </receiver>
* */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "  BootReceiver ";

    @Override
    public void onReceive(final Context context, Intent intent) {
        // if (intent.getAction() != null && intent.getAction().equals(action))
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开机自启  延迟2秒
                //  Intent service = new Intent(context, MyService.class);
                //  context.startService(service);

                try {
                    Thread.sleep(1000 * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent mIntent = new Intent(context, MainActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mIntent);


            }
        }).start();
    }
}
