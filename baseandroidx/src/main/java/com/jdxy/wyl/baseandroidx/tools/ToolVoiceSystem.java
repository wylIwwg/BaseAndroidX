package com.jdxy.wyl.baseandroidx.tools;

import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.base.BaseDataHandler;
import com.jdxy.wyl.baseandroidx.bean.BVoice;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 调用系统安装引擎进行文字转语音播放
 */
public class ToolVoiceSystem {

    public static final String TAG = "ToolVoice系统语音";


    static ToolVoiceSystem mToolVoice;
    static Object mObject = new Object();

    public ToolVoiceSystem(BaseDataHandler context) {
        mDataHandler = context;
    }

    public static ToolVoiceSystem Instance() {

        return mToolVoice;
    }

    public static ToolVoiceSystem Instance(BaseDataHandler handler) {

        if (mToolVoice == null) {
            synchronized (mObject) {
                if (mToolVoice == null) {
                    mToolVoice = new ToolVoiceSystem(handler);
                }
            }
        }
        return mToolVoice;
    }


    /**
     * 是否按顺序播放排队号
     */
    public boolean isOrderPlay = false;

    public boolean isSpeeking = false;
    public boolean isSpeakTest = false;
    public int speakTimes = 0;
    public int voiceCount = 1;//语音播报次数 默认1次

    private BVoiceSetting mVoiceSetting;//语音设置

    public Map<String, BVoice> mapVoice = new HashMap<>();
    public BVoice mNext;
    public String urlFinishVoice = "";

    public BaseDataHandler mDataHandler;

    /**
     * 是否按顺序播放排队号： 设置为true时，排队号 123 将逐字念成 一 二 三，而不是一百二十三
     *
     * @param orderPlay
     */
    public ToolVoiceSystem setOrderPlay(boolean orderPlay) {
        isOrderPlay = orderPlay;
        return this;
    }

    //获取语音格式对象 可以重复设置
    public BVoiceSetting getVoiceSetting() {
        return mVoiceSetting;
    }

    public ToolVoiceSystem setVoiceSetting(BVoiceSetting voiceSetting) {
        mVoiceSetting = voiceSetting;
        if (voiceSetting != null && mVoiceSetting.getVoNumber().length() > 0) {
            voiceCount = Integer.parseInt(mVoiceSetting.getVoNumber());
            voiceCount = voiceCount > 0 ? voiceCount : 1;
        }
        mVoiceSetting.setCanSpeak("1".equals(ToolSP.getDIYString(IConfigs.SP_VOICE_SWITCH)));
        return this;
    }

    /**
     * 设置语音结束请求接口
     *
     * @param urlFinishVoice
     */
    public ToolVoiceSystem setUrlFinishVoice(String urlFinishVoice) {
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
        UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                ToolLog.efile(TAG, "onSpeakBegin: 开始播放 ");
                isSpeeking = true;
                if (!isSpeakTest)
                    speakTimes++;
            }

            @Override
            public void onDone(String utteranceId) {
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
                        //如果设置了 需要单独处理
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
                            //如果设置了语音播放完成回传链接 需要更新后台
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
                                    if (response.body().contains("1")) {
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
                            //都没有设置 则播放完成直接移除 播放下一条
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
            }

            @Override
            public void onError(String utteranceId) {

            }
        };

        ToolTts.Instance().setSynthesizerListener(mUtteranceProgressListener);
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

    /**
     * 添加语音对象到呼叫集合 需要判断docid 一般情况指哪个医生呼叫的，
     * 若是没有，则不会播放该条语音
     *
     * @param voice
     */
    public void addVoice(BVoice voice) {
        if (mVoiceSetting == null || !mVoiceSetting.isCanSpeak()) {
            ToolLog.efile(TAG, "【请先设置语音格式 或后台开启语音功能】");
            return;
        }
        if (!TextUtils.isEmpty(voice.getDocid()))
            mapVoice.put(voice.getDocid(), voice);
        else {
            ToolLog.efile(TAG, "【声音对象docid为空，则不会播放该条语音】");
        }

        hasVoiceSpeak();
    }


    public TextView mTextView;

    public ToolVoiceSystem setVoiceView(TextView textView) {
        mTextView = textView;
        return this;
    }


    public void TtsSpeakTest(String test) {
        isSpeakTest = true;
        ToolTts.Instance().TtsSpeak(test);
        ToolLog.e(TAG, "TtsSpeakTest: " + test);
    }

    String voice = "";
    String txt = "";

