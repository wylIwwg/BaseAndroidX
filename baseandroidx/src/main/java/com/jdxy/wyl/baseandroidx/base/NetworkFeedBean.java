package com.jdxy.wyl.baseandroidx.base;

import java.io.Serializable;
import java.util.Map;

public class NetworkFeedBean implements Serializable {


    private String mRequestId;
    private String mUrl;
    private String mHost;
    private String mMethod;

    private Map<String, String> mRequestHeadersMap;

    private String mName;
    private int mStatus;
    private int mSize;
    private long mCostTime;
    private String mContentType;
    private String mBody;
    private Map<String, String> mResponseHeadersMap;

    private long mCreateTime;

    private String mCURL;
    private int connectTimeoutMillis;
    private int readTimeoutMillis;
    private int writeTimeoutMillis;

    public String getRequestId() {
        return mRequestId == null ? "" : mRequestId;
    }

    public void setRequestId(String requestId) {
        mRequestId = requestId == null ? "" : requestId;
    }

    public String getUrl() {
        return mUrl == null ? "" : mUrl;
    }

    public void setUrl(String url) {
        mUrl = url == null ? "" : url;
    }

    public String getHost() {
        return mHost == null ? "" : mHost;
    }

    public void setHost(String host) {
        mHost = host == null ? "" : host;
    }

    public String getMethod() {
        return mMethod == null ? "" : mMethod;
    }

    public void setMethod(String method) {
        mMethod = method == null ? "" : method;
    }

    public Map<String, String> getRequestHeadersMap() {
        return mRequestHeadersMap;
    }

    public void setRequestHeadersMap(Map<String, String> requestHeadersMap) {
        mRequestHeadersMap = requestHeadersMap;
    }

    public String getName() {
        return mName == null ? "" : mName;
    }

    public void setName(String name) {
        mName = name == null ? "" : name;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public long getCostTime() {
        return mCostTime;
    }

    public void setCostTime(long costTime) {
        mCostTime = costTime;
    }

    public String getContentType() {
        return mContentType == null ? "" : mContentType;
    }

    public void setContentType(String contentType) {
        mContentType = contentType == null ? "" : contentType;
    }

    public String getBody() {
        return mBody == null ? "" : mBody;
    }

    public void setBody(String body) {
        mBody = body == null ? "" : body;
    }

    public Map<String, String> getResponseHeadersMap() {
        return mResponseHeadersMap;
    }

    public void setResponseHeadersMap(Map<String, String> responseHeadersMap) {
        mResponseHeadersMap = responseHeadersMap;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    public String getCURL() {
        return mCURL == null ? "" : mCURL;
    }

    public void setCURL(String CURL) {
        mCURL = CURL == null ? "" : CURL;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public int getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public void setWriteTimeoutMillis(int writeTimeoutMillis) {
        this.writeTimeoutMillis = writeTimeoutMillis;
    }

    @Override
    public String toString() {
        return "NetworkFeedBean{" +
                "mRequestId='" + mRequestId + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mHost='" + mHost + '\'' +
                ", mMethod='" + mMethod + '\'' +
                ", mRequestHeadersMap=" + mRequestHeadersMap +
                ", mName='" + mName + '\'' +
                ", mStatus=" + mStatus +
                ", mSize=" + mSize +
                ", mCostTime=" + mCostTime +
                ", mContentType='" + mContentType + '\'' +
                ", mBody='" + mBody + '\'' +
                ", mResponseHeadersMap=" + mResponseHeadersMap +
                ", mCreateTime=" + mCreateTime +
                ", mCURL='" + mCURL + '\'' +
                '}';
    }
}
