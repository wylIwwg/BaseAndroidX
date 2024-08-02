package com.jdxy.wyl.baseandroidx.bean;

import java.util.ArrayList;
import java.util.List;

public class BPushData {
    private List<BPatient> currentList;

    private BDoctor doctorInfo;
    private BDept deptInfo;
    private List<BPatient> waitingList;
    private List<BPatient> passedList;

    public List<BPatient> getCurrentList() {
        if (currentList == null) {
            return new ArrayList<>();
        }
        return currentList;
    }

    public void setCurrentList(List<BPatient> currentList) {
        this.currentList = currentList;
    }

    public BDoctor getDoctorInfo() {
        return doctorInfo;
    }

    public void setDoctorInfo(BDoctor doctorInfo) {
        this.doctorInfo = doctorInfo;
    }

    public BDept getDeptInfo() {
        return deptInfo;
    }

    public void setDeptInfo(BDept deptInfo) {
        this.deptInfo = deptInfo;
    }

    public List<BPatient> getWaitingList() {
        if (waitingList == null) {
            return new ArrayList<>();
        }
        return waitingList;
    }

    public void setWaitingList(List<BPatient> waitingList) {
        this.waitingList = waitingList;
    }

    public List<BPatient> getPassedList() {
        if (passedList == null) {
            return new ArrayList<>();
        }
        return passedList;
    }

    public void setPassedList(List<BPatient> passedList) {
        this.passedList = passedList;
    }
}
