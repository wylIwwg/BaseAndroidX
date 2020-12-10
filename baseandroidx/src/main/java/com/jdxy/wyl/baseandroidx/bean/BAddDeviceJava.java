package com.jdxy.wyl.baseandroidx.bean;

public class BAddDeviceJava {
    private int state;
    private String message;
    private String facDecode;
    private int facId;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFacDecode() {
        return facDecode == null ? "" : facDecode;
    }

    public void setFacDecode(String facDecode) {
        this.facDecode = facDecode;
    }

    public int getFacId() {
        return facId;
    }

    public void setFacId(int facId) {
        this.facId = facId;
    }

    @Override
    public String toString() {
        return "AddBean{" +
                "state=" + state +
                ", message='" + message + '\'' +
                ", facDecode='" + facDecode + '\'' +
                ", facId=" + facId +
                '}';
    }
}
