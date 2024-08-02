package com.jdxy.wyl.baseandroidx.bean;

public class BDept {
    String departName;
    String clinicName;
    String departId;
    String clinicId;
    String roNum;
    String address;

    String deptName;
    String deptId;
    String clinicNum;
    String deptTibetan;
    String clinicTibetan;

    int clinicPriority;//诊室优先级
    int deptPriority;//科室优先级


    public String getDeptName() {
        return deptName == null ? "" : deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName == null ? "" : deptName;
    }

    public String getDeptId() {
        return deptId == null ? "" : deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId == null ? "" : deptId;
    }

    public String getClinicNum() {
        return clinicNum == null ? "" : clinicNum;
    }

    public void setClinicNum(String clinicNum) {
        this.clinicNum = clinicNum == null ? "" : clinicNum;
    }

    public String getDeptTibetan() {
        return deptTibetan == null ? "" : deptTibetan;
    }

    public void setDeptTibetan(String deptTibetan) {
        this.deptTibetan = deptTibetan == null ? "" : deptTibetan;
    }

    public String getClinicTibetan() {
        return clinicTibetan == null ? "" : clinicTibetan;
    }

    public void setClinicTibetan(String clinicTibetan) {
        this.clinicTibetan = clinicTibetan == null ? "" : clinicTibetan;
    }

    public int getClinicPriority() {
        return clinicPriority;
    }

    public void setClinicPriority(int clinicPriority) {
        this.clinicPriority = clinicPriority;
    }

    public int getDeptPriority() {
        return deptPriority;
    }

    public void setDeptPriority(int deptPriority) {
        this.deptPriority = deptPriority;
    }

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
