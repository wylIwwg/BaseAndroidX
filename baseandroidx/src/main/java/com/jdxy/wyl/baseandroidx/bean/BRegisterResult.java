package com.jdxy.wyl.baseandroidx.bean;

/**
 * Created by wyl on 2020/1/20.
 */
public class BRegisterResult {
    private String registerStr;//注册码
    private int registerCode;//注册状态
    private boolean registered;//是否注册


    @Override
    public String toString() {
        return "BRegisterResult{" +
                "registerStr='" + registerStr + '\'' +
                ", registerCode=" + registerCode +
                ", registered=" + registered +
                '}';
    }

    public String getRegisterStr() {
        return registerStr == null ? "" : registerStr;
    }

    public void setRegisterStr(String registerStr) {
        this.registerStr = registerStr;
    }

    public int getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(int registerCode) {
        this.registerCode = registerCode;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
