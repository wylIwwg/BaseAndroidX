package com.jdxy.wyl.baseandroidx.base;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.bean.BAppType;
import com.jdxy.wyl.baseandroidx.bean.BBanner;
import com.jdxy.wyl.baseandroidx.bean.BPower;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.jdxy.wyl.baseandroidx.bean.BVolume;
import com.jdxy.wyl.baseandroidx.thread.TimeThread;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolCommon;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLZ;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.tools.ToolSocket;
import com.jdxy.wyl.baseandroidx.tools.ToolTtsXF;
import com.jdxy.wyl.baseandroidx.tools.ToolVoiceXF;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity implements BaseDataHandler.MessageListener, IView {
    public String TAG = "【" + this.getClass().getSimpleName() + "】";

    public static final String SOCKET = "【socket】";
    public static final String HTTP = "【http】";
    public static final String ERROR = "【icon_error】";
    public static final String SUCCESS = "【success】";
    public Context mContext;
    public RelativeLayout mBaseRlRoot;//根布局
    public BaseDataHandler mDataHandler;
    public String[] mPermissions;

    public String mBaseHost = "";//host http://192.168.2.188:8080
    public String mHost = "";//host http://192.168.2.188:8080/项目名

    public boolean isRegistered = false;
    public int mRegisterCode = 0;
    public String mRegisterViper = "";

    public String mIP;
    public String mHttpPort;
    public String mSocketPort;
    public String mMac;

    public Presenter mPresenter;
    public SimpleDateFormat mDateFormat;
    public SimpleDateFormat mTimeFormat;
    public SimpleDateFormat mWeekFormat;
    public SimpleDateFormat mDateTimeFormat;
    public TimeThread mTimeThread;

    public String mRebootStarTime = "";//开关机 开机时间
    public String mRebootEndTime = "";//开关机 关机时间

    public String mProStarTime = "";//节目 开始时间
    public String mProEndTime = "";//节目 结束时间


    public String mVoiceSwitch = "1";//语音播报开关
    public boolean isContent = false;//内容切换开关
    public BVoiceSetting mVoiceSetting;//语音设置

    public String mUrlUploadCapture;//上传截图链接http
    public String mUrlUploadLogs;//上传截图链接http

    public String mUrlFinishVoice;//语音播报结束
    public String mClientId = "";
    //主内容
    public View mViewContent;
    public boolean islocalTimeSeted = false;//是否设置过本地时间
    public String mProjectName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseRlRoot = findViewById(R.id.baseRlRoot);

        mPresenter = new Presenter(mContext, this);
        mDataHandler.setMessageListener(this);

        mMac = ToolDevice.getMac();


        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mWeekFormat = new SimpleDateFormat("EEEE", Locale.CHINA);

        mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        mProjectName = ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME);
        if (TextUtils.isEmpty(mProjectName)) {
            showInfo("请设置 ProjectName");
        }

    }


    //展示未注册view
    public View viewRegister;

    /**
     * 控制展示注册状态
     *
     * @param msg
     */
    public void showRegister(String msg) {
        if (viewRegister != null) {
            mBaseRlRoot.removeView(viewRegister);
            viewRegister = null;
        }
        viewRegister = View.inflate(mContext, R.layout.item_register, null);
        TextView tv = viewRegister.findViewById(R.id.tvRegister);
        tv.setText(msg);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ToolDisplay.dip2px(mContext, 250), ToolDisplay.dip2px(mContext, 180));
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        viewRegister.setLayoutParams(lp);
        mBaseRlRoot.addView(viewRegister,90);
        viewRegister.bringToFront();
    }

    @Override
    public void showBanner(List<BBanner> banners) {

    }

    @Override
    public void showData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        ToolDisplay.hideBottomUIMenu(this);
    }


    /**
     * 开启本地时间线程
     */
    public void startLocalTime() {
        if (mTimeThread != null) {
            mTimeThread.onDestroy();
            mTimeThread = null;
        }
        mTimeThread = new TimeThread(mContext, mDataHandler, "yyyy-MM-dd", "HH:mm", "EEEE");
        mTimeThread.sleep_time = 1000 * 3;
        mTimeThread.start();
    }


    public void hasPermission() {
        //6.0以上申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPresenter.checkPermission(mPermissions);
        } else {
            initData();
        }

    }


    public void initListener() {

    }

    /**
     * 基本设置
     */
    public void initSetting() {
        try {
            Map<String, ?> mAll = ToolSP.getAll();
            ToolLog.efile("【本地配置信息】：");
            for (String str : mAll.keySet()) {
                ToolLog.efile("【key= " + str + " value= " + mAll.get(str) + "】");
            }

            mIP = ToolSP.getDIYString(IConfigs.SP_IP);
            mHttpPort = ToolSP.getDIYString(IConfigs.SP_PORT_HTTP);
            mSocketPort = ToolSP.getDIYString(IConfigs.SP_PORT_SOCKET);

            mVoiceSwitch = ToolSP.getDIYString(IConfigs.SP_VOICE_SWITCH);
            if (mVoiceSwitch.length() < 1) {
                mVoiceSwitch = "1";
            }
            //获取语音配置
            String mVoice = ToolSP.getDIYString(IConfigs.SP_VOICE_TEMP);
            if (mVoice != null && mVoice.length() > 0) {
                mVoiceSetting = JSON.parseObject(mVoice, BVoiceSetting.class);
            } else {
                //设置默认
                mVoiceSetting = new BVoiceSetting();
                mVoiceSetting.setVoFormat("请设置语音播报格式");
                mVoiceSetting.setVoNumber("1");
                mVoiceSetting.setVoSex("1");
                mVoiceSetting.setVoSpeed("45");
                mVoiceSetting.setVoPitch("50");
                mVoiceSetting.setVoVolume("100");
            }

            //读取到开关机设置
            String power = ToolSP.getDIYString(IConfigs.SP_POWER);
            if (!TextUtils.isEmpty(power)) {
                ToolLog.efile(TAG, "读取到开关机设置: " + power);
                JSONObject mPowerBean = JSON.parseObject(power);
                if (mPowerBean != null) {
                    mRebootStarTime = mPowerBean.getString("starTime");
                    mRebootEndTime = mPowerBean.getString("endTime");
                    ToolLog.efile(TAG, "开关机时间设置完成: " + " 开机时间：" + mRebootStarTime + "  关机时间：" + mRebootEndTime);
                }
            }

            //节目开始结束时间
            mProEndTime = ToolSP.getDIYString(IConfigs.SP_SETTING_END_TIME);
            mProStarTime = ToolSP.getDIYString(IConfigs.SP_SETTING_START_TIME);


            if (mIP.length() < 6) {
                return;
            }
            if (mHttpPort.length() < 1) {
                mHttpPort = "8080";
            }
            mBaseHost = String.format(IConfigs.HOST, mIP, mHttpPort);


        } catch (Exception error) {

            ToolLog.efile(TAG, "initSetting: " + error.toString());
        }


    }


    public void initData() {
        initSetting();
        initListener();
        ToolSocket.getInstance().setDataHandler(mDataHandler);

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        mViewContent = view;
        if (mBaseRlRoot == null) return;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseRlRoot.addView(view, lp);
    }

    public void showSuccess(String success) {
        ToolLog.efile(SUCCESS, "showSuccess: " + success);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.success(mContext, success, Toast.LENGTH_LONG, true).show();
            }
        });
    }

    public void showError(final String error) {
        ToolLog.efile(ERROR, "showError: " + error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.error(mContext, error, Toast.LENGTH_LONG, true).show();
            }
        });
    }

    public void showInfo(final String info) {
        ToolLog.efile(TAG, "showInfo: " + info);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.info(mContext, info, Toast.LENGTH_LONG, true).show();
            }
        });
    }

    @Override
    public void release() {

    }

    @Override
    public void showTips(int type, String message) {

    }

    @Override
    public void userHandler(Message msg) {
        switch (msg.what) {
            case IConfigs.MSG_SOCKET_DISCONNECT://socket断开连接
                startLocalTime();//启动本地时间线程
                showError(msg.obj.toString());
                break;
            case IConfigs.MSG_CREATE_TCP_ERROR://socket连接失败
                showError(msg.obj.toString());
                break;
            case IConfigs.MSG_REBOOT_LISTENER://设备关机 重启
                int mins;
                if (mRebootStarTime != null && mRebootStarTime.contains(":")) {
                    String[] ends = mRebootEndTime.split(":");
                    String[] starts = mRebootStarTime.split(":");
                    int endhour = Integer.parseInt(ends[0]);
                    int endmin = Integer.parseInt(ends[1]);
                    int starthour = Integer.parseInt(starts[0]);
                    int startmin = Integer.parseInt(starts[1]);
                    if (starthour >= endhour) {//当天
                        mins = (starthour - endhour) * 60 + (startmin - endmin);
                    } else {//
                        mins = (starthour + 24 - endhour) * 60 + (startmin - endmin);
                    }
                } else {
                    mins = 30;
                }
                showInfo("设备即将关机，将在" + mins + "分钟后重启");
                hardReboot(60 * mins);
                break;
            case IConfigs.NET_TIME_CHANGED://本地时间变化
                HashMap<String, String> times = (HashMap<String, String>) msg.obj;
                if (times != null) {
                    //时间
                    String timeStr = times.get("time");
                    //日期
                    String dateStr = times.get("date");
                    //星期
                    String week = times.get("week");
                    showTime(dateStr, timeStr, week);
                }
                break;
            case IConfigs.MSG_SOCKET_RECEIVED://socket收到通知

                try {
                    String obj = msg.obj.toString();
                    JSONObject mObject = JSONObject.parseObject(obj);
                    String mType = mObject.getString("type");
                    //不打印心跳日志
                    if (!"pong".equals(mType)) {
                        ToolLog.efile(SOCKET, obj);
                    }
                    ToolLog.e(TAG, "handleMessage: socket  " + obj);
                    switch (mType) {
                        case "voiceFile"://下载声音文件
                            List<String> urls = JSON.parseArray(mObject.getString("data"), String.class);
                            mPresenter.downloadVoiceFiles(urls);
                            break;
                        case "timing"://定时开关机
                            BPower mPowerBean = JSON.parseObject(obj, BPower.class);
                            if (mPowerBean != null) {
                                BPower.Data pbd = mPowerBean.getData();
                                if (pbd != null) {
                                    mRebootStarTime = pbd.getStarTime();
                                    mRebootEndTime = pbd.getEndTime();
                                    ToolSP.putDIYString(IConfigs.SP_POWER, JSON.toJSONString(pbd));
                                }
                            }
                            break;
                        case "reconnection"://重连 就重启
                            mDataHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.relaunchApp(true);
                                }
                            }, 1000);
                            break;
                        case "pong"://心跳处理
                            Date mDate;
                            ToolSocket.getInstance().feed();
                            if (obj.contains("date")) {
                                currentTime = mObject.getLong("date");
                                if (currentTime > 0) {
                                    mDate = new Date(currentTime);
                                    //以服务器时间来控制开关机重启
                                    if (!islocalTimeSeted)
                                        setSystemTime(currentTime);
                                } else {
                                    mDate = new Date(System.currentTimeMillis());
                                }

                            } else {
                                //如果后台没有推送时间  采用本地的
                                mDate = new Date(System.currentTimeMillis());
                            }
                            //时间
                            String timeStr = mTimeFormat.format(mDate);
                            //日期
                            String dateStr = mDateFormat.format(mDate);
                            //星期
                            String week = mWeekFormat.format(mDate);
                            showTime(dateStr, timeStr, week);
                            break;
                        case "voiceSwitch"://flag
                            mVoiceSwitch = mObject.getString("flag");
                            ToolSP.putDIYString(IConfigs.SP_VOICE_SWITCH, mVoiceSwitch);

                            break;
                        case "logs":
                            String sessionId2 = mObject.getString("sessionId");
                            mPresenter.uploadLogs(mUrlUploadLogs, sessionId2, mMac);
                            break;
                        case "screen"://截屏请求
                            String sessionId = mObject.getString("sessionId");
                            if (TextUtils.isEmpty(mUrlUploadLogs)) {
                                showError("截图链接无效！");
                                return;
                            }
                            String res = ToolCommon.getBitmapString(mBaseRlRoot);
                            ToolLog.efile(HTTP, "【上传截图】" + mUrlUploadLogs);
                            mPresenter.uploadScreen(mUrlUploadLogs, res, sessionId);
                            break;
                        case "voiceSize"://设置声音大小
                            BVolume volume = JSON.parseObject(msg.obj.toString(), BVolume.class);
                            BVolume.Data vdata = volume.getData();
                            if (vdata != null) {
                                String vsize = vdata.getSize();
                                vsize = vsize.replace("%", "");
                                largeVoice(vsize);
                            }
                            break;
                        case "restart":
                        case "restartApp"://重启软件
                            showInfo("软件即将重启");
                            mDataHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.relaunchApp(true);
                                }
                            }, 2000);
                            break;
                        case "upgrade"://更新apk
                            showInfo("收到软件更新");
                            String link = mObject.getString("link");
                            if (!TextUtils.isEmpty(link)) {
                                ToolLog.efile(TAG, "userHandler: " + "【下载更新】" + mBaseHost + link);
                                mPresenter.downloadApk(mBaseHost + link);
                            }

                            break;
                        case "register"://在线注册
                            String mRegister_code = mObject.getString("register_code");

                            break;
                        case "init": //socket连接成功之后 做初始化操作
                            mClientId = mObject.getString("id");
                            String ping = "{\"type\":\"ping\",\"id\":\"" + mClientId + "\"}";
                            ToolSocket.getInstance().setPing(ping);
                            //关闭本地时间线程
                            if (mTimeThread != null) {
                                mTimeThread.onDestroy();
                                mTimeThread = null;
                            }
                            addDevice(mClientId); //添加设备
                            break;

                        case "voiceFormat"://语音格式
                            mVoiceSetting = JSON.parseObject(mObject.get("data").toString(), BVoiceSetting.class);
                            if (mVoiceSetting != null) {
                                ToolSP.putDIYString(IConfigs.SP_VOICE_TEMP, JSON.toJSONString(mVoiceSetting));
                                ToolTtsXF.Instance().InitTtsSetting(mVoiceSetting);
                                ToolVoiceXF.Instance().setVoiceSetting(mVoiceSetting);
                            }
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ToolLog.efile(TAG, "userHandler: " + ex.toString());
                    showError(ex.getMessage());
                }
                break;
        }

    }

    /**
     * 显示时间
     * 在此基础上判断开关机时间
     *
     * @param dateStr
     * @param timeStr
     * @param week
     */
    public void showTime(String dateStr, String timeStr, String week) {
        if (timeStr.endsWith("00") && !timeStr.equals(lastPrintTime)) {
            lastPrintTime = timeStr;
            ToolLog.efile(TAG, "每隔整点打印一次时间：当前：" + timeStr + " 开机：" + mRebootStarTime + " 关机：" + mRebootEndTime);
        }

        if (timeStr.equals(mRebootEndTime)) {
            ToolLog.efile("【关机时间到了】: " + mRebootEndTime);
            if (mDataHandler != null) {
                mDataHandler.sendEmptyMessage(IConfigs.MSG_REBOOT_LISTENER);
            }
        }
    }

    @Override
    public void moreMessage(String type, String data) {

    }

    public void addDevice(String clientId) {

    }

    @Override
    public void uploadScreen(String url, String sessionId) {

    }

    public void largeVoice(String vsize) {
        if (vsize.length() > 0) {
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager != null) {
                int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//最大值
                int value = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//设置前
                int index = max * Integer.parseInt(vsize) / 100;//需要设置的值
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0); //音量
                ToolLog.efile(SOCKET, "【音量设置 】 " + vsize + " ，设置前：" + value + "， 设置后：" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        }
    }

    /**
     * 设置系统时间
     *
     * @param time
     */
    public void setSystemTime(long time) {

        islocalTimeSeted = ToolLZ.Instance().setSystemTime(time);
    }


    /**
     * 定时开关机
     *
     * @param seconds 单位秒
     */
    public void hardReboot(final int seconds) {
        if (ToolLZ.Instance().isLZDevice()) {
            release();

            mDataHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (seconds > 0) {
                        ToolLZ.Instance().alarmPoweron(seconds);////定时开机
                    } else {
                        ToolLZ.Instance().hardReboot();//重启
                    }
                    close();
                }
            }, 2000);
        }


    }


    public long currentTime = 0;
    public long changeTime = 0;

    String lastPrintTime = "";


    public void close() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * 默认设置
     *
     * @param api 获取地址的api
     */
    public void showSetting(String api) {
        showSetting(api, null, false);

    }

    /**
     * 添加软件类型
     *
     * @param api
     * @param appTypes 软件类型集合
     */
    public void showSetting(String api, List<BAppType> appTypes) {
        showSetting(api, appTypes, false);
    }

    /**
     * 添加软件类型
     *
     * @param api
     * @param appTypes 软件类型集合
     * @param clear    是否清除默认集合
     */
    public void showSetting(String api, List<BAppType> appTypes, boolean clear) {
        Intent mIntent = new Intent(mContext, BaseSettingActivity.class);
        mIntent.putExtra(IConfigs.SP_API, api);
        mIntent.putExtra(IConfigs.INTENT_APP_TYPE, JSON.toJSONString(appTypes));
        mIntent.putExtra(IConfigs.INTENT_CLEAR_APP_TYPE, clear);
        startActivity(mIntent);

    }

    public void showSetting() {
        showSetting(null, null, false);

    }

}
