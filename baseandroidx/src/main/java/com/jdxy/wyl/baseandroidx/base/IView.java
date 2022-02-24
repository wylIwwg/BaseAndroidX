package com.jdxy.wyl.baseandroidx.base;

import com.jdxy.wyl.baseandroidx.bean.BBanner;

import java.util.List;

/**
 * Created by wyl on 2020/5/12.
 */
public interface IView {

    void showTips(int type, String message);

    void release();

    void initData();

    void showRegister(String msg);


    /**
     * 显示banner
     */
    void showBanner(List<BBanner> banners);

    /**
     * 显示数据内容
     */
    void showData();


    void addDevice(String clientId);


    void uploadScreen(String url, String sessionId);

    /**
     * 显示时间信息
     *
     * @param dateStr yyyy-MM-dd
     * @param timeStr HH:mm
     * @param week    星期
     */
    void showTime(String dateStr, String timeStr, String week);


    /**
     * 将未处理的socket信息抛出
     *
     * @param type
     * @param data
     */
    void moreMessage(String type, String data);
}
