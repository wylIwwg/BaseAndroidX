package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;

import es.dmoral.toasty.Toasty;

/**
 * Created by wyl on 2019/2/25.
 */
public class ToolTtsXF {
    private static final String TAG = "ToolTts";
    // 语音合成对象
    private SpeechSynthesizer mTTSPlayer;//呼叫播放

    static ToolTtsXF mToolTts;
    static Object mObject = new Object();
    static Context mContext;

    BVoiceSetting mVoiceSetting;

    public static ToolTtsXF Instance(Context context) {
        mContext = context;
        return Instance();
    }

    public static ToolTtsXF Instance() {
        if (mToolTts == null) {
            synchronized (mObject) {
                if (mToolTts == null) {
                    mToolTts = new ToolTtsXF();
                }
            }
        }
        return mToolTts;
    }

    public SpeechSynthesizer getTTSPlayer() {
        return mTTSPlayer;
    }

    public String defaultDir = IConfigs.PATH_TTS;

    String baseSource = "common.jet";
    String voiceFileW = "xiaoyan.jet";
    String voiceFileM = "xiaofeng.jet";

    /**
     * 指定目录下是否存在语音播放文件
     *
     * @return
     */
    public boolean existsTTsFile() {
        String pathBaseSource = defaultDir + baseSource;
        if (!FileUtils.isFileExists(pathBaseSource)) {
            Toasty.error(mContext, "声音文件不存在 " + pathBaseSource).show();
            ToolLog.efile(TAG, "existsTTsFile: 声音文件不存在 " + pathBaseSource);
            return false;
        }
        return true;
    }


    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mTTSPlayer.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        mTTSPlayer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
        //设置发音人资源路径
        mTTSPlayer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTTSPlayer.setParameter(SpeechConstant.VOICE_NAME, "0".equals(mVoiceSetting.getVoSex())?"xiaoyan":"xiaofeng");
        //mTTSPlayer.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        ToolLog.e(TAG, "setParam: " + JSON.toJSONString(mVoiceSetting));
        //默认设置40
        mTTSPlayer.setParameter(SpeechConstant.SPEED, TextUtils.isEmpty(mVoiceSetting.getVoSpeed()) ? "40" : mVoiceSetting.getVoSpeed().length() == 1 ? (mVoiceSetting.getVoSpeed() + "0") : mVoiceSetting.getVoSpeed());
        //设置合成音调
        mTTSPlayer.setParameter(SpeechConstant.PITCH, TextUtils.isEmpty(mVoiceSetting.getVoPitch()) ? "50" : mVoiceSetting.getVoPitch());
        //设置合成音量
        mTTSPlayer.setParameter(SpeechConstant.VOLUME, TextUtils.isEmpty(mVoiceSetting.getVoVolume()) ? "100" : mVoiceSetting.getVoVolume());
        //设置播放器音频流类型
        mTTSPlayer.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC + "");
        //mTTSPlayer.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTTSPlayer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTTSPlayer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

        //合成录音保存路径
        mTTSPlayer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/tts/tts.wav");


    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.path, IConfigs.PATH_TTS + baseSource));
        tempBuffer.append(";");
        //发音人资源
        if ("0".equals(mVoiceSetting.getVoSex())) {
            //女发音
            tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.path, IConfigs.PATH_TTS + voiceFileW));
        } else {
            //男发音
            tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.path, IConfigs.PATH_TTS + voiceFileM));

        }
        return tempBuffer.toString();
    }

    public void release() {
        if (null != mTTSPlayer) {
            mTTSPlayer.stopSpeaking();
            // 退出时释放连接
            mTTSPlayer.destroy();
        }
    }


    /**
     * 合成回调监听。
     */
    private SynthesizerListener mDefaultTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
            ToolLog.efile(TAG, "onSpeakBegin: 开始播放 ");
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
/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
Log.e("MscSpeechLog", "buf is =" + buf);
}*/
        }
    };

    /**
     * 初始化呼叫
     */
    public ToolTtsXF InitTts() {

        if (mTTSPlayer == null) {
            mTTSPlayer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
                @Override
                public void onInit(int code) {
                    if (code != ErrorCode.SUCCESS) {
                        ToolLog.efile(TAG, "onInit: " + code);
                        Toasty.error(mContext, "初始化失败,错误码：" + code).show();
                    } else {
                        setParam();
                        // 初始化成功，之后可以调用startSpeaking方法
                        // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                        // 正确的做法是将onCreate中的startSpeaking调用移至这里
                    }
                }
            });

        } else {
            setParam();
        }
        return this;
    }

    public ToolTtsXF InitTtsSetting(BVoiceSetting voiceSetting) {
        this.mVoiceSetting = voiceSetting;
        if (existsTTsFile()) {
            InitTts();
        }
        return this;
    }

    SynthesizerListener mSynthesizerListener;

    public void setSynthesizerListener(SynthesizerListener synthesizerListener) {
        mSynthesizerListener = synthesizerListener;
    }

    public void TtsSpeak(String txt) {
        if (mTTSPlayer != null) {
            ToolLog.e(TAG, "TtsSpeak: mSynthesizerListener " + mSynthesizerListener);
            ToolLog.e(TAG, "TtsSpeak: mDefaultTtsListener " + mDefaultTtsListener);
            if (mSynthesizerListener != null)
                mTTSPlayer.startSpeaking(txt, mSynthesizerListener);
            else mTTSPlayer.startSpeaking(txt, mDefaultTtsListener);
            ToolLog.efile(TAG, "TtsSpeak: " + txt);
        }
    }


}
