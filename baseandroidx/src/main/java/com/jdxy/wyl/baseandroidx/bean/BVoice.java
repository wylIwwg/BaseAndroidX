package com.jdxy.wyl.baseandroidx.bean;

/**
 * Created by wyl on 2019/8/16.
 */
public class BVoice {

    private String patientName;//当前就诊
    private String patientNum;//当前就诊排队号
    private String patientId;//当前就诊id
    private String doctorName;//员工
    private String docid;//医生
    private String departmentName;//位置
    private String clinicName;//位置
    private String clinicId;//位置ID
    private String count;//等候人数
    private String nextName;//下一位
    private String nextNum;//下一位排队号
    private String roNum;//门牌号

    private String type;//呼叫类型 就诊/复诊/

    public String getRoNum() {
        return roNum == null ? "" : roNum;
    }

    public void setRoNum(String roNum) {
        this.roNum = roNum == null ? "" : roNum;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type == null ? "" : type;
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
}

