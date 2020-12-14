package com.jdxy.wyl.baseandroidx.bean;

public class BAppType {

    private int appType;//设备类型
    private String appTypeName;//类型名称

    public BAppType() {
    }

    public BAppType(int appType, String appTypeName) {
        this.appType = appType;
        this.appTypeName = appTypeName;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getAppTypeName() {
        return appTypeName == null ? "" : appTypeName;
    }

    public void setAppTypeName(String appTypeName) {
        this.appTypeName = appTypeName == null ? "" : appTypeName;
    }
}
