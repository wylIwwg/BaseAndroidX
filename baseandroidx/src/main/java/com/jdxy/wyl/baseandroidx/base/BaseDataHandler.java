package com.jdxy.wyl.baseandroidx.base;

import android.os.Handler;
import android.os.Message;

import com.jdxy.wyl.baseandroidx.tools.IConfigs;

/**
 * Created by wyl on 2019/5/22.
 */
public class BaseDataHandler extends Handler {

    public interface MessageListener {
        void showTips(int type, String message);

        void userHandler(Message msg);
    }

    public MessageListener mMessageListener;

    public void setMessageListener(MessageListener errorListener) {
        mMessageListener = errorListener;
    }

    public BaseDataHandler() {
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        //常用消息处理
        if (mMessageListener != null)
            switch (msg.what) {
                case IConfigs.NET_CONNECT_ERROR:
                    mMessageListener.showTips(IConfigs.MESSAGE_ERROR, msg.obj == null ? "网络连接异常" : (String) msg.obj);
                    return;
                case IConfigs.NET_SERVER_ERROR:
                    mMessageListener.showTips(IConfigs.MESSAGE_ERROR,msg.obj == null ? "服务器异常" : (String) msg.obj);
                    return;
                case IConfigs.NET_UNKNOWN_ERROR:
                    mMessageListener.showTips(IConfigs.MESSAGE_ERROR,msg.obj == null ? "未知错误" : (String) msg.obj);
                    return;
                case IConfigs.NET_TIMEOUT:
                    mMessageListener.showTips(IConfigs.MESSAGE_ERROR,msg.obj == null ? "网络连接超时" : (String) msg.obj);
                    return;

            }
        //自定义消息处理
        if (mMessageListener != null)
            mMessageListener.userHandler(msg);
    }
}
