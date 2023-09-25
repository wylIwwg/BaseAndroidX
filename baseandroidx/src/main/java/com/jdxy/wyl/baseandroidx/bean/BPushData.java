package com.jdxy.wyl.baseandroidx.bean;

import java.util.ArrayList;
import java.util.List;

public class BPushData {
    private String patientName;//当前就诊
    private String patientNum;//当前就诊排队号
    private String patientId;//当前就诊id
    private String doctorName;//医生
    private String docid;//医生
    private String departmentName;//科室
    private String clinicName;//诊室
    private String clinicId;//诊室ID
    private String count;//等候人数
    private String nextName;//下一位
    private String nextNum;//下一位排队号
    private String state;//状态

    private String voiceTxt;//语音播报文字

    private List<Patient> waitingList;
    private List<Patient> passedList;
    private List<Patient> currents;

    public List<Patient> getCurrents() {
        if (currents == null) {
            return new ArrayList<>();
        }
        return currents;
    }

    public void setCurrents(List<Patient> currents) {
        this.currents = currents;
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

    public String getPatientId() {
        return patientId == null ? "" : patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId == null ? "" : patientId;
    }

    public String getDoctorName() {
        return doctorName == null ? "" : doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName == null ? "" : doctorName;
    }

    public String getDocid() {
        return docid == null ? "" : docid;
    }

    public void setDocid(String docid) {
        this.docid = docid == null ? "" : docid;
    }

    public String getDepartmentName() {
        return departmentName == null ? "" : departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName == null ? "" : departmentName;
    }

    public String getClinicName() {
        return clinicName == null ? "" : clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName == null ? "" : clinicName;
    }

    public String getClinicId() {
        return clinicId == null ? "" : clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId == null ? "" : clinicId;
    }

    public String getCount() {
        return count == null ? "" : count;
    }

    public void setCount(String count) {
        this.count = count == null ? "" : count;
    }

    public String getNextName() {
        return nextName == null ? "" : nextName;
    }

    public void setNextName(String nextName) {
        this.nextName = nextName == null ? "" : nextName;
    }

    public String getNextNum() {
        return nextNum == null ? "" : nextNum;
    }

    public void setNextNum(String nextNum) {
        this.nextNum = nextNum == null ? "" : nextNum;
    }

    public String getState() {
        return state == null ? "" : state;
    }

    public void setState(String state) {
        this.state = state == null ? "" : state;
    }

    public String getVoiceTxt() {
        return voiceTxt == null ? "" : voiceTxt;
    }

    public void setVoiceTxt(String voiceTxt) {
        this.voiceTxt = voiceTxt == null ? "" : voiceTxt;
    }

    public List<Patient> getWaitingList() {
        if (waitingList == null) {
            return new ArrayList<>();
        }
        return waitingList;
    }

    public void setWaitingList(List<Patient> waitingList) {
        this.waitingList = waitingList;
    }

    public List<Patient> getPassedList() {
        if (passedList == null) {
            return new ArrayList<>();
        }
        return passedList;
    }

    public void setPassedList(List<Patient> passedList) {
        this.passedList = passedList;
    }
}
