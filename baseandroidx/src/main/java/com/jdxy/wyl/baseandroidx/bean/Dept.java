package com.jdxy.wyl.baseandroidx.bean;

public class Dept {
    String departName;
    String clinicName;
    String departId;
    String clinicId;
    String roNum;
    String address;

    public String getDepartName() {
        return departName == null ? "" : departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName == null ? "" : departName;
    }

    public String getClinicName() {
        return clinicName == null ? "" : clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName == null ? "" : clinicName;
    }

    public String getDepartId() {
        return departId == null ? "" : departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId == null ? "" : departId;
    }

    public String getClinicId() {
        return clinicId == null ? "" : clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId == null ? "" : clinicId;
    }

    public String getRoNum() {
        return roNum == null ? "" : roNum;
    }

    public void setRoNum(String roNum) {
        this.roNum = roNum == null ? "" : roNum;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address == null ? "" : address;
    }
}
