package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.jdxy.wyl.baseandroidx.base.BaseDataHandler;
import com.jdxy.wyl.baseandroidx.bean.BVoice;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ToolVoice {

    private static final String TAG = "ToolVoice";


    static ToolVoice mToolVoice;
    static Object mObject = new Object();

    public ToolVoice(BaseDataHandler context) {
        mDataHandler = context;
    }

    public static ToolVoice Instance() {

        return mToolVoice;
    }

    public static ToolVoice Instance(BaseDataHandler handler) {

        if (mToolVoice == null) {
            synchronized (mObject) {
                if (mToolVoice == null) {
                    mToolVoice = new ToolVoice(handler);
                }
            }
        }
        return mToolVoice;
    }


    boolean isSpeeking = false;
    boolean isSpeakTest = false;
    int speakTimes = 0;
    int voiceCount = 1;//语音播报次数 默认1次

    BVoiceSetting mVoiceSetting;//语音设置

    Map<String, BVoice> mapVoice = new HashMap<>();
    BVoice mNext;
    String urlFinishVoice = "";

    BaseDataHandler mDataHandler;


    public ToolVoice setVoiceSetting(BVoiceSetting voiceSetting) {
        mVoiceSetting = voiceSetting;
        if (voiceSetting != null && mVoiceSetting.getVoNumber().length() > 0) {
            voiceCount = Integer.parseInt(mVoiceSetting.getVoNumber());
            voiceCount = voiceCount > 0 ? voiceCount : 1;

        }
        return this;
    }

    /**
     * 设置语音结束请求接口
     *
     * @param urlFinishVoice
     */
    public ToolVoice setUrlFinishVoice(String urlFinishVoice) {
        this.urlFinishVoice = urlFinishVoice;
        return this;
    }


    public void InitTtsListener() {
        SpeechSynthesizerListener mListener = new SpeechSynthesizerListener() {
            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        ToolLog.e(TAG, "onEvent:  TTS_EVENT_PLAYING_START ");
                        isSpeeking = true;
                        if (!isSpeakTest)
                            speakTimes++;
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        isSpeeking = false;
                        if (isSpeakTest) {
                            isSpeakTest = false;
                            ToolLog.efile(TAG, "【测试语音播报完成】");
                            return;
                        }
                        ToolLog.efile(TAG, "播放次数 speakTimes=" + speakTimes + " voiceCount=" + voiceCount);
                        if (speakTimes >= voiceCount) {//播放次数达标
                            // 呼叫成功 通知后台改状态
                            if (mapVoice != null && mapVoice.size() > 0 && mNext != null) {

                                //如果结束播报的连接不为空，就请求结束
                                if (!TextUtils.isEmpty(urlFinishVoice)) {
                                    String url = urlFinishVoice + mNext.getPatientId();
                                    OkGo.<String>get(url)
                                            .tag(this).execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            isSpeeking = false;
                                            ToolLog.efile(TAG, "onSuccess:语音播报结束： " + response.body());
                                            if (response != null && response.body().contains("1")) {
                                                //修改状态成功后再移除
                                                mapVoice.remove(mNext.getDocid());
                                                //继续播报下一个
                                                hasVoiceSpeak();

                                            } else {
                                                //重复呼叫
                                                speakTimes = 0;
                                                ttsSpeak();
                                            }
                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);
                                            //网络请求出错 重复呼叫
                                            isSpeeking = false;
                                            ttsSpeak();
                                        }
                                    });
                                } else {
                                    //修改状态成功后再移除
                                    mapVoice.remove(mNext.getDocid());
                                    //继续播报下一个
                                    hasVoiceSpeak();
                                }

                            }
                        } else {
                            //重复呼叫
                            if (mDataHandler != null)
                                mDataHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ttsSpeak();
                                    }
                                }, 1000);
                        }


                        break;
                }
            }

            @Override
            public void onError(int i, String s) {
                //语音播报失败
                isSpeeking = false;
                //重复呼叫
                speakTimes = 0;
                ttsSpeak();

            }
        };

        ToolTts.Instance().setSynthesizerListener(mListener);
    }

    //是否还有数据可以播报
    public void hasVoiceSpeak() {
        if (mapVoice != null && mapVoice.size() > 0) {
            Iterator<BVoice> mIterator = mapVoice.values().iterator();
            if (mIterator.hasNext()) {
                if (isSpeeking) {
                    return;
                }
                mNext = mIterator.next();
                speakTimes = 0;//归0
                //开始语音播报
                ttsSpeak();

            }
        }
    }

    public void addVoice(BVoice voice) {
        if (!TextUtils.isEmpty(voice.getDocid()))
            mapVoice.put(voice.getDocid(), voice);

        hasVoiceSpeak();
    }


    TextView mTextView;

    public ToolVoice setVoiceView(TextView textView) {
        mTextView = textView;
        return this;
    }

    public synchronized void ttsSpeak() {
        if (mNext != null) {
            //"请(line)(name)到(department)(room)(doctor)就诊"
            String txt = mVoiceSetting.getVoFormat();
            txt = txt.replace("name", ToolCommon.SplitStarName(mNext.getPatientName(), "*", 1, 2))
                    .replace("line", mNext.getPatientNum() + "")
                    .replace("department", mNext.getPatientName())
                    .replace("room", mNext.getClinicName())
                    .replace("doctor", mNext.getDoctorName())
                    .replace("type", mNext.getType())
                    .replace("(", "")
                    .replace(")", "");
            String voice = mVoiceSetting.getVoFormat();
            voice = voice.replace("name", mNext.getPatientName())
                    .replace("line", mNext.getPatientNum() + "")
                    .replace("department", mNext.getDepartmentName())
                    .replace("room", mNext.getClinicName())
                    .replace("doctor", mNext.getDoctorName())
                    .replace("type", mNext.getType())
                    .replace("(", "")
                    .replace(")", "")
                    .replace("一", "衣");

            if (mTextView != null) {
                mTextView.setText(txt);
            }
            ToolTts.Instance().TtsSpeak(voice);
            ToolLog.efile(TAG, "ttsSpeak txt: " + txt);
            ToolLog.efile(TAG, "ttsSpeak voice: " + voice);

        }


    }
}
