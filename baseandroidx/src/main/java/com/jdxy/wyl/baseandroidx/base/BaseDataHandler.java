package com.jdxy.wyl.baseandroidx.base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.jdxy.wyl.baseandroidx.tools.IConfigs;

import java.lang.ref.WeakReference;

/**
 * Created by wyl on 2019/5/22.
 */
public class BaseDataHandler extends Handler {
    WeakReference<Activity> mReference;

    public interface MessageListener {
        void showError(String error);

        void userHandler(Message msg);
    }

    public MessageListener mMessageListener;

    public void setMessageListener(MessageListener errorListener) {
        mMessageListener = errorListener;
    }

    public BaseDataHandler(Activity reference) {
        mReference = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        //常用消息处理
        if (mMessageListener != null)
            switch (msg.what) {
                case IConfigs.NET_CONNECT_ERROR:
                    mMessageListener.showError(msg.obj == null ? "网络连接异常" : (String) msg.obj);
                    break;
                case IConfigs.NET_SERVER_ERROR:
                    mMessageListener.showError(msg.obj == null ? "服务器异常" : (String) msg.obj);
                    break;
                case IConfigs.NET_UNKNOWN_ERROR:
                    mMessageListener.showError(msg.obj == null ? "未知错误" : (String) msg.obj);
                    break;
                case IConfigs.NET_TIMEOUT:
                    mMessageListener.showError(msg.obj == null ? "网络连接超时" : (String) msg.obj);
                    break;

            }
        //自定义消息处理
        if (mMessageListener != null)
            mMessageListener.userHandler(msg);
    }
}
