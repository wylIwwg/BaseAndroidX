package com.jdxy.wyl.baseandroidx.tools;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ThreadUtils;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SynthesizerListener;
import com.jdxy.wyl.baseandroidx.base.BaseDataHandler;
import com.jdxy.wyl.baseandroidx.bean.BVoice;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizerListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ToolVoiceXF {

    private static final String TAG = "ToolVoice";


    static ToolVoiceXF mToolVoice;
    static Object mObject = new Object();

    public ToolVoiceXF(BaseDataHandler context) {
        mDataHandler = context;
    }

    public static ToolVoiceXF Instance() {

        return mToolVoice;
    }

    public static ToolVoiceXF Instance(BaseDataHandler handler) {

        if (mToolVoice == null) {
            synchronized (mObject) {
                if (mToolVoice == null) {
                    mToolVoice = new ToolVoiceXF(handler);
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


    public ToolVoiceXF setVoiceSetting(BVoiceSetting voiceSetting) {
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
    public ToolVoiceXF setUrlFinishVoice(String urlFinishVoice) {
        this.urlFinishVoice = urlFinishVoice;
        return this;
    }

    public interface SpeechEndListener {
        boolean speechEnd(BVoice patient);
    }

    SpeechEndListener mSpeechEndListener;

    public void setSpeechEndListener(SpeechEndListener synthesizerListener) {
        mSpeechEndListener = synthesizerListener;
    }

    public void InitTtsListener() {
        /**
         * 合成回调监听。
         */
        SynthesizerListener mDefaultTtsListener = new SynthesizerListener() {

            @Override
            public void onSpeakBegin() {
                //showTip("开始播放");
                ToolLog.efile(TAG, "onSpeakBegin: 开始播放 ");
                isSpeeking = true;
                if (!isSpeakTest)
                    speakTimes++;
            }

            @Override
            public void onSpeakPaused() {
            }

            @Override
            public void onSpeakResumed() {
            }

            @Override
            public void onBufferProgress(int percent, int beginPos, int endPos,
                                         String info) {
                // 合成进度
            }

            @Override
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
                // 播放进度
            }

            @Override
            public void onCompleted(SpeechError error) {

                if (error == null) {
                    ToolLog.e(TAG, "onCompleted: 播放完成 ");
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

                            //如果设置了
                            if (mSpeechEndListener != null) {
                                boolean mEnd = mSpeechEndListener.speechEnd(mNext);
                                if (mEnd) {
                                    //修改状态成功后再移除
                                    mapVoice.remove(mNext.getDocid());
                                    //继续播报下一个
                                    hasVoiceSpeak();
                                } else {
                                    //重复呼叫
                                    speakTimes = 0;
                                    ttsSpeak();
                                }
                            } else if (!TextUtils.isEmpty(urlFinishVoice)) {
                                HttpParams hp = new HttpParams();
                                hp.put("pid", mNext.getPatientId());
                                hp.put("queNum", mNext.getPatientNum());
                                ToolLog.efile(TAG, "【语音播报链接】: " + urlFinishVoice);
                                ToolLog.efile(TAG, "【语音播报参数】: " + JSON.toJSONString(hp));

                                OkGo.<String>post(urlFinishVoice)
                                        .params(hp)
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
                        ThreadUtils.executeByCachedWithDelay(new ThreadUtils.SimpleTask<Object>() {
                            @Override
                            public Object doInBackground() throws Throwable {
                                ttsSpeak();
                                return null;
                            }

                            @Override
                            public void onSuccess(Object result) {

                            }
                        }, 1, TimeUnit.SECONDS);
                        if (mDataHandler != null)
                            mDataHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 1000);
                    }
                } else {
                    ToolLog.efile(TAG, "onCompleted: 播放完成 " + error.getPlainDescription(true));
                }
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
                // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
                // 若使用本地能力，会话id为null
                if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                    String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                    ToolLog.e(TAG, "onEvent: " + "session id =" + sid);
                }
                //实时音频流输出参考
            }
        };

        ToolTtsXF.Instance().setSynthesizerListener(mDefaultTtsListener);
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

    public ToolVoiceXF setVoiceView(TextView textView) {
        mTextView = textView;
        return this;
    }

    public void TtsSpeakTest(String test) {
        isSpeakTest = true;
        ToolTtsXF.Instance().TtsSpeak(test);
        ToolLog.e(TAG, "TtsSpeakTest: " + test);
    }

    public synchronized void ttsSpeak() {
        if (mNext != null) {
            //"请(line)(name)到(department)(room)(doctor)就诊"
            String format = mVoiceSetting.getVoFormat();
            final String txt = format.replace("name", ToolCommon.SplitStarName(mNext.getPatientName(), "*", 1, 2))
                    .replace("line", mNext.getPatientNum() + "")
                    .replace("department", mNext.getDepartmentName())
                    .replace("room", mNext.getClinicName())
                    .replace("doctor", mNext.getDoctorName())
                    .replace("type", mNext.getType())
                    .replace("(", "")
                    .replace(")", "");
            String voice = format.replace("name", mNext.getPatientName())
                    .replace("line", mNext.getPatientNum() + "")
                    .replace("department", mNext.getDepartmentName())
                    .replace("room", mNext.getClinicName())
                    .replace("doctor", mNext.getDoctorName())
                    .replace("type", mNext.getType())
                    .replace("(", "")
                    .replace(")", "")
                    .replace("一", "衣");

            if (mTextView != null) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(txt);
                    }
                });

            }

            ToolTtsXF.Instance().TtsSpeak(voice);
            ToolLog.efile(TAG, "ttsSpeak txt: " + txt);
            ToolLog.efile(TAG, "ttsSpeak voice: " + voice);

        }

    }


}
