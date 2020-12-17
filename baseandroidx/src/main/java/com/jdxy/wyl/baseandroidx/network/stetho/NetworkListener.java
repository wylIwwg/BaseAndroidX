package com.jdxy.wyl.baseandroidx.network.stetho;


import com.jdxy.wyl.baseandroidx.network.NetworkTraceBean;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2019/07/22
 *     desc  : EventListener子类
 *     revise:
 * </pre>
 */
public class NetworkListener extends EventListener {

    private static final String TAG = "NetworkEventListener";
    private static AtomicInteger mNextRequestId = new AtomicInteger(0);
    private String mRequestId ;

    public static Factory get(){
        Factory factory = new Factory() {
            @NotNull
            @Override
            public EventListener create(@NotNull Call call) {
                return new NetworkListener();
            }
        };
        return factory;
    }

    @Override
    public void callStart(@NotNull Call call) {
        super.callStart(call);
        //mRequestId = mNextRequestId.getAndIncrement() + "";
        //getAndAdd，在多线程下使用cas保证原子性
        mRequestId = String.valueOf(mNextRequestId.getAndIncrement());
        ToolLog.e(TAG+"-------callStart---requestId-----"+mRequestId);
        saveEvent(NetworkTraceBean.CALL_START);
        saveUrl(call.request().url().toString());
    }

    @Override
    public void dnsStart(@NotNull Call call, @NotNull String domainName) {
        super.dnsStart(call, domainName);
        ToolLog.e(TAG, "dnsStart");
        saveEvent(NetworkTraceBean.DNS_START);
    }

    @Override
    public void dnsEnd(@NotNull Call call, @NotNull String domainName, @NotNull List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        ToolLog.e(TAG, "dnsEnd");
        saveEvent(NetworkTraceBean.DNS_END);
    }

    @Override
    public void connectStart(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        ToolLog.e(TAG, "connectStart");
        saveEvent(NetworkTraceBean.CONNECT_START);
    }

    @Override
    public void secureConnectStart(@NotNull Call call) {
        super.secureConnectStart(call);
        ToolLog.e(TAG, "secureConnectStart");
        saveEvent(NetworkTraceBean.SECURE_CONNECT_START);
    }

    @Override
    public void secureConnectEnd(@NotNull Call call, @Nullable Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        ToolLog.e(TAG, "secureConnectEnd");
        saveEvent(NetworkTraceBean.SECURE_CONNECT_END);
    }

    @Override
    public void connectEnd(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress,
                           @NotNull Proxy proxy, @Nullable Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        ToolLog.e(TAG, "connectEnd");
        saveEvent(NetworkTraceBean.CONNECT_END);
    }

    @Override
    public void connectFailed(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol, @NotNull IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        ToolLog.e(TAG, "connectFailed");
    }

    @Override
    public void requestHeadersStart(@NotNull Call call) {
        super.requestHeadersStart(call);
        ToolLog.e(TAG, "requestHeadersStart");
        saveEvent(NetworkTraceBean.REQUEST_HEADERS_START);
    }

    @Override
    public void requestHeadersEnd(@NotNull Call call, @NotNull Request request) {
        super.requestHeadersEnd(call, request);
        ToolLog.e(TAG, "requestHeadersEnd");
        saveEvent(NetworkTraceBean.REQUEST_HEADERS_END);
    }

    @Override
    public void requestBodyStart(@NotNull Call call) {
        super.requestBodyStart(call);
        ToolLog.e(TAG, "requestBodyStart");
        saveEvent(NetworkTraceBean.REQUEST_BODY_START);
    }

    @Override
    public void requestBodyEnd(@NotNull Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        ToolLog.e(TAG, "requestBodyEnd");
        saveEvent(NetworkTraceBean.REQUEST_BODY_END);
    }

    @Override
    public void responseHeadersStart(@NotNull Call call) {
        super.responseHeadersStart(call);
        ToolLog.e(TAG, "responseHeadersStart");
        saveEvent(NetworkTraceBean.RESPONSE_HEADERS_START);
    }

    @Override
    public void responseHeadersEnd(@NotNull Call call, @NotNull Response response) {
        super.responseHeadersEnd(call, response);
        ToolLog.e(TAG, "responseHeadersEnd");
        saveEvent(NetworkTraceBean.RESPONSE_HEADERS_END);
    }

    @Override
    public void responseBodyStart(@NotNull Call call) {
        super.responseBodyStart(call);
        ToolLog.e(TAG, "responseBodyStart");
        saveEvent(NetworkTraceBean.RESPONSE_BODY_START);
    }

    @Override
    public void responseBodyEnd(@NotNull Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        ToolLog.e(TAG, "responseBodyEnd");
        saveEvent(NetworkTraceBean.RESPONSE_BODY_END);
    }

    @Override
    public void callEnd(@NotNull Call call) {
        super.callEnd(call);
        ToolLog.e(TAG, "callEnd");
        saveEvent(NetworkTraceBean.CALL_END);
    }

    @Override
    public void callFailed(@NotNull Call call, @NotNull IOException ioe) {
        super.callFailed(call, ioe);
        ToolLog.e(TAG, "callFailed");
    }


    private void saveEvent(String eventName){
    }

    private void saveUrl(String url){
    }

}
