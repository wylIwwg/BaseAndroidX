package com.jdxy.wyl.baseandroidx.bean;

/**
 * Created by wyl on 2019/4/1.
 */
public class BRegister {
    private int residue;//剩余天数  -1 0 1
    private String longitude;//经度
    private String latitude;//纬度
    private String message;//
    private String registerDays;//
    private String registerDate;//
    private String mac;//


    public String getRegisterDays() {
        return registerDays == null ? "" : registerDays;
    }

    public void setRegisterDays(String registerDays) {
        this.registerDays = registerDays == null ? "" : registerDays;
    }

    public String getRegisterDate() {
        return registerDate == null ? "" : registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate == null ? "" : registerDate;
    }

    public String getMac() {
        return mac == null ? "" : mac;
    }

    public void setMac(String mac) {
        this.mac = mac == null ? "" : mac;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message == null ? "" : message;
    }

    public String getLongitude() {
        return longitude == null ? "" : longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? "" : longitude;
    }

    public String getLatitude() {
        return latitude == null ? "" : latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? "" : latitude;
    }

    public int getResidue() {
        return residue;
    }

    public void setResidue(int residue) {
        this.residue = residue;
    }

}
