package com.jdxy.wyl.baseandroidx.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.jdxy.wyl.baseandroidx.network.HttpLoggingInterceptor2;
import com.jdxy.wyl.baseandroidx.tools.ToolLZ;
import com.jdxy.wyl.baseandroidx.tools.ToolTtsXF;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.anr.ANRThread;
import com.jdxy.wyl.baseandroidx.appTopService.AppTopLifecycleHandler;
import com.jdxy.wyl.baseandroidx.appTopService.AppTopService;
import com.jdxy.wyl.baseandroidx.crash.config.CrashConfig;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import es.dmoral.toasty.Toasty;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.onAdaptListener;
import me.jessyan.autosize.utils.ScreenUtils;
import okhttp3.OkHttpClient;

/**
 * Created by wyl on 2018/11/13.
 */
public class BaseApp extends Application {

    public static final String TAG = " 【Application】 ";

    @Override
    public void onCreate() {

        super.onCreate();
        new ANRThread().start();
        Toasty.Config.getInstance()
                .allowQueue(false)
                .setTextSize(18)
                .apply();
        ToolSP.Init(this, getPackageName());
        ToolSP.instance().putBoolean(IConfigs.SP_SHOWLOG, false);

        Utils.init(this);

        if (Build.USER.contains("liaokai")) {
            ToolLZ.Init(this);
        }
        LogUtils.getConfig().setDir(IConfigs.PATH_LOG).setFilePrefix("log");

        AutoSizeConfig.getInstance()
                .setCustomFragment(true)
                //屏幕适配监听器
                .setOnAdaptListener(new onAdaptListener() {
                    @Override
                    public void onAdaptBefore(Object target, Activity activity) {
                        //使用以下代码, 可以解决横竖屏切换时的屏幕适配问题
                        //使用以下代码, 可支持 Android 的分屏或缩放模式, 但前提是在分屏或缩放模式下当用户改变您 App 的窗口大小时
                        //系统会重绘当前的页面, 经测试在某些机型, 某些情况下系统不会重绘当前页面, ScreenUtils.getScreenSize(activity) 的参数一定要不要传 Application!!!
                        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
                        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
                    }

                    @Override
                    public void onAdaptAfter(Object target, Activity activity) {
                    }
                })
                .setLog(false);

    }

    /**
     * 启用语音设置
     *
     * @param appid
     */
    public void initVoice(String appid) {
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid
        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        if (TextUtils.isEmpty(appid))
            return;

        if (!ToolTtsXF.Instance(this).existsTTsFile())
            return;
        StringBuffer param = new StringBuffer();
        param.append("appid=" + appid);
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());

    }

    /**
     * 设置项目名
     *
     * @param pn
     */
    public void setProjectName(String pn) {
        //如果没有修改过项目名 则以原来的为准
        if (TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME))) {
            ToolSP.putDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME, pn);
        }
    }


    /**
     * 删除指定时间内的文件  保留
     *
     * @param days
     */
    public void deleteFiles(final int days) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.listFilesInDirWithFilter(IConfigs.PATH_LOG, new FileFilter() {
                    @Override
                    public boolean accept(File mFile) {
                        long mLastModified = mFile.lastModified();
                        long date = 1000 * 60 * 60 * 24 * days;
                        if (System.currentTimeMillis() - mLastModified > date) {
                            FileUtils.delete(mFile);
                        }
                        return false;
                    }
                });
            }
        }).start();
    }

    /**
     * 删除指定时间内的文件  保留
     *
     * @param days
     */
    public void deleteFiles(final String path, final int days) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.listFilesInDirWithFilter(path, new FileFilter() {
                    @Override
                    public boolean accept(File mFile) {
                        long mLastModified = mFile.lastModified();
                        long date = 1000 * 60 * 60 * 24 * days;
                        if (System.currentTimeMillis() - mLastModified > date) {
                            FileUtils.delete(mFile);
                        }
                        return false;
                    }
                });
            }
        }).start();
    }

    public void initTopService() {
        registerActivityLifecycleCallbacks(new AppTopLifecycleHandler());

        //3秒之后启动服务
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent service = new Intent(getApplicationContext(), AppTopService.class);
                startService(service);
            }
        }, 5000);
    }


    public void initDebug(Class<Activity> activity) {
        CrashConfig.Builder.create()
                .backgroundMode(CrashConfig.BACKGROUND_MODE_SILENT) //default: CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(true) //default: true
                .showRestartButton(true) //default: true
                .logErrorOnRestart(true) //default: true
                .trackActivities(false) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .errorDrawable(R.drawable.icon_error) //default: bug image
                .restartActivity(activity) //default: null (your app's launch activity)
                .errorActivity(activity) //default: null (default icon_error activity)
                .eventListener(null) //default: null
                .apply();
    }


    public void initOkGO() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor2 loggingInterceptor = new HttpLoggingInterceptor2("【HTTP】");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.ALL);
        //添加OkGo默认debug日志
        builder.addInterceptor(loggingInterceptor);

        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));
        // builder.eventListenerFactory(NetworkListener.get());
        // builder.addNetworkInterceptor(new NetworkInterceptor());

        //超时时间设置，默认60秒
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(5000L, TimeUnit.MILLISECONDS);


        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)//必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(1)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        ;
    }


    /**
     * 用于程序崩溃重启
     */
    public boolean restart = true;

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public void initCrashRestart() {
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程

    }

    /**
     * 添加崩溃重启选择
     *
     * @param restart
     */
    public void initCrashRestart(boolean restart) {
        setRestart(restart);
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程

    }

    // 创建服务用于捕获崩溃异常
    public Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            handleException(ex);
            try {
                Thread.sleep(500 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    //保存错误日志
    public void handleException(Throwable ex) {
        String s = formatCrashInfo(ex);
        LogUtils.file(TAG, s);
        if (restart) {
            String message = ex.getMessage();
            //如果大于10
            int count = ToolSP.getDIYInt(message);
            count += 1;
            ToolLog.e("【count】", message + "  " + count + "");
            ToolSP.putDIYInt(message, count);
            if (count > 5) {
                //不再重启
            } else {

                AppUtils.relaunchApp(true);
            }
        }

    }


    public String formatCrashInfo(Throwable ex) {
        String lineSeparator = "\r\n";

        StringBuilder sb = new StringBuilder();

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        String dump = info.toString();
        String exception = "exception:" + "{\n" + dump + "\n}";
        printWriter.close();


        sb.append("----------------------------").append(lineSeparator);
        sb.append(exception).append(lineSeparator);
        sb.append("----------------------------").append(lineSeparator);

        return sb.toString();

    }


    private String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(date);
        return time;
    }


}
