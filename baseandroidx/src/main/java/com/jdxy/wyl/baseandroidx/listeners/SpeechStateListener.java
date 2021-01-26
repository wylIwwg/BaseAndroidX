package com.jdxy.wyl.baseandroidx.listeners;

import com.jdxy.wyl.baseandroidx.bean.BVoice;

/**
 * 描述 TODO
 * 作者 wyl
 * 日期 2021/1/26 20:48
 */
public interface SpeechStateListener {
    boolean speechEnd(BVoice patient);

    void speechStart(BVoice patient);
}