package com.jdxy.wyl.baseandroidx.tools;

public interface IToolDevice {
    /**
     * 执行su命令
     *
     * @param cmd
     */
    void suExec(String cmd);


    /**
     * 安装apk（静默安装）
     *
     * @param apk apk绝对路径
     * @return
     */
    boolean installApk(String apk);

    /**
     * 卸载apk
     *
     * @param packageName 包名
     * @return
     */
    boolean uninstallApk(String packageName);

    boolean takeScreenshot(String path);

    /**
     * 设置定时开关机
     *
     * @param powerOnTime  开机时间  [8,30] 早上8:30开机
     * @param powerOffTime 关机时间 [18,30] 晚上18:30关机
     * @param seconds      秒数
     */
    void setPowerOnOff(int[] powerOnTime, int[] powerOffTime, long seconds);

    /**
     * 获取开关机时间
     * @return
     */
    String getPowerOnOffTime();

    /**
     * 设置系统时间
     *
     * @param time 时间戳
     */
    void setSystemTime(long time);

}