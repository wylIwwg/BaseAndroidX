package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.bean.BVoice;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.jdxy.wyl.baseandroidx.listeners.CopyFilesListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyl on 2019/2/25.
 */
public class ToolTts {
    private static final String TAG = "ToolTts";
    SpeechSynthesizer mTTSPlayer;//呼叫播放

    static ToolTts mToolTts;
    static Object mObject = new Object();
    static Context mContext;

    public static ToolTts Instance(Context context) {
        mContext = context;
        return Instance();
    }

    public static ToolTts Instance() {
        if (mToolTts == null) {
            synchronized (mObject) {
                if (mToolTts == null) {
                    mToolTts = new ToolTts();
                }
            }
        }
        return mToolTts;
    }

    public SpeechSynthesizer getTTSPlayer() {
        return mTTSPlayer;
    }

    public String defaultDir = IConfigs.PATH_TTS;

    String frontName = "frontend_model";
    String backName = "backend_female";


    public static final String appKey = "medtrhg7qrnnhkxploclzxezjumq667zc3l3rkaf";
    public static final String secret = "bbe919b0d4234c4b0f13ebfeb4e7173f";


    public boolean existsTTsFile() {
        return existsTTsFile(defaultDir);
    }


    /**
     * 指定目录下是否存在语音播放文件
     *
     * @param dir
     * @return
     */
    public boolean existsTTsFile(String dir) {

        if (dir == null || dir.length() < 1) {
        } else {
            defaultDir = dir.endsWith("/") ? dir : dir + "/";
        }
        return FileUtils.isFileExists(defaultDir + backName) && FileUtils.isFileExists(defaultDir + frontName);
    }


    /**
     * 初始化呼叫
     */
    public ToolTts InitTts() {

        if (mTTSPlayer == null) {
            // 初始化语音合成对象
            mTTSPlayer = new SpeechSynthesizer(mContext, appKey, secret);
            // 设置本地合成
            mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
            // 设置前端模型
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, defaultDir + frontName);
            // 设置后端模型
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, defaultDir + backName);
            //设置语音速度
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, speed > 0 ? speed : 30);
            //设置音量
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, 100);
            // 初始化合成引擎
            int mInit = mTTSPlayer.init("");
            ToolLog.efile(TAG, "语音初始化 initTts: " + mInit);
            if (mSynthesizerListener != null)
                mTTSPlayer.setTTSListener(mSynthesizerListener);

        }

        return this;
    }

    public ToolTts InitTtsSetting() {

        if (existsTTsFile()) {
            InitTts();
        }
        return this;
    }

    SpeechSynthesizerListener mSynthesizerListener;

    public void setSynthesizerListener(SpeechSynthesizerListener synthesizerListener) {
        mSynthesizerListener = synthesizerListener;
        if (mTTSPlayer != null)
            mTTSPlayer.setTTSListener(synthesizerListener);
    }

    int speed;

    public void setSpeed(int speed) {
        this.speed = speed;
        if (mTTSPlayer != null)
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, speed > 0 ? speed : 30);
    }

    public ToolTts InitTtsSetting(int speed) {
        this.speed = speed;
        if (existsTTsFile()) {
            InitTts();
        }
        //  mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, TTSManager.getInstance(mContext).defaultDir + TTSManager.getInstance(mContext).backName);
        return this;
    }

    public void TtsSpeak(String txt) {
        if (mTTSPlayer != null) {
            mTTSPlayer.playText(txt);
            ToolLog.efile(TAG, "TtsSpeak: " + txt);
        }
    }


}
