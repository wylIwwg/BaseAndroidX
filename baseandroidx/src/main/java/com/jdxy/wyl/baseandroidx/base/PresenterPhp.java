package com.jdxy.wyl.baseandroidx.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.jdxy.wyl.baseandroidx.bean.BAppType;
import com.jdxy.wyl.baseandroidx.bean.BBanner;
import com.jdxy.wyl.baseandroidx.bean.BPower;
import com.jdxy.wyl.baseandroidx.bean.BProgram;
import com.jdxy.wyl.baseandroidx.bean.BRegister;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.jdxy.wyl.baseandroidx.bean.BVolume;
import com.jdxy.wyl.baseandroidx.listeners.RegisterListener;
import com.jdxy.wyl.baseandroidx.network.LogDownloadListener;
import com.jdxy.wyl.baseandroidx.thread.TimeThread;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.IToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolLZ;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolRegister;
import com.jdxy.wyl.baseandroidx.tools.ToolRegisterPhp;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.tools.ToolSocket;
import com.jdxy.wyl.baseandroidx.tools.ToolTtsXF;
import com.jdxy.wyl.baseandroidx.tools.ToolVoiceXF;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Created by wyl on 2020/5/12.
 */
public class PresenterPhp implements IPresenter, BaseDataHandler.MessageListener {

    private static final String TAG = " Presenter ";

    private IView mView;
    private BaseDataHandler mHandler;
    private Context mContext;

    public String mBaseHost = "";//host http://192.168.2.188:8080
    public String mHost = "";//host http://192.168.2.188:8080/项目名

    public String mVoiceSwitch = "1";//语音播报开关
    public boolean isContent = false;//内容切换开关
    public BVoiceSetting mVoiceSetting;//语音设置


    public String URL_FINISH_VOICE;//语音播报结束
    public String ProjectName = "";
    public boolean localTimeSeted = false;//是否设置过本地时间

    public String mRebootStarTime = "";//开关机 开机时间
    public String mRebootEndTime = "";//开关机 关机时间

    public String mProStarTime = "";//节目 开始时间
    public String mProEndTime = "";//节目 结束时间


    public SimpleDateFormat mDateFormat;
    public SimpleDateFormat mTimeFormat;
    public SimpleDateFormat mWeekFormat;
    public SimpleDateFormat mDateTimeFormat;
    public TimeThread mTimeThread;

    ///默认语音格式
    public String voiceFormat = "请(line)号(name)到(department)(room)(doctor)处(type)";


    private static PresenterPhp instance = new PresenterPhp();

    public PresenterPhp() {

    }

    public static PresenterPhp getInstance() {
        return instance;
    }

    public PresenterPhp init(Context context, IView mView) {
        this.mView = mView;
        mContext = context;
        mHandler = new BaseDataHandler();
        mHandler.setMessageListener(this);

        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mWeekFormat = new SimpleDateFormat("EEEE", Locale.CHINA);

        mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return instance;
    }

    public void setView(IView view) {
        mView = view;
    }

    /**
     * 设置Handler
     *
     * @param handler
     */
    public void setHandler(BaseDataHandler handler) {
        mHandler = handler;
        ToolSocket.getInstance().setDataHandler(mHandler);

    }

    IToolDevice mToolDevice;

    /**
     * 启动socket
     */
    public void startSocket() {
        //连接socket
        ToolSocket.getInstance().setDataHandler(mHandler).initSocket();
    }

