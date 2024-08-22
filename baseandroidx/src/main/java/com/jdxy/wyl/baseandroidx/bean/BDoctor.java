package com.jdxy.wyl.baseandroidx.bean;

/**
 * Created by wyl on 2019/8/29.
 */
public class BDoctor {
    private String doctorName;
    private String doctorLevel;
    private String doctorProfil;
    private String doctorNum;
    private String clinicName;
    private String avatar;
    private String id;
    private String introImg;
    private String docId;
    private String doctorAvatar;
    private int doctorPriority;


    public String getDoctorAvatar() {
        return doctorAvatar == null ? "" : doctorAvatar;
    }

    public void setDoctorAvatar(String doctorAvatar) {
        this.doctorAvatar = doctorAvatar == null ? "" : doctorAvatar;
    }

    public int getDoctorPriority() {
        return doctorPriority;
    }

    public void setDoctorPriority(int doctorPriority) {
        this.doctorPriority = doctorPriority;
    }

    public String getDocId() {
        return docId == null ? "" : docId;
    }

    public void setDocId(String docId) {
        this.docId = docId == null ? "" : docId;
    }

    public String getIntroImg() {
        return introImg;
    }

    public void setIntroImg(String introImg) {
        this.introImg = introImg;
    }


    public String getDoctorName() {
        return doctorName == null ? "" : doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorLevel() {
        return doctorLevel == null ? "" : doctorLevel;
    }

    public void setDoctorLevel(String doctorLevel) {
        this.doctorLevel = doctorLevel;
    }

    public String getDoctorProfil() {
        return doctorProfil == null ? "" : doctorProfil;
    }

    public void setDoctorProfil(String doctorProfil) {
        this.doctorProfil = doctorProfil;
    }

    public String getDoctorNum() {
        return doctorNum == null ? "" : doctorNum;
    }

    public void setDoctorNum(String doctorNum) {
        this.doctorNum = doctorNum;
    }

    public String getClinicName() {
        return clinicName == null ? "" : clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getAvatar() {
        return avatar == null ? "" : avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id == null ? "" : id;
    }
}
