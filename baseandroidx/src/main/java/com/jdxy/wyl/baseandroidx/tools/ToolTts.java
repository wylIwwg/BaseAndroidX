package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.StringUtils;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * Created by wyl on 2019/2/25.
 * 采用系统TextToSpeech文字转语音
 * 需安装讯飞等引擎
 */
public class ToolTts {
    private static final String TAG = "ToolTts";
    // 语音合成对象
    private TextToSpeech mTTSPlayer;//呼叫播放

    static ToolTts mToolTts;
    static Object mObject = new Object();
    static Context mContext;

    BVoiceSetting mVoiceSetting;

    public static ToolTts Instance(Context context) {
        mContext = context;
        return Instance();
    }

    public static ToolTts Instance() {
        if (mToolTts == null) {
            synchronized (mObject) {
                if (mToolTts == null) {
                    mToolTts = new ToolTts();
                    speed.put("1", 0.1f);
                    speed.put("2", 0.5f);
                    speed.put("3", 1f);
                    speed.put("4", 1.5f);
                    speed.put("5", 2f);
                }
            }
        }
        return mToolTts;
    }

    public TextToSpeech getTTSPlayer() {
        return mTTSPlayer;
    }


    static Map<String, Float> speed = new HashMap<>();

    /**
     * 参数设置
     */
    public void setParam() {
        mTTSPlayer.setOnUtteranceProgressListener(mDefaultTtsListener);

        //float pitch = TextUtils.isEmpty(mVoiceSetting.getVoPitch()) ? 50 : Float.parseFloat(mVoiceSetting.getVoPitch());
        //ToolLog.e(TAG, "设置音高 " + pitch / 100f);
        mTTSPlayer.setPitch(1f);

        //1 2 3 4 5
        String rate = TextUtils.isEmpty(mVoiceSetting.getVoSpeed()) ? "1" : mVoiceSetting.getVoSpeed();
        ToolLog.e(TAG, "设置音速 " + (speed.containsKey(rate) ? speed.get(rate) : 1f));
        mTTSPlayer.setSpeechRate(speed.containsKey(rate) ? speed.get(rate) : 1f);
        //设置合成语速
        ToolLog.efile(TAG, "初始化语音SDK: " + JSON.toJSONString(mVoiceSetting));

    }


    /**
     * 设置语言播报速度
     * 1f正常语速
     *
     * @param speechRate 0.1f - 1f -2f
     */
    public void setSpeechSpeed(String speechRate) {
        if (null != mTTSPlayer) {
            String rate = TextUtils.isEmpty(speechRate) ? "1" : speechRate;
            ToolLog.e(TAG, "设置音速 " + (speed.containsKey(rate) ? speed.get(rate) : 1f));
            mTTSPlayer.setSpeechRate(speed.containsKey(rate) ? speed.get(rate) : 1f);
        }
    }


    public void release() {
        if (null != mTTSPlayer) {
            mTTSPlayer.stop();
            mTTSPlayer.shutdown();
            mTTSPlayer = null;
        }
    }


    private UtteranceProgressListener mDefaultTtsListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            ToolLog.efile(TAG, "onSpeakBegin: 开始播放 ");
        }

        @Override
        public void onDone(String utteranceId) {
            ToolLog.e(TAG, "onCompleted: 播放完成 ");
        }

        @Override
        public void onError(String utteranceId) {

        }
    };


    /**
     * 初始化呼叫
     */
    public ToolTts InitTts() {

        if (mTTSPlayer == null) {
            mTTSPlayer = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub
                    if (null != mTTSPlayer && status == TextToSpeech.SUCCESS) {
                        //支持的语言类型(依赖讯飞语音支持中文合成)
                        int result = mTTSPlayer.setLanguage(Locale.SIMPLIFIED_CHINESE);

                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            ToolLog.efile(TAG, "onInit:语言不可用,不支持语音播报功能! " + result);
                            Toasty.error(mContext, "语言不可用,不支持语音播报功能：" + result).show();
                        } else {
                            ToolLog.efile(TAG, "initSpeech  getDefaultEngine = " + mTTSPlayer.getDefaultEngine() + "   " + result);
                            setParam();
                        }
                    }
                }
            });
        } else {
            setParam();
        }
        return this;
    }

    public ToolTts InitTtsSetting(BVoiceSetting voiceSetting) {
        this.mVoiceSetting = voiceSetting;
        InitTts();
        return this;
    }

    UtteranceProgressListener mSynthesizerListener;

    public void setSynthesizerListener(UtteranceProgressListener synthesizerListener) {
        mSynthesizerListener = synthesizerListener;
        if (mTTSPlayer != null) {
            mTTSPlayer.setOnUtteranceProgressListener(mSynthesizerListener);
        }
    }

    public void TtsSpeak(String txt) {
        if (mTTSPlayer != null && !StringUtils.isEmpty(txt)) {
            ToolLog.efile(TAG, "TtsSpeakSDK 播放语音: " + txt);
            if (!mTTSPlayer.isSpeaking()) {
                mTTSPlayer.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "1");
            }

        }
    }


}