    /**
     * 基本设置
     */
    public void initSetting() {
        try {
            Map<String, ?> mAll = ToolSP.getAll();
            ToolLog.efile("【打印本地配置信息】：");
            for (String str : mAll.keySet()) {
                ToolLog.efile("【key= " + str + " value= " + mAll.get(str) + "】");
            }

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
                mVoiceSetting.setVoFormat(voiceFormat);
                mVoiceSetting.setVoNumber("1");
                mVoiceSetting.setVoSex("0");
                mVoiceSetting.setVoSpeed("45");
                mVoiceSetting.setVoPitch("50");
                mVoiceSetting.setVoVolume("100");
                mVoiceSetting.setNeedSave(false);
            }

            //读取到开关机设置
            String power = ToolSP.getDIYString(IConfigs.SP_POWER);
            if (!TextUtils.isEmpty(power)) {
                ToolLog.efile(TAG, "读取到开关机设置: " + power);
                JSONObject mPowerBean = JSON.parseObject(power);
                if (mPowerBean != null) {
                    mRebootStarTime = mPowerBean.getString("starTime");
                    mRebootEndTime = mPowerBean.getString("endTime");
                    ToolLog.efile(TAG, ": " + " 开机时间：" + mRebootStarTime + "  关机时间：" + mRebootEndTime);
                    if (mToolDevice != null) {
                        ToolLog.efile(TAG, "获取开关机时间: " + mToolDevice.getPowerOnOffTime());

                    }
                }
            }

            //节目开始结束时间
            mProEndTime = ToolSP.getDIYString(IConfigs.SP_SETTING_END_TIME);
            mProStarTime = ToolSP.getDIYString(IConfigs.SP_SETTING_START_TIME);

            isContent = ToolSP.getDIYBoolean(IConfigs.SP_CONTENT_SWITCH);

            ProjectName = ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME);
            if (TextUtils.isEmpty(ProjectName)) {
                showTips(IConfigs.MESSAGE_INFO, "请设置 ProjectName");
                return;
            }

            //域名
            String mDomainName = ToolSP.getDIYString(IConfigs.SP_DOMAIN_NAME);
            String mIP = ToolSP.getDIYString(IConfigs.SP_IP);
            String mHttpPort = ToolSP.getDIYString(IConfigs.SP_PORT_HTTP);
            if (!TextUtils.isEmpty(mDomainName)) {
                mHost = mDomainName;//填写了域名 则使用域名请求
            } else {
                if (mIP.length() < 6) {
                    showTips(IConfigs.MESSAGE_INFO, "请设置ip");
                    return;
                }
                if (mHttpPort.length() < 1) {
                    showTips(IConfigs.MESSAGE_INFO, "请设置ip端口");
                    return;
                }
                mHost = String.format(IConfigs.HOST, mIP, mHttpPort);
            }
            //设置项目名  修改后设置的
            mBaseHost = mHost;
            mHost = mBaseHost + ProjectName;


