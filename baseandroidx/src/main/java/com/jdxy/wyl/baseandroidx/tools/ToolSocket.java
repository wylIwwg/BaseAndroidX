package com.jdxy.wyl.baseandroidx.tools;

import android.os.Message;
import android.text.TextUtils;

import com.jdxy.wyl.baseandroidx.base.BaseDataHandler;
import com.jdxy.wyl.baseandroidx.bean.BPulse;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ToolSocket {

    private static final String TAG = " ToolSocket ";
    static ToolSocket mSocket = new ToolSocket();

    BaseDataHandler mDataHandler;

    public ToolSocket setDataHandler(BaseDataHandler dataHandler) {
        mDataHandler = dataHandler;
        return this;
    }

    public static ToolSocket getInstance() {
        return mSocket;
    }

    private IConnectionManager mSocketManager;

    //将心跳喂狗
    public void feed() {
        if (mSocketManager != null && mSocketManager.getPulseManager() != null) {
            mSocketManager.getPulseManager().feed();
        }
    }


    public void stopConnect() {
        if (mSocketManager != null) {
            mSocketManager.unRegisterReceiver(adapter);
            mSocketManager.disconnect();
            mSocketManager = null;
        }
    }

    public void initSocket() {
        String ip = ToolSP.getDIYString(IConfigs.SP_IP);
        String port = ToolSP.getDIYString(IConfigs.SP_PORT_SOCKET);
        if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port)) {
            initSocket(ip, port);
        }
    }

    public void initSocket(String ip, String port) {
        if (isConnected)
            return;
        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo(ip, Integer.parseInt(port));
        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        mSocketManager = OkSocket.open(info);
        //获得当前连接通道的参配对象
        OkSocketOptions options = mSocketManager.getOption();
        //基于当前参配对象构建一个参配建造者类
        //  OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        //修改参配设置(其他参配请参阅类文档)
        //builder.setSinglePackageBytes(size);

        //设置自定义解析头
        OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(options);
        okOptionsBuilder.setPulseFrequency(5000);
        okOptionsBuilder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                /*
                 * 返回不能为零或负数的报文头长度(字节数)。
                 * 您返回的值应符合服务器文档中的报文头的固定长度值(字节数),可能需要与后台同学商定
                 */
                //  return /*固定报文头的长度(字节数)*/;
                return 8;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
     /*
         * 体长也称为有效载荷长度，
         * 该值应从作为函数输入参数的header中读取。
         * 从报文头数据header中解析有效负载长度时，最好注意参数中的byteOrder。
         * 我们强烈建议您使用java.nio.ByteBuffer来做到这一点。
         * 你需要返回有效载荷的长度,并且返回的长度中不应该包含报文头的固定长度
                        * /
                return /*有效负载长度(字节数)，固定报文头长度(字节数)除外*/


                byte[] buffer = new byte[4];//用四个字节接收报文头部
                System.arraycopy(header, 0, buffer, 0, 4);//头部记录的是多少字节
                int len = bytesToInt(buffer, 0);
                // ToolLog.e(TAG, "getBodyLength: " + (len - 8));
                return len - 8;
            }

        });

        //建造一个新的参配对象并且付给通道
        mSocketManager.option(okOptionsBuilder.build());
        //调用通道进行连接
        mSocketManager.registerReceiver(adapter);
        mSocketManager.connect();

    }


    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    public BPulse mPulseData = new BPulse();

    public BPulse getPulseData() {
        return mPulseData;

    }

    public void setPing(String ping) {
        mPulseData.setPing(ping);
        mSocketManager.getPulseManager().setPulseSendable(mPulseData);
    }

    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            ToolLog.efile(TAG, " 【socket 连接成功】");
            //连接成功其他操作...
            //链式编程调用,给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
           /* OkSocket.open(info)
                    .getPulseManager()
                    .setPulseSendable(mPulseData)//只需要设置一次,下一次可以直接调用pulse()
                    .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发*/
            isConnected = true;

            mSocketManager.getPulseManager().setPulseSendable(mPulseData).pulse();

        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            isConnected = false;
            if (mDataHandler != null) {
                Message mObtain = Message.obtain();
                mObtain.what = IConfigs.MSG_SOCKET_DISCONNECT;
                mObtain.obj = "socket断开连接：" + (e == null ? "" : e.getMessage());
                mDataHandler.sendMessage(mObtain);
            }
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            isConnected = false;
            if (mDataHandler != null) {
                Message mObtain = Message.obtain();
                mObtain.what = IConfigs.MSG_CREATE_TCP_ERROR;
                mObtain.obj = "socket连接失败：" + (e == null ? "" : e.getMessage());
                mDataHandler.sendMessage(mObtain);
            }
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            byte[] b = data.getBodyBytes();
            String str = new String(b, Charset.forName("utf-8"));
            ToolLog.e(TAG, "onSocketReadResponse: " + action + "     " + b.length + "  " + str);
            //  JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            // String type = jsonObject.get("type").getAsString();
            //处理socket消息

            if (mDataHandler != null) {
                Message msg = Message.obtain();
                msg.what = IConfigs.MSG_SOCKET_RECEIVED;
                msg.obj = str;
                mDataHandler.sendMessage(msg);
            } else {
                // showError("handler 为空");
            }

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
        /*    byte[] bytes = data.parse();
            bytes = Arrays.copyOfRange(bytes, 4, bytes.length);
            String str = new String(bytes, Charset.forName("utf-8"));
            ToolLog.e(TAG, "onSocketWriteResponse: " + action + "     " + str);*/

        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            byte[] bytes = data.parse();
            bytes = Arrays.copyOfRange(bytes, 8, bytes.length);
            String str = new String(bytes, Charset.forName("utf-8"));
            // JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            ToolLog.e(TAG, "发送心跳包：" + str);
        }
    };

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

}