    public synchronized void ttsSpeak() {
        if (mNext != null) {
            //"请(line)(name)到(department)(room)(doctor)就诊"
            String format = mVoiceSetting.getVoFormat();
            ToolLog.e(TAG, "format " + format);
            //说明是预叫号
            if (format.contains(",")) {
                //没有下一位了
                if (TextUtils.isEmpty(mNext.getNextName())) {
                    format = format.split(",")[0];//取前半
                    ToolLog.efile("预叫号 没有下一位了 " + format);
                }
                txt = format
                        .replaceFirst(ToolRegex.regPatientName, ToolToggle.showPatientFullName ? mNext.getPatientName() : ToolCommon.SplitStarName(mNext.getPatientName(), "*", 1, 2))
                        .replaceFirst(ToolRegex.regPatientNum, mNext.getPatientNum() + "")
                        .replaceFirst(ToolRegex.regDeptName, mNext.getDepartmentName())
                        .replaceFirst(ToolRegex.regClinicName, mNext.getClinicName())
                        .replaceFirst(ToolRegex.regDoctorName, mNext.getDoctorName())
                        .replaceFirst(ToolRegex.regClinicNum, mNext.getRoNum())
                        .replaceFirst(ToolRegex.regPatientType, mNext.getType())

                        .replace(ToolRegex.regDoctorName, mNext.getDoctorName())
                        .replace(ToolRegex.regPatientType, mNext.getType())
                        .replace(ToolRegex.regClinicNum, mNext.getRoNum())
                        .replace(ToolRegex.regDeptName, mNext.getDepartmentName())
                        .replace(ToolRegex.regClinicName, mNext.getClinicName())
                        .replace(ToolRegex.regPatientName, ToolToggle.showPatientFullName ? mNext.getNextName() : ToolCommon.SplitStarName(mNext.getNextName(), "*", 1, 2))
                        .replace(ToolRegex.regPatientNum, mNext.getNextNum() + "")
                        .replace(ToolRegex.regLeftParentheses, "")
                        .replace(ToolRegex.regRightParentheses, "");
                voice = format
                        .replaceFirst(ToolRegex.regPatientName, mNext.getPatientName())
                        .replaceFirst(ToolRegex.regPatientNum, isOrderPlay ? mNext.getPatientNum().replaceAll(".{1}(?!$)", "$0 ") : mNext.getPatientNum())
                        .replaceFirst(ToolRegex.regDeptName, mNext.getDepartmentName())
                        .replaceFirst(ToolRegex.regClinicName, mNext.getClinicName())
                        .replaceFirst(ToolRegex.regClinicNum, mNext.getRoNum())
                        .replaceFirst(ToolRegex.regDoctorName, mNext.getDoctorName())
                        .replaceFirst(ToolRegex.regPatientType, mNext.getType())

                        .replace(ToolRegex.regDoctorName, mNext.getDoctorName())
                        .replace(ToolRegex.regPatientType, mNext.getType())
                        .replace(ToolRegex.regClinicNum, mNext.getRoNum())
                        .replace(ToolRegex.regDeptName, mNext.getDepartmentName())
                        .replace(ToolRegex.regClinicName, mNext.getClinicName())
                        .replace(ToolRegex.regPatientName, mNext.getNextName())
                        .replace(ToolRegex.regPatientNum, isOrderPlay ? mNext.getPatientNum().replaceAll(".{1}(?!$)", "$0 ") : mNext.getPatientNum())
                        .replace(ToolRegex.regLeftParentheses, "")
                        .replace(ToolRegex.regRightParentheses, "")
                        .replace(ToolRegex.regCN1, "衣");//防止一 读成四声

            } else {
                txt = format
                        .replace(ToolRegex.regPatientName, ToolToggle.showPatientFullName ? mNext.getPatientName() : ToolCommon.SplitStarName(mNext.getPatientName(), "*", 1, 2))
                        .replace(ToolRegex.regPatientNum, mNext.getPatientNum() + "")
                        .replace(ToolRegex.regDeptName, mNext.getDepartmentName())
                        .replace(ToolRegex.regClinicName, mNext.getClinicName())
                        .replace(ToolRegex.regDoctorName, mNext.getDoctorName())
                        .replace(ToolRegex.regClinicNum, mNext.getRoNum())
                        .replace(ToolRegex.regPatientType, mNext.getType())
                        .replace(ToolRegex.regLeftParentheses, "")
                        .replace(ToolRegex.regRightParentheses, "");
                voice = format
                        .replace(ToolRegex.regPatientName, mNext.getPatientName())
                        .replace(ToolRegex.regPatientNum, isOrderPlay ? mNext.getPatientNum().replaceAll(".{1}(?!$)", "$0 ") : mNext.getPatientNum())
                        .replace(ToolRegex.regDeptName, mNext.getDepartmentName())
                        .replace(ToolRegex.regClinicName, mNext.getClinicName())
                        .replace(ToolRegex.regClinicNum, mNext.getRoNum())
                        .replace(ToolRegex.regDoctorName, mNext.getDoctorName())
                        .replace(ToolRegex.regPatientType, mNext.getType())
                        .replace(ToolRegex.regLeftParentheses, "")
                        .replace(ToolRegex.regRightParentheses, "")
                        .replace(ToolRegex.regCN1, "衣");//防止一 读成四声

            }
            ToolLog.e(TAG, "txt " + txt);
            ToolLog.e(TAG, "voice " + voice);
            if (mTextView != null) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(txt);
                    }
                });

            }
            ToolTts.Instance().TtsSpeak(voice);

        }

    }


}