            ToolSP.putDIYString(IConfigs.SP_HOST, mHost);

        } catch (Exception error) {

            ToolLog.efile(TAG, "initSetting: " + error.toString());
        }
    }

    public void setToolDevice(IToolDevice device) {
        mToolDevice = device;
    }

    /**
     * 上传截图
     *
     * @param url
     * @param sessionId
     */
    public void uploadScreen(final String url, final File file, final String sessionId) {
        if (isCapturing) return;
        isCapturing = true;
        JSONObject content = new JSONObject();
        content.put("beaseStr", "");
        content.put("sessionId", "123");
        content.put("macId", ToolDevice.getMac());
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                OkGo.<String>post(url)
                        .params("content", content.toJSONString())
                        .params("checkinfo", "{\"timestamp\":\"123\",\"token\":\"123\"}")
                        .params("method", IConfigs.METHOD_UPLOAD_CAPTURE)
                        .params("file", file)
                        .tag(this)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                isCapturing = false;
                                ToolLog.efile(TAG, "onSuccess: 截图上传成功：" + response.body());
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                isCapturing = false;
                                mView.showTips(IConfigs.MESSAGE_ERROR, "截图上传失败" + response.getException().toString());
                            }
                        });
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });

    }

    boolean isCapturing = false;

    public void downProgram(BProgram.Data mData) {

        if (mData != null) {
            ToolSP.putDIYString(IConfigs.SP_SETTING_BACK_TIME, mData.getTemNoops());
            ToolSP.putDIYString(IConfigs.SP_SETTING_DELAY_TIME, mData.getTemIntime());
            ToolSP.putDIYString(IConfigs.SP_SETTING_SCROLL_TIME, mData.getTemBytime());
            ToolSP.putDIYString(IConfigs.SP_SETTING_TRY_TIME, mData.getTemTrytime());

            ToolSP.putDIYString(IConfigs.SP_SETTING_START_TIME, mData.getStartTime());
            ToolSP.putDIYString(IConfigs.SP_SETTING_END_TIME, mData.getEndTime());


            if (mData.getTemLink() != null && mData.getTemLink().length() > 0) {

                ToolSP.putDIYString(IConfigs.SP_PROGRAM_ID, mData.getId());
                ToolLog.efile(TAG, "开始下载节目：downProgram: " + mData.getTemLink());
                GetRequest<File> mRequest = OkGo.<File>get(mData.getTemLink());
                OkDownload.request(IConfigs.PATH_ZIP, mRequest)//
                        .extra1(mData.getId())
                        .folder(IConfigs.PATH_ZIP)//adui/a3
                        .save()//
                        .register(new LogDownloadListener(this))//
                        .start();

               /* OkGo.<File>get(mData.getTemLink())
                        .tag(this)
                        .execute(new FileCallback(IConfigs.PATH_ZIP, null) {
                            @Override
                            public void onSuccess(Response<File> response) {
                            }

                            @Override
                            public void onError(Response<File> response) {
                                super.onError(response);
                                ToolLog.efile(TAG, "onError: " + (response.getException() == null ? "" : response.getException().toString()));
                            }
                        });*/

            }
        }

    }

    public void operationProgram(File mFile, String dir) {
        //
        ToolLog.efile(TAG, "operationProgram:节目压缩包路径：  " + mFile.getAbsolutePath());
        //通知更新
        //读取默认配置信息
        //通知后台更新
       /* HttpParams hp = new HttpParams();
        hp.put("pushTem", ToolSP.getDIYString(IConfigs.SP_PROGRAM_ID));
        hp.put("pushMac", ToolDevice.getMac());
        hp.put("pushState", "1");
        OkGo.<String>post(mHost + IConfigs.URL_ADD_PUSH)
                .params(hp)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });*/

        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                //新节目文件夹
                String cur = IConfigs.PATH_PROGRAM + "/" + dir + "/";
                FileUtils.createOrExistsDir(cur);
                //记录上一次资源目录
                ToolSP.putDIYString(IConfigs.SP_PATH_DATA_BACKUP, ToolSP.getDIYString(IConfigs.SP_PATH_DATA));
                //设置当前资源目录
                ToolSP.putDIYString(IConfigs.SP_PATH_DATA, cur);
                ToolLog.efile(TAG, "【Program】:媒体地址： " + cur);

                try {
                    ZipFile zipFile = new ZipFile(mFile);
                    InputStream is = null;
                    Enumeration e = zipFile.entries();
                    int count = 0;
                    while (e.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) e.nextElement();
                        File dstFile = new File(cur + "/" + entry.getName());
                        ToolLog.efile(TAG, "【Program】: " + dstFile.getAbsolutePath());
                        if (entry.isDirectory()) {
                            FileUtils.createOrExistsDir(dstFile);
                            continue;
                        }
                        is = zipFile.getInputStream(entry);

                        FileUtils.createOrExistsDir(dstFile.getParentFile());
                        FileOutputStream fos = new FileOutputStream(dstFile);
                        byte[] buffer = new byte[1024 * 1024];
                        while ((count = is.read(buffer, 0, buffer.length)) != -1) {
                            fos.write(buffer, 0, count);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ToolLog.efile(TAG, "doInBackground:解压异常： " + e.toString());
                }
                ToolLog.efile(TAG, "【Program】: 解压完成 ..");
                Thread.sleep(2000);
                AppUtils.relaunchApp(true);
                return null;
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                ToolLog.efile(TAG, "onFail: " + t.toString());
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }

    public void downloadVoiceFiles(List<String> urls) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                for (String url : urls) {
                    //如果包含项目名
                    if (url.contains(ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME))) {
                        url = url.replace(ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME), "");
                        ToolLog.efile(TAG, "下载声音文件2: " + url);
                    }
                    if (!url.startsWith("http")) {
                        url = ToolSP.getDIYString(IConfigs.SP_HOST) + url;
                        ToolLog.efile(TAG, "下载声音文件3: " + url);
                    }
                    ToolLog.efile(TAG, "下载声音文件: " + url);
                    OkGo.<File>get(url).tag(this).execute(new FileCallback(IConfigs.PATH_TTS, null) {
                        @Override
                        public void onSuccess(Response<File> response) {

                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                            ToolLog.efile(TAG, "onError:声音文件下载失败： " + (response.getException() == null ? "" : response.getException().toString()));
                        }
                    });
                }
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });
    }

    private boolean isLoging = false;

    /**
     * 上传日志
     */
    public void uploadLogs(String sessionId, String mac) {
        if (isLoging) return;
        isLoging = true;
        JSONObject content = new JSONObject();
        content.put("beaseStr", "");
        content.put("sessionId", sessionId);
        content.put("mac", mac);
        ToolLog.mLogger.setLogging(true);
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                ToolLog.mLogger.setLogging(true);
                List<File> mFiles = FileUtils.listFilesInDir(IConfigs.PATH_LOG);
                if (mFiles.size() > 0) {
                    OkGo.<String>post(mHost)
                            .params("content", content.toJSONString())
                            .params("checkinfo", "{\"timestamp\":\"123\",\"token\":\"123\"}")
                            .params("method", IConfigs.METHOD_UPLOAD_LOG)
                            // .params("file", files[0])//单个文件
                            .addFileParams("file[]", mFiles)//文件集合

                            .tag(this)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    isLoging = false;
                                    ToolLog.mLogger.setLogging(false);
                                    ToolLog.efile("HTTP 日志上传结果" + response.body());
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    isLoging = false;
                                    ToolLog.mLogger.setLogging(false);
                                    mView.showTips(IConfigs.MESSAGE_ERROR, "日志上传失败" + response.getException().toString());
                                }
                            });
                } else {
                    isLoging = false;
                    ToolLog.mLogger.setLogging(false);
                }
                return null;
            }

            @Override
            public void onSuccess(String result) {
                ToolLog.mLogger.setLogging(true);
                isLoging = false;

            }
        });


    }


    /**
     * 检查权限
     *
     * @param mPermissions
     */
    @SuppressLint("WrongConstant")
    public void checkPermission(String[] mPermissions) {
        if (mPermissions != null && mPermissions.length > 0) {
            if (PermissionUtils.isGranted(mPermissions)) {
                mView.initData();
            } else {
                PermissionUtils
                        .permission(mPermissions)
                        .callback(new PermissionUtils.FullCallback() {
                            @Override
                            public void onGranted(@NonNull List<String> granted) {
                                mView.initData();
                            }

                            @Override
                            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                mView.showTips(IConfigs.MESSAGE_ERROR, "权限请求被拒绝将无法正常使用！");
                            }
                        }).request();
            }
        } else {
            mView.showTips(IConfigs.MESSAGE_ERROR, "权限列表为空");
        }
    }

    /**
     * 下载更新软件
     *
     * @param url
     */
    public void downloadApk(String url) {
        ToolLog.efile(TAG, "downloadApk: " + "【下载更新】" + url);
        if (!TextUtils.isEmpty(url) && url.endsWith(".apk")) {
            OkGo.<File>get(url)
                    .tag(this)
                    .execute(new FileCallback(IConfigs.PATH_APK, "update.apk") {
                        @Override
                        public void onSuccess(Response<File> response) {
                            final File apk = response.body();
                            ToolLog.e(TAG, "文件路径：" + apk.getAbsolutePath());
                            if (FileUtils.isFileExists(apk)) {
                                AppUtils.AppInfo mApkInfo = AppUtils.getApkInfo(apk.getAbsolutePath());
                                if (mApkInfo == null) {
                                    mView.showTips(IConfigs.MESSAGE_ERROR, "apk软件解析异常！");
                                    return;
                                }
                                String mPackageName = mApkInfo.getPackageName();
                                //如果当前包名是root包 说明可以安装其他软件  否则只能安装同包名软件
                                if (Utils.getApp().getPackageName().endsWith("root")) {
                                    installApk(apk, mPackageName);
                                } else {
                                    if (mPackageName.equals(Utils.getApp().getPackageName())) {
                                        //包名一样  是正确的apk
                                        //如果更新包的apk的版本号大 则更新apk
                                        installApk(apk, mPackageName);
                                    } else {
                                        mView.showTips(IConfigs.MESSAGE_ERROR, "应用程序不匹配");
                                    }
                                }
                            } else {
                                showTips(IConfigs.MESSAGE_ERROR, "apk文件不存在");
                            }
                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                            showTips(IConfigs.MESSAGE_ERROR, "apk下载失败" + response.getException().toString());
                        }
                    });
        } else {
            showTips(IConfigs.MESSAGE_ERROR, "文件链接无效！");
        }

    }


    public void installApk(File apk, String mPackageName) {
        ToolLog.efile(TAG, "【准备安装apk】" + apk.getAbsolutePath());
        //亮钻
        if (Build.USER.contains("liaokai")) {
            //7.0以下 安装升级
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                ToolLog.efile("【亮钻7.0以下系统升级】" + Build.USER);
                Intent intentapk = new Intent(Intent.ACTION_VIEW);
                intentapk.setDataAndType(Uri.fromFile(apk),
                        "application/vnd.android.package-archive");
                intentapk.putExtra("IMPLUS_INSTALL", "SILENT_INSTALL");
                if (mContext != null)
                    mContext.startActivity(intentapk);

            } else {
                String SHELL = "am start -a android.intent.action.VIEW -d %1$s " +
                        "-t application/vnd.android.package-archive -e IMPLUS_INSTALL SILENT_INSTALL";
                String apkPath = "file:///" + apk.getAbsolutePath();

                ToolLog.efile("【亮钻7.0以上系统升级】" + Build.USER, "onSuccess: " + String.format(SHELL, apkPath));
                ToolLZ.Instance().suExec(String.format(SHELL, apkPath));
            }
        } else {

            if (mToolDevice != null) {
                //boolean success = mToolDevice.installApk(apk.getAbsolutePath());
                // ToolLog.efile("【非亮钻安装7.0以上系统自动安装升级】" + Build.USER, "onSuccess: " + success);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    ToolLog.efile("【非亮钻7.0以下系统升级】" + Build.USER);
                    Intent intentapk = new Intent(Intent.ACTION_VIEW);
                    intentapk.setDataAndType(Uri.fromFile(apk),
                            "application/vnd.android.package-archive");
                    intentapk.putExtra("IMPLUS_INSTALL", "SILENT_INSTALL");
                    if (mContext != null)
                        mContext.startActivity(intentapk);
                } else {
                    String SHELL = "am start -a android.intent.action.VIEW -d %1$s " +
                            "-t application/vnd.android.package-archive -e IMPLUS_INSTALL SILENT_INSTALL";
                    String apkPath = "file:///" + apk.getAbsolutePath();

                    ToolLog.efile("【非亮钻安装7.0以上系统自动安装升级】" + Build.USER, "onSuccess: " + String.format(SHELL, apkPath));
                    mToolDevice.suExec(String.format(SHELL, apkPath));
                }
                return;
            }

            //普通板子
            Intent intent = new Intent();
            //执行动作
            intent.setAction(Intent.ACTION_VIEW);
            //判断版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ToolLog.efile("【普通7.0以上系统升级】" + Build.USER);
                Uri apkUri = FileProvider.getUriForFile(Utils.getApp(), Utils.getApp().getPackageName() + ".updatefileprovider", apk);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                ToolLog.efile("【普通7.0以下系统升级】" + Build.USER);
            }
            if (mContext != null)
                mContext.startActivity(intent);
        }

    }

    public void checkJavaRegister(RegisterListener listener) {
        checkJavaRegister(null, listener);
    }

    public void checkJavaRegister(String pubKey, RegisterListener listener) {
        BRegister mBRegister = ToolRegister.Instance(Utils.getApp(), null, pubKey).checkDeviceRegisteredJava();
        if (listener != null) {
            listener.RegisterCallBack(mBRegister);
        }
    }


    @Override
    public void showTips(int type, String message) {
        mView.showTips(type, message);
    }


    /**
     * 包含 开关机；时间变化；声音大小，开关；截屏；重启；语音格式；连接；在线注册；升级；
     *
     * @param msg
     */
    @Override
    public void userHandler(Message msg) {
        switch (msg.what) {
            case IConfigs.MSG_SOCKET_DISCONNECT://socket断开连接
                startLocalTime();//启动本地时间线程
            case IConfigs.MSG_CREATE_TCP_ERROR://socket连接失败
                startLocalTime();//启动本地时间线程
                showTips(IConfigs.MESSAGE_ERROR, msg.obj == null ? "socket连接失败" : msg.obj.toString());
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
                    //开机    关机
                    //9.30   9.35   第二天
                    //9.30   10.00/10.45  第二天

                    //9.30   8.30   当天（关机时间小于 开机时间）
                    //9.30   9.28
                    if (starthour == endhour && endmin <= startmin || starthour > endhour) {
                        //说明是当天
                        mins = (starthour - endhour) * 60 + (startmin - endmin);
                    } else {//
                        mins = (starthour + 24 - endhour) * 60 + (startmin - endmin);
                    }
                } else {
                    mins = 30;
                }
                showTips(IConfigs.MESSAGE_ERROR, "设备即将关机，将在" + mins + "分钟后重启");
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
                        ToolLog.efile(TAG, obj);
                    }
                    ToolLog.e(TAG, "handleMessage: socket  " + obj);
                    switch (mType) {
                        case "change"://节目 数据切换
                            //记录切换操作的时间
                            changeTime = currentTime;
                            boolean change = "1".equals(mObject.getString("data"));
                            //0:显示节目；1 显示数据
                            ToolSP.putDIYBoolean(IConfigs.SP_CONTENT_SWITCH, change);
                            if (!change) {
                                mView.showBanner(null);
                            } else {
                                mView.showData();
                            }
                            break;

                        case "voiceFile"://下载声音文件
                            List<String> urls = JSON.parseArray(mObject.getString("data"), String.class);
                            downloadVoiceFiles(urls);

                            break;
                        case "program"://接收到推送的节目包
                            showTips(IConfigs.MESSAGE_INFO, "收到节目");
                            BProgram mProgram = JSON.parseObject(msg.obj.toString(), BProgram.class);
                            downProgram(mProgram.getData());

                            break;
                        case "timing"://定时开关机
                            BPower mPowerBean = JSON.parseObject(obj, BPower.class);
                            if (mPowerBean != null) {
                                BPower.Data pbd = mPowerBean.getData();
                                if (pbd != null) {
                                    mRebootStarTime = pbd.getStarTime();
                                    mRebootEndTime = pbd.getEndTime();
                                    ToolSP.putDIYString(IConfigs.SP_POWER, JSON.toJSONString(pbd));
                                    if (mToolDevice != null) {
                                        String[] ends = mRebootEndTime.split(":");
                                        String[] starts = mRebootStarTime.split(":");
                                        int endhour = Integer.parseInt(ends[0]);
                                        int endmin = Integer.parseInt(ends[1]);
                                        int starthour = Integer.parseInt(starts[0]);
                                        int startmin = Integer.parseInt(starts[1]);
                                        mToolDevice.setPowerOnOff(new int[]{starthour, startmin}, new int[]{endhour, endmin}, 0);
                                    }
                                }
                            }
                            break;
                        case "reconnection"://重连 就重启
                            mHandler.postDelayed(new Runnable() {
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
                                    if (!localTimeSeted)
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
                        case "errorLog":
                            uploadLogs("", ToolDevice.getMac());
                            break;
                        case "capture":
                            mView.uploadScreen(mHost, "");
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
                            showTips(IConfigs.MESSAGE_INFO, "软件即将重启");
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtils.relaunchApp(true);
                                }
                            }, 2000);
                            break;
                        case "upgrade"://更新apk
                            showTips(IConfigs.MESSAGE_INFO, "收到软件更新");
                            String cloudVersionCode = mObject.get("version").toString();
                            String mApply = mObject.get("apply").toString();
                            if (TextUtils.isEmpty(cloudVersionCode) || TextUtils.isEmpty(mApply)) {
                                showTips(IConfigs.MESSAGE_ERROR, "软件链接为空或版本号存在");
                                return;
                            }

                            if (mApply.length() > 0 && mApply.toLowerCase().endsWith("apk")) {
                                downloadApk(mApply);
                            }
                            break;
                        case "register"://在线注册
                            String mRegister_code = mObject.getString("register_code");
                            ToolRegisterPhp.Instance(mContext).registerDevice(mRegister_code);
                            showTips(IConfigs.MESSAGE_INFO, "注册信息已更改，软件即将重启");
                            if (mHandler != null)
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppUtils.relaunchApp(true);
                                    }
                                }, 2000);
                            break;
                        case "init": //socket连接成功之后 做初始化操作
                            String clientId = mObject.getString("client_id");
                            String ping = "{\"type\":\"ping\",\"id\":\"" + clientId + "\"}";
                            // ToolSocket.getInstance().setPing(ping);
                            //连接上服务器 关闭本地时间线程
                            if (mTimeThread != null) {
                                mTimeThread.onDestroy();
                                mTimeThread = null;
                            }
                            mView.addDevice(clientId); //添加设备
                            break;

                        case "voiceFormat"://语音格式
                            mVoiceSetting = JSON.parseObject(mObject.get("data").toString(), BVoiceSetting.class);
                            if (mVoiceSetting != null) {
                                ToolSP.putDIYString(IConfigs.SP_VOICE_TEMP, JSON.toJSONString(mVoiceSetting));
                                ToolTtsXF.Instance().InitTtsSetting(mVoiceSetting);
                                ToolVoiceXF.Instance().setVoiceSetting(mVoiceSetting);
                            }
                            break;
                        default://需要将未处理的信息抛出去
                            //很多类型的的type都没有data 需要将整条消息抛出
                            mView.moreMessage(mType, obj);
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ToolLog.efile(TAG, "userHandler: " + ex.toString());
                    showTips(IConfigs.MESSAGE_ERROR, ex.getMessage());
                }
                break;
        }
    }

    //获取Handler
    public BaseDataHandler getHandler() {
        return mHandler;
    }

    public void InitTtsSetting() {

        //初始化语音sdk
        ToolTtsXF.Instance(mContext).InitTtsSetting(mVoiceSetting);
        //初始化语音控制
        ToolVoiceXF.Instance(mHandler).setVoiceSetting(mVoiceSetting).setUrlFinishVoice(URL_FINISH_VOICE).InitTtsListener();

    }


    /**
     * 开启本地时间线程
     */
    public void startLocalTime() {
        if (mTimeThread != null) {
            return;
        }
        mTimeThread = new TimeThread(mContext, mHandler, "yyyy-MM-dd", "HH:mm", "EEEE");
        mTimeThread.sleep_time = 1000 * 10;
        mTimeThread.start();
    }

    public long currentTime = 0;
    public long changeTime = 0;

    String lastPrintTime = "";

    /**
     * 显示时间
     * 在此基础上判断开关机时间
     *
     * @param dateStr
     * @param timeStr
     * @param week
     */
    public void showTime(String dateStr, String timeStr, String week) {
        mView.showTime(dateStr, timeStr, week);

        if (timeStr.endsWith("00") && !timeStr.equals(lastPrintTime)) {
            lastPrintTime = timeStr;
            ToolLog.efile(TAG, "每隔整点打印一次时间：当前：" + timeStr + " 开机：" + mRebootStarTime + " 关机：" + mRebootEndTime);
        }

        if (timeStr.equals(mRebootEndTime)) {
            ToolLog.efile("【关机时间到了】: " + mRebootEndTime);
            if (mHandler != null) {
                mHandler.sendEmptyMessage(IConfigs.MSG_REBOOT_LISTENER);
            }
        }
        try {
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

                        Date mDateChange = new Date(changeTime);
                        if (isContent && (endDate.getTime() - mDateChange.getTime() > 0)) {
                            return;
                        }
                        //若是没有显示banner
                        if (isContent) {
                            //展示信息 当天  展示
                            ToolLog.efile(TAG, "showTime: 当天  展示");
                            mView.showBanner(null);
                        }
                    } else if (mParse.getTime() - endDate.getTime() <= 0) {
                        Date mDateChange = new Date(changeTime);
                        if (isContent && (endDate.getTime() - mDateChange.getTime() > 0)) {
                            return;
                        }
                        //展示信息 第二天  展示   若是没有显示banner
                        if (isContent) {
                            ToolLog.efile(TAG, "showTime: 第二天  展示");
                            mView.showBanner(null);
                        }
                    } else {
                        //时间不合  显示数据内容
                        mView.showData();
                    }

                } else if (mParse.getTime() - startDate.getTime() >= 0 && endDate.getTime() - mParse.getTime() >= 0) {

                    String mFormat = mTimeFormat.format(changeTime);
                    Date mDateChange = mTimeFormat.parse(mFormat);
                    ToolLog.e(TAG, "showTime: " + mFormat + "  " + (endDate.getTime() - mDateChange.getTime()));
                    if (isContent && (endDate.getTime() - mDateChange.getTime() > 0)) {
                        return;
                    }
                    //展示信息
                    if (isContent) {
                        ToolLog.efile(TAG, "showTime: 当天且同一天  展示");
                        mView.showBanner(null);
                    }

                } else {
                    //时间不合 显示数据
                    mView.showData();
                }
            }
        } catch (ParseException e) {
            ToolLog.e(TAG, e.toString());
            // e.printStackTrace();
        }

    }


    /**
     * 获取资源文件
     *
     * @param dir
     */
    void listProgramFiles(String dir) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                List<BBanner> mBanners = new ArrayList<>();
                FileUtils.listFilesInDirWithFilter(dir, new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        String mPath = pathname.getAbsolutePath().toLowerCase();
                        if (mPath.endsWith("jpg") || mPath.endsWith("jpeg") || mPath.endsWith("png")) {
                            BBanner mBanner = new BBanner();
                            mBanner.setUrl(pathname.getAbsolutePath());
                            mBanner.setType("0");
                            mBanners.add(mBanner);
                        } else if (mPath.endsWith("mp4") || mPath.endsWith("avi") || mPath.endsWith("flv")) {
                            BBanner mBanner = new BBanner();
                            mBanner.setUrl(pathname.getAbsolutePath());
                            mBanner.setType("1");
                            mBanners.add(mBanner);
                        }
                        return false;
                    }
                });
                if (mBanners.size() > 0) {
                    mView.showBanner(mBanners);
                } else {
                    showTips(IConfigs.MESSAGE_ERROR, "资源文件不支持");
                }
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }


    /**
     * 设置系统时间
     *
     * @param time
     */
    public void setSystemTime(long time) {
        if (mToolDevice != null) {
            mToolDevice.setSystemTime(time);
        } else
            ToolLZ.Instance().setSystemTime(time);
        localTimeSeted = true;
    }


    /**
     * 定时开关机
     *
     * @param seconds 单位秒
     */
    public void hardReboot(final int seconds) {
        if (ToolLZ.Instance().isLZDevice()) {
            mView.release();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (seconds > 0) {
                        ToolLZ.Instance().alarmPoweron(seconds);////定时开机
                    } else {
                        ToolLZ.Instance().hardReboot();//重启
                    }
                }
            }, 2000);
        }
    }

    /**
     * 设置音量
     *
     * @param vsize
     */
    public void largeVoice(String vsize) {
        if (vsize.length() > 0) {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager != null) {
                int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//最大值
                int value = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//设置前
                int index = max * Integer.parseInt(vsize) / 100;//需要设置的值
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0); //音量
                ToolLog.efile(TAG, "【音量设置 】 " + vsize + " ，设置前：" + value + "， 设置后：" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
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
        mIntent.putExtra(IConfigs.INTENT_APP_TYPE, "-99");
        mIntent.putExtra(IConfigs.INTENT_CLEAR_APP_TYPE, clear);
        mContext.startActivity(mIntent);

    }

    public void showSetting() {
        showSetting(null, null, false);

    }
}