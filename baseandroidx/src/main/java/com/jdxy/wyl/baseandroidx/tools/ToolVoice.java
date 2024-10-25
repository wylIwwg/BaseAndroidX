package com.jdxy.wyl.baseandroidx.tools;


import com.jdxy.wyl.baseandroidx.bean.BVoice;

/**
 * ToolVoice工具类
 */
public class ToolVoice {

    public static final String TAG = "ToolVoice";


    static ToolVoice mToolVoice = new ToolVoice();

    public static ToolVoice instance() {
        return mToolVoice;
    }

    /**
     * 自定义播放（只有在空闲时才能播放）
     *
     * @param data
     */
    public void customVoice(String data) {
        if (ToolSP.getDIYInt(IConfigs.SP_VOICE_SOURCE) == IConfigs.VoiceType_SYSTEM
                || ToolSP.getDIYInt(IConfigs.SP_VOICE_SOURCE) == -1) {
            if (!ToolVoiceSystem.Instance().isSpeeking)
                ToolVoiceSystem.Instance().TtsSpeakTest(data);
        }
        if (ToolSP.getDIYInt(IConfigs.SP_VOICE_SOURCE) == IConfigs.VoiceType_XF) {
            if (!ToolVoiceXF.Instance().isSpeeking)
                ToolVoiceXF.Instance().TtsSpeakTest(data);
        }

    }

    /**
     * 添加播放对象到集合中
     *
     * @param mVoiceBean
     */
    public void addVoice(BVoice mVoiceBean) {
        if (ToolSP.getDIYInt(IConfigs.SP_VOICE_SOURCE) == IConfigs.VoiceType_SYSTEM || ToolSP.getDIYInt(IConfigs.SP_VOICE_SOURCE) == -1) {
            ToolVoiceSystem.Instance().addVoice(mVoiceBean);
        }
        if (ToolSP.getDIYInt(IConfigs.SP_VOICE_SOURCE) == IConfigs.VoiceType_XF) {
            ToolVoiceXF.Instance().addVoice(mVoiceBean);
        }

    }
}
