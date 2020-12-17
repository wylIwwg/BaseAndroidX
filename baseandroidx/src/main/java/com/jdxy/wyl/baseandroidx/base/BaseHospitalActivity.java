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
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.bean.BAppType;
import com.jdxy.wyl.baseandroidx.bean.BBanner;
import com.jdxy.wyl.baseandroidx.bean.BPower;
import com.jdxy.wyl.baseandroidx.bean.BProgram;
import com.jdxy.wyl.baseandroidx.bean.BPulse;
import com.jdxy.wyl.baseandroidx.bean.BRegisterResult;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.jdxy.wyl.baseandroidx.bean.BVolume;
import com.jdxy.wyl.baseandroidx.listeners.RegisterListener;
import com.jdxy.wyl.baseandroidx.media.player.SimpleJZPlayer;
import com.jdxy.wyl.baseandroidx.thread.TimeThread;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolCommon;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLZ;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolRegister;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.tools.ToolTts;
import com.jdxy.wyl.baseandroidx.tools.ToolVoice;
import com.jdxy.wyl.baseandroidx.view.DialogLogs;
import com.jdxy.wyl.baseandroidx.view.ItemScrollLayoutManager;
import com.jdxy.wyl.baseandroidx.view.SuperBanner;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.File;
import java.io.FileFilter;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jzvd.Jzvd;
import es.dmoral.toasty.Toasty;

public class BaseHospitalActivity extends AppCompatActivity implements BaseDataHandler.MessageListener, IView {
    public String TAG = "【" + this.getClass().getSimpleName() + "】";
    public static final String SOCKET = "【socket】";
    public static final String HTTP = "【http】";
    public static final String ERROR = "【error】";
    public static final String SUCCESS = "【success】";
    public Context mContext;
    public RelativeLayout mBaseRlRoot;//根布局
    public BaseDataHandler mDataHandler;
    public String mBaseHost = "";//host http://192.168.2.188:8080
    public String mHost = "";//host http://192.168.2.188:8080/项目名

    public boolean isRegistered = false;
    public int mRegisterCode = 0;
    public String mRegisterViper = "";

    public String mClientID = "";
    public String mIP;
    public String mHttpPort;
    public String mSocketPort;
    public String mMac;

    public String[] mPermissions;//申请权限

    ///默认语音格式
    public String voiceFormat = "请(line)号(name)到(department)(room)(doctor)处(type)";

    public Presenter mPresenter;
    public SimpleDateFormat mDateFormat;
    public SimpleDateFormat mTimeFormat;
    public SimpleDateFormat mWeekFormat;
    public SimpleDateFormat mDateTimeFormat;
    public TimeThread mTimeThread;

    // public RestartThread mRestartThread;
    public String mRebootStarTime = "";//开关机 开机时间
    public String mRebootEndTime = "";//开关机 关机时间

    public String mProStarTime = "";//节目 开始时间
    public String mProEndTime = "";//节目 结束时间


    public String mVoiceSwitch = "1";//语音播报开关
    public boolean isContent = false;//内容切换开关
    public BVoiceSetting mVoiceSetting;//语音设置

    public String URL_UPLOAD_SCREEN;//上传截图链接http
    public String URL_UPLOAD_LOGS;//上传截图链接http

    public String URL_FINISH_VOICE;//语音播报结束
    public String ProjectName = "";
    public boolean localTimeSeted = false;//是否设置过本地时间

    public String ClientId = "";

    //展示节目
    public RelativeLayout mRlvBanner;
    public SuperBanner mSuperBanner;
    public TextView mTvCover;
    public TextView mTvProSetting;//节目界面设置

    //主内容
    public View mViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseRlRoot = findViewById(R.id.baseRlRoot);
        mRlvBanner = findViewById(R.id.rlvBanner);
        mTvCover = findViewById(R.id.tvCover);
        mSuperBanner = findViewById(R.id.banner);
        mTvProSetting = findViewById(R.id.tvProSetting);

        mPresenter = new Presenter(mContext, this);
        mDataHandler = new BaseDataHandler(this);
        mDataHandler.setMessageListener(this);

        mMac = ToolDevice.getMac();


        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mWeekFormat = new SimpleDateFormat("EEEE", Locale.CHINA);

        mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        mPresenter.checkJavaRegister(new RegisterListener() {
            @Override
            public void RegisterCallBack(BRegisterResult registerResult) {
                mRegisterCode = registerResult.getRegisterCode();
                mRegisterViper = registerResult.getRegisterStr();
                isRegistered = registerResult.isRegistered();
            }
        });

        ProjectName = ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME);
        if (TextUtils.isEmpty(ProjectName)) {
            showInfo("请设置 ProjectName");
        }


    }


    //展示未注册view
    public View viewRegister;

    /**
     * @param msg
     */
    public void showRegister(String msg) {
        viewRegister = View.inflate(mContext, R.layout.item_register, null);
        TextView tv = viewRegister.findViewById(R.id.tvRegister);
        tv.setText(msg);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ToolDisplay.dip2px(mContext, 250), ToolDisplay.dip2px(mContext, 180));
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        viewRegister.setLayoutParams(lp);
        mBaseRlRoot.addView(viewRegister);
        viewRegister.bringToFront();
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

    @Override
    protected void onResume() {
        super.onResume();
        ToolDisplay.hideBottomUIMenu(this);
        if (ToolSP.getDIYBoolean(IConfigs.SP_SHOWLOG)) {
            showLogsDialog();
        } else {
            if (mDialogLogs != null) {
                mDialogLogs.dismiss();
            }
        }
    }


    public void initListener() {

    }

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

            isContent = "1".equals(ToolSP.getDIYString(IConfigs.SP_CONTENT_SWITCH));

            //获取声音配置
            String mVoice = ToolSP.getDIYString(IConfigs.SP_VOICE_TEMP);
            if (mVoice != null && mVoice.length() > 0) {
                mVoiceSetting = JSON.parseObject(mVoice, BVoiceSetting.class);
            } else {
                mVoiceSetting = new BVoiceSetting();
                mVoiceSetting.setVoFormat(voiceFormat);
                mVoiceSetting.setVoNumber("1");
                mVoiceSetting.setVoSex("1");
                mVoiceSetting.setVoSpeed("3");
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


    }

    public List<BBanner> mBanners;
    public int scrollTime;//图片滚动时间
    public int delayTime;//间隔多少时间滚动
    public String PathData = "";
    public CommonAdapter<BBanner> mBannerAdapter;

    public void InitProgram() {
        isContent = false;

        mRlvBanner.setVisibility(View.VISIBLE);
        if (mViewContent != null) {
            mViewContent.setVisibility(View.GONE);
        }
        mBanners = new ArrayList<>();
        Jzvd.WIFI_TIP_DIALOG_SHOWED = true;
        //读取默认配置信息
        String scroll = ToolSP.getDIYString(IConfigs.SP_SETTING_SCROLL_TIME);
        if (scroll.length() > 0) {
            scrollTime = Integer.parseInt(scroll);

        }
        String delay = ToolSP.getDIYString(IConfigs.SP_SETTING_DELAY_TIME);
        if (delay.length() > 0) {
            delayTime = Integer.parseInt(delay);

        }

        scrollTime = Math.max(scrollTime, 1) * 100;
        delayTime = Math.max(delayTime, 10);
        mBannerAdapter = new CommonAdapter<BBanner>(mContext, R.layout.item_video, mBanners) {
            @Override
            protected void convert(ViewHolder holder, BBanner banner, int position) {
                SimpleJZPlayer mPlayer = holder.getView(R.id.videoplayer);
                if ("0".equals(banner.getType())) {
                    Glide.with(mContext).load(banner.getUrl()).into(mPlayer.thumbImageView);

                } else {
                    mPlayer.setUp(banner.getUrl(), "", Jzvd.SCREEN_FULLSCREEN);
                }
            }
        };

        mSuperBanner.setTypeTime(SuperBanner.ROLL_ITEM, delayTime * 1000);
        ItemScrollLayoutManager mLayoutManager = new ItemScrollLayoutManager(mContext, LinearLayoutManager.HORIZONTAL);
        mLayoutManager.setScrollTime(scrollTime);

        mSuperBanner.setLayoutManager(mLayoutManager);
        mSuperBanner.setAdapter(mBannerAdapter);
        mSuperBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int mVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int index = mVisibleItemPosition % mBanners.size();
                    if (index < mBanners.size() && index > 0) {
                        BBanner mBanner = mBanners.get(index);
                        if ("1".equals(mBanner.getType())) {
                            View mView = mSuperBanner.getChildAt(0);
                            if (mView != null) {
                                SimpleJZPlayer mPlayer = mView.findViewById(R.id.videoplayer);
                                mPlayer.startButton.performClick();
                                mSuperBanner.stop();
                                mPlayer.setOnCompleteListener(new SimpleJZPlayer.CompleteListener() {
                                    @Override
                                    public void complete(SimpleJZPlayer player, String url, int screen) {
                                        mSuperBanner.start(true);
                                    }
                                });
                            }
                        }
                    }

                }
            }

        });

        mTvCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //获取文件数据
        PathData = ToolSP.getDIYString(IConfigs.SP_PATH_DATA);
        if (FileUtils.listFilesInDir(PathData).size() > 0) {
            //主目录有数据
            listProgramFiles(PathData);

        } else {
            //否则尝试获取备份
            PathData = ToolSP.getDIYString(IConfigs.SP_PATH_DATA_BACKUP);
            if (FileUtils.listFilesInDir(PathData).size() > 0) {
                listProgramFiles(PathData);
            } else {
                showError("未获取到资源");
            }
        }

        addDevice(ClientId);

    }

    public void releaseProgram() {
        if (mBanners != null) {
            mBanners.clear();
        }
        if (mSuperBanner != null) {
            mSuperBanner.stop();
            Jzvd.releaseAllVideos();
        }

        mRlvBanner.setVisibility(View.GONE);
        if (mViewContent != null) {
            mViewContent.setVisibility(View.VISIBLE);
        }

    }

    void listProgramFiles(String dir) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                FileUtils.listFilesInDirWithFilter(dir, new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        String mPath = pathname.getAbsolutePath();
                        if (mPath.endsWith("jpg") || mPath.endsWith("jpeg") || mPath.endsWith("png")) {
                            BBanner mBanner = new BBanner();
                            mBanner.setUrl(mPath);
                            mBanner.setType("0");
                            mBanners.add(mBanner);
                        } else if (mPath.endsWith("mp4") || mPath.endsWith("avi") || mPath.endsWith("flv")) {
                            BBanner mBanner = new BBanner();
                            mBanner.setUrl(mPath);
                            mBanner.setType("1");
                            mBanners.add(mBanner);

                        }
                        return false;
                    }
                });
                if (mBanners.size() > 0) {
                    mDataHandler.sendEmptyMessage(IConfigs.MSG_MEDIA_INIT);
                } else {
                    showError("资源文件不支持");
                }

                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
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


    @Override
    public void showSuccess(String success) {
        ToolLog.efile(SUCCESS, "showSuccess: " + success);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.success(mContext, success, Toast.LENGTH_LONG, true).show();
            }
        });
    }

    @Override
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

    public DialogLogs mDialogLogs;

    public void showLogsDialog() {
        mDialogLogs = DialogLogs.newInstance();
        mDialogLogs.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        mDialogLogs.show(getSupportFragmentManager(), "");
    }

    /**
     * 上传截图
     *
     * @param url
     * @param sessionId
     */
    public void uploadScreen(String url, String sessionId) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                String res = ToolCommon.getBitmapString(mBaseRlRoot);
                ToolLog.efile(HTTP, "【上传截图】" + url);
                mPresenter.uploadScreen(url, res, sessionId);
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });

    }


    /**
     * 包含 开关机；时间变化；声音大小，开关；截屏；重启；语音格式；连接；在线注册；升级；
     *
     * @param msg
     */
    @Override
    public void userHandler(Message msg) {
        switch (msg.what) {
            case IConfigs.MSG_MEDIA_INIT:
                //控制轮播
                if (mBanners.size() > 1) {
                    mBannerAdapter.setLoop(true);
                    mSuperBanner.start();
                } else {
                    mBannerAdapter.setLoop(false);
                    mSuperBanner.stop();
                }

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
                    if (mDialogLogs != null) {
                        mDialogLogs.showSocketMsg(obj);
                    }
                    JSONObject mObject = JSONObject.parseObject(obj);
                    String mType = mObject.getString("type");
                    //不打印心跳日志
                    if (!"pong".equals(mType)) {
                        ToolLog.efile(SOCKET, obj);
                    }
                    ToolLog.e(TAG, "handleMessage: socket  " + obj);
                    switch (mType) {
                        case "change"://节目 数据切换
                            isContent = "1".equals(mObject.getString("data"));
                            //0:显示节目；1 显示数据
                            ToolSP.putDIYBoolean(IConfigs.SP_CONTENT_SWITCH, isContent);
                            if (!isContent) {
                                InitProgram();
                            } else {
                                releaseProgram();
                            }
                            break;

                        case "voiceFile"://下载声音文件
                            List<String> urls = JSON.parseArray(mObject.getString("data"), String.class);
                            mPresenter.downloadVoiceFiles(urls);

                            break;
                        case "program"://接收到推送的节目包
                            showInfo("收到节目");
                            BProgram mProgram = JSON.parseObject(msg.obj.toString(), BProgram.class);
                            mPresenter.downProgram(mProgram.getData());

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
                            feed();
                            if (obj.contains("date")) {
                                long mTime = mObject.getLong("date");
                                if (mTime > 0) {
                                    mDate = new Date(mTime);
                                    //以服务器时间来控制开关机重启
                                    if (!localTimeSeted)
                                        setSystemTime(mTime);
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
                            mPresenter.uploadLogs(URL_UPLOAD_LOGS, sessionId2, mMac);
                            break;
                        case "screen"://截屏请求
                            String sessionId = mObject.getString("sessionId");
                            if (TextUtils.isEmpty(URL_UPLOAD_SCREEN)) {
                                showError("截图链接无效！");
                                return;
                            }
                            uploadScreen(URL_UPLOAD_SCREEN, sessionId);
                            break;
                        case "voiceSize"://设置声音大小
                            BVolume volume = JSON.parseObject(msg.obj.toString(), BVolume.class);
                            BVolume.Data vdata = volume.getData();
                            if (vdata != null) {
                                String vsize = vdata.getSize();
                                vsize = vsize.replace("%", "");
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
                       /* case "voice"://通知语音播报
                            ToolLog.e(TAG, "userHandler: voice  VoiceFlag = " + mVoiceSwitch + "   " + msg.obj.toString());
                            BVoice mVoiceBean = JSON.parseObject(mObject.get("data").toString(), BVoice.class);
                            if ("1".equals(mVoiceSwitch)) {
                                if (mVoiceBean != null && mVoiceBean.getPatientId().length() > 0) {
                                    mapVoice.put(mVoiceBean.getPatientId(), mVoiceBean);
                                }
                                if (!isSpeeking) {
                                    hasVoiceSpeak();
                                }
                            } else {
                                mapVoice.clear();
                            }

                            break;
*/
                        case "register"://在线注册
                            String mRegister_code = mObject.getString("register_code");
                            ToolRegister.Instance(mContext).registerDevice(mRegister_code);
                            showInfo("注册信息已更改，软件即将重启");
                            if (mDataHandler != null)
                                mDataHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppUtils.relaunchApp(true);
                                    }
                                }, 2000);

                            break;
                        case "init": //socket连接成功之后 做初始化操作
                            ClientId = mObject.getString("id");
                            String ping = "{\"type\":\"ping\",\"id\":\"" + ClientId + "\"}";
                            mPulseData.setPing(ping);
                            mSocketManager.getPulseManager().setPulseSendable(mPulseData);
                            if (mTimeThread != null) {
                                mTimeThread.onDestroy();
                                mTimeThread = null;
                            }
                            addDevice(ClientId); //添加设备
                            break;

                        case "voiceFormat"://语音格式
                            mVoiceSetting = JSON.parseObject(mObject.get("data").toString(), BVoiceSetting.class);
                            if (mVoiceSetting != null) {
                                ToolSP.putDIYString(IConfigs.SP_VOICE_TEMP, JSON.toJSONString(mVoiceSetting));

                                ToolTts.Instance().setSpeed(Integer.parseInt(mVoiceSetting.getVoSpeed()) * 10);
                                ToolVoice.Instance().setVoiceSetting(mVoiceSetting);
                            }

                            break;


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                }
                break;
        }

    }


    /**
     * 添加设备
     *
     * @param clientId
     */
    public void addDevice(String clientId) {

    }

    @Override
    public void release() {
        if (mTimeThread != null) {
            mTimeThread.onDestroy();
            mTimeThread = null;

        }
      /*  if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
            mDataHandler = null;
        }*/
        if (mSocketManager != null) {
            mSocketManager.disconnect();
            mSocketManager = null;
        }
    }


    /**
     * 设置系统时间
     *
     * @param time
     */
    public void setSystemTime(long time) {

        localTimeSeted = ToolLZ.Instance().setSystemTime(time);
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

    /**
     * 显示时间
     * 在此基础上判断开关机时间
     *
     * @param dateStr
     * @param timeStr
     * @param week
     */
    public void showTime(String dateStr, String timeStr, String week) {
        if (timeStr.endsWith("00")) {
            ToolLog.efile(TAG, "每隔整点打印一次时间：当前：" + timeStr + " 开机：" + mRebootStarTime + " 关机：" + mRebootEndTime);
        }

        if (timeStr.equals(mRebootEndTime)) {
            ToolLog.efile("【关机时间到了】: " + mRebootEndTime);
            if (mDataHandler != null) {
                mDataHandler.sendEmptyMessage(IConfigs.MSG_REBOOT_LISTENER);
            }
        }
        try {
            //已经处于节目显示情况
            if (!isContent) {

            } else {
                mProStarTime = ToolSP.getDIYString(IConfigs.SP_SETTING_START_TIME);
                mProEndTime = ToolSP.getDIYString(IConfigs.SP_SETTING_END_TIME);
                //开始时间和结束时间不为空才进入
                if (!TextUtils.isEmpty(mProEndTime) && !TextUtils.isEmpty(mProStarTime)) {
                    Date mParse = mTimeFormat.parse(timeStr);// 15:00  15:10  15:20 15:21
                    ToolLog.e(TAG, "showTime:  " + " 当前时间： " + timeStr + "  节目开始时间： " + mProStarTime + " 节目结束时间： " + mProEndTime);
                    Date startDate = mTimeFormat.parse(mProStarTime);
                    Date endDate = mTimeFormat.parse(mProEndTime);
                    //结束时间 小于 开始时间  表示跨天   15:00 —— 08：00
                    if (endDate.getTime() - startDate.getTime() < 0) {
                        // 15:00  19:00   8：00
                        if (mParse.getTime() - startDate.getTime() >= 0) {
                            //当天  展示
                            //展示信息
                            if (mRlvBanner.getVisibility() == View.GONE) {
                                ToolLog.efile(TAG, "showTime: 当天  展示");

                                InitProgram();
                            }
                        } else if (mParse.getTime() - endDate.getTime() <= 0) {
                            //第二天  展示
                            //展示信息
                            if (mRlvBanner.getVisibility() == View.GONE) {
                                ToolLog.efile(TAG, "showTime: 第二天  展示");
                                InitProgram();
                            }
                        } else {
                            //时间不合
                            mRlvBanner.setVisibility(View.GONE);
                            mSuperBanner.stop();
                            if (mViewContent != null)
                                mViewContent.setVisibility(View.VISIBLE);
                        }

                    } else if (mParse.getTime() - startDate.getTime() >= 0 && endDate.getTime() - mParse.getTime() >= 0) {
                        //展示信息
                        if (mRlvBanner.getVisibility() == View.GONE) {
                            ToolLog.efile(TAG, "showTime: 当天且同一天  展示");
                            InitProgram();
                        }

                    } else {
                        //时间不合
                        mRlvBanner.setVisibility(View.GONE);
                        mSuperBanner.stop();
                        if (mViewContent != null)
                            mViewContent.setVisibility(View.VISIBLE);
                    }

                }
            }


        } catch (ParseException e) {
            ToolLog.e(TAG, e.toString());
            // e.printStackTrace();
        }


    }

    public void close() {
        this.finish();

    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
            mDataHandler = null;
        }
    }


    /**
     * 处理语音类容 是否显示
     *
     * @param txt
     */
    public void showVoice(String txt) {

    }


    public void InitTtsSetting() {

        //初始化语音sdk
        ToolTts.Instance(mContext).InitTtsSetting(Integer.parseInt(mVoiceSetting.getVoSpeed()));
        //初始化语音控制
        ToolVoice.Instance(mDataHandler).setVoiceSetting(mVoiceSetting).setUrlFinishVoice(URL_FINISH_VOICE).InitTtsListener();

    }

    public void showSetting(String api) {
        Intent mIntent = new Intent(mContext, CommonSettingActivity.class);
        mIntent.putExtra(IConfigs.SP_API, api);
        startActivity(mIntent);

    }

    public void showSetting(String api, List<BAppType> appTypes) {
        Intent mIntent = new Intent(mContext, CommonSettingActivity.class);
        mIntent.putExtra(IConfigs.SP_API, api);
        mIntent.putExtra(IConfigs.INTENT_APP_TYPE, JSON.toJSONString(appTypes));
        startActivity(mIntent);

    }

    public void showSetting() {
        Intent mIntent = new Intent(mContext, CommonSettingActivity.class);
        startActivity(mIntent);

    }

    public IConnectionManager mSocketManager;

    public void feed() {
        if (mSocketManager != null && mSocketManager.getPulseManager() != null) {
            mSocketManager.getPulseManager().feed();
        }
    }

    public void initSocket() {
        if (mIP.length() < 6) {
            showError("请设置ip与端口号");
            return;
        }
        initSocket(mIP, mSocketPort);
    }

    public void initSocket(String ip, String port) {
        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo(ip, Integer.parseInt(port));
        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        mSocketManager = OkSocket.open(info);
        //获得当前连接通道的参配对象
        OkSocketOptions options = mSocketManager.getOption();
        //基于当前参配对象构建一个参配建造者类
        //  OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        //修改参配设置(其他参配请参阅类文档)
        //builder.setSinglePackageBytes(size);

        //设置自定义解析头
        OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(options);
        okOptionsBuilder.setPulseFrequency(5000);
        okOptionsBuilder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                /*
                 * 返回不能为零或负数的报文头长度(字节数)。
                 * 您返回的值应符合服务器文档中的报文头的固定长度值(字节数),可能需要与后台同学商定
                 */
                //  return /*固定报文头的长度(字节数)*/;
                return 8;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
     /*
         * 体长也称为有效载荷长度，
         * 该值应从作为函数输入参数的header中读取。
         * 从报文头数据header中解析有效负载长度时，最好注意参数中的byteOrder。
         * 我们强烈建议您使用java.nio.ByteBuffer来做到这一点。
         * 你需要返回有效载荷的长度,并且返回的长度中不应该包含报文头的固定长度
                        * /
                return /*有效负载长度(字节数)，固定报文头长度(字节数)除外*/
                ;
                // ToolLog.e("getBodyLengthheader: " + header.length);
                byte[] buffer = new byte[4];
                System.arraycopy(header, 0, buffer, 0, 4);
                int len = bytesToInt(buffer, 0);
                // ToolLog.e(TAG, "getBodyLength: " + (len - 8));
                return len - 8;
            }

        });

        //建造一个新的参配对象并且付给通道
        mSocketManager.option(okOptionsBuilder.build());
        //调用通道进行连接
        mSocketManager.registerReceiver(adapter);
        mSocketManager.connect();


    }

    public BPulse mPulseData = new BPulse();
    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            ToolLog.efile(TAG, "onSocketConnectionSuccess: " + " 【socket 连接成功】");
            //连接成功其他操作...
            //链式编程调用,给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
           /* OkSocket.open(info)
                    .getPulseManager()
                    .setPulseSendable(mPulseData)//只需要设置一次,下一次可以直接调用pulse()
                    .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发*/
            mSocketManager.getPulseManager().setPulseSendable(mPulseData).pulse();

        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            showError("【socket断开连接】" + e.getMessage());
            startLocalTime();
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            showError("【socket连接失败】" + e.getMessage());
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            byte[] b = data.getBodyBytes();
            String str = new String(b, Charset.forName("utf-8"));
            ToolLog.e(TAG, "onSocketReadResponse: " + action + "     " + b.length + "  " + str);
            //  JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            // String type = jsonObject.get("type").getAsString();
            //处理socket消息
            Message msg = Message.obtain();
            msg.what = IConfigs.MSG_SOCKET_RECEIVED;
            msg.obj = str;
            if (mDataHandler != null) {
                mDataHandler.sendMessage(msg);
            } else {
                // showError("handler 为空");
            }

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
        /*    byte[] bytes = data.parse();
            bytes = Arrays.copyOfRange(bytes, 4, bytes.length);
            String str = new String(bytes, Charset.forName("utf-8"));
            ToolLog.e(TAG, "onSocketWriteResponse: " + action + "     " + str);*/

        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            byte[] bytes = data.parse();
            bytes = Arrays.copyOfRange(bytes, 8, bytes.length);
            String str = new String(bytes, Charset.forName("utf-8"));
            // JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            ToolLog.e(TAG, "发送心跳包：" + str);
        }
    };

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }


}
