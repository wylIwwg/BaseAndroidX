package com.jdxy.wyl.baseandroidx.bean;

/**
 * Created by wyl on 2020/5/11.
 */
public class BVoiceSetting {

    private String voSex;//发音人性别
    private String voSpeed;//播放速度 值范围：[0，100]，默认：50
    private String voFormat;//播放格式
    private String voNumber;//播放次数
    private String voPitch;//音调 值范围：[0，100]，默认：50
    private String voVolume;//音量 值范围：[0，100]，默认：50
    private boolean needSave;//

    public boolean isNeedSave() {
        return needSave;
    }

    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }

    public String getVoPitch() {
        return voPitch == null ? "" : voPitch;
    }

    public void setVoPitch(String voPitch) {
        this.voPitch = voPitch == null ? "" : voPitch;
    }

    public String getVoVolume() {
        return voVolume == null ? "" : voVolume;
    }

    public void setVoVolume(String voVolume) {
        this.voVolume = voVolume == null ? "" : voVolume;
    }

    public String getVoSex() {
        return voSex == null ? "" : voSex;
    }

    public void setVoSex(String voSex) {
        this.voSex = voSex;
    }

    public String getVoSpeed() {
        return voSpeed == null ? "" : voSpeed;
    }

    public void setVoSpeed(String voSpeed) {
        this.voSpeed = voSpeed;
    }

    public String getVoFormat() {
        return voFormat == null ? "" : voFormat;
    }

    public void setVoFormat(String voFormat) {
        this.voFormat = voFormat;
    }

    public String getVoNumber() {
        return voNumber == null ? "" : voNumber;
    }

    public void setVoNumber(String voNumber) {
        this.voNumber = voNumber;
    }

}
