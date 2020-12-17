package com.jdxy.wyl.baseandroidx.ping;


import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class NetSocket {

    private static final String TAG = " NetSocket  ";
    private static int PORT = 80;
    private static String HOST = "80";
    private static final int CONN_TIMES = 4;
    private static final String TIMEOUT = "DNS解析正常,连接超时,TCP建立失败";
    private static final String IOERR = "DNS解析正常,IO异常,TCP建立失败";
    private static final String HOSTERR = "DNS解析失败,主机地址不可达";
    private static NetSocket instance = null;
    private LDNetSocketListener listener;
    // 设置每次连接的timeout时间
    private int timeOut = 6000;
    // 表示一个互联网协议(IP)地址
    public InetAddress[] _remoteInet;
    // ip集合
    public List<String> _remoteIpList;
    private boolean[] isConnnected;
    // 用于存储三次测试中每次的RTT值
    private final long[] RttTimes = new long[CONN_TIMES];

    private NetSocket() {

    }

    public static NetSocket getInstance() {
        if (instance == null) {
            instance = new NetSocket();
        }
        return instance;
    }

    public void initListener(LDNetSocketListener listener) {
        this.listener = listener;
    }

    /**
     * 通过connect函数测试TCP的RTT时延
     */
    public boolean exec(String host) {
        return execUseJava(host);
    }


    /**
     * 使用java执行connected
     */
    private boolean execUseJava(String host) {
        if (host.contains(":")) {
            String[] mSplit = host.split(":");
            PORT = Integer.parseInt(mSplit[1]);
            HOST = mSplit[0];
            ToolLog.e("execUseJava", "execUseJava: " + host);
            InetSocketAddress mAddress = new InetSocketAddress(HOST, PORT);
            execSocket(mAddress, timeOut, 0);
            return true;
        }
        return false;

    }


    /**
     * 针对某个IP第index次connect
     */
    private void execSocket(InetSocketAddress socketAddress, int timeOut, int index) {
        Socket socket = null;
        long start = 0;
        long end = 0;
        try {
            socket = new Socket();
            start = System.currentTimeMillis();
            listener.OnNetSocketFinished("\n");
            socket.connect(socketAddress, timeOut);

            end = System.currentTimeMillis();

            ShellUtils.CommandResult mResult = ShellUtils.execCmd(String.format("ping -c 1 %s", HOST), false);
            ToolLog.e(TAG, "execSocket: " + mResult.errorMsg);
            ToolLog.e(TAG, "execSocket: " + mResult.successMsg);

            if (socket.isConnected()) {
                InputStream mOutputStream = socket.getInputStream();
                byte[] b = new byte[1024];
                int len = mOutputStream.read(b);
                String str = new String(b, 0, len);

                listener.OnNetSocketFinished("【socket连接成功】" + str);

            } else {
                listener.OnNetSocketFinished("【socket连接失败】");
            }
            listener.OnNetSocketFinished("");
            RttTimes[index] = end - start;
            ToolLog.e("TAG", "execSocket: " + RttTimes[index]);
        } catch (SocketTimeoutException e) {
            // 超时
            RttTimes[index] = -1;
            // 作为TIMEOUT标识
            e.printStackTrace();
            listener.OnNetSocketFinished("【socket连接失败】" + e.getMessage());
        } catch (IOException e) {
            // 异常
            RttTimes[index] = -2;
            // 作为IO异常标识
            e.printStackTrace();
            listener.OnNetSocketFinished("【socket连接失败】" + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            listener.OnNetSocketFinished("\n");
        }
    }

    public void resetInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public void printSocketInfo(String log) {
        listener.OnNetSocketUpdated(log);
    }

    public interface LDNetSocketListener {
        void OnNetSocketFinished(String log);

        void OnNetSocketUpdated(String log);
    }

}
