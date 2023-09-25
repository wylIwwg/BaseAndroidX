package com.jdxy.wyl.baseandroidx.bean;

public class Patient {
    private String patientName;
    private String patientNum;
    private String clinicName;//诊室
    private String state;
    private String patientId;
    private String cardNo;
    private String sex;
    private String age;
    private String idCard;

    public String getClinicName() {
        return clinicName == null ? "" : clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName == null ? "" : clinicName;
    }

    public String getPatientName() {
        return patientName == null ? "" : patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName == null ? "" : patientName;
    }

    public String getPatientNum() {
        return patientNum == null ? "" : patientNum;
    }

    public void setPatientNum(String patientNum) {
        this.patientNum = patientNum == null ? "" : patientNum;
    }

    public String getState() {
        return state == null ? "" : state;
    }

    public void setState(String state) {
        this.state = state == null ? "" : state;
    }

    public String getPatientId() {
        return patientId == null ? "" : patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId == null ? "" : patientId;
    }

    public String getCardNo() {
        return cardNo == null ? "" : cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo == null ? "" : cardNo;
    }

    public String getSex() {
        return sex == null ? "" : sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? "" : sex;
    }

    public String getAge() {
        return age == null ? "" : age;
    }

    public void setAge(String age) {
        this.age = age == null ? "" : age;
    }

    public String getIdCard() {
        return idCard == null ? "" : idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? "" : idCard;
    }
}
