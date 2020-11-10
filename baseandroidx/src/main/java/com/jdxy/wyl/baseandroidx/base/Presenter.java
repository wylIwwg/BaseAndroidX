package com.jdxy.wyl.baseandroidx.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.listeners.CopyFilesListener;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.lztek.toolkit.Lztek;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.jdxy.wyl.baseandroidx.bean.BRegisterResult;
import com.jdxy.wyl.baseandroidx.bean.Result;
import com.jdxy.wyl.baseandroidx.listeners.RegisterListener;
import com.jdxy.wyl.baseandroidx.thread.JsonCallBack;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolRegister;
import com.jdxy.wyl.baseandroidx.tools.ToolTts;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wyl on 2020/5/12.
 */
public class Presenter {

    private static final String TAG = " Presenter ";
    private IView mView;
    private Context mContext;

    public Presenter(Context context, IView mView) {
        mContext = context;
        this.mView = mView;

    }


    /**
     * 上传截图
     *
     * @param url
     * @param base64
     * @param sessionId
     */
    public void uploadScreen(final String url, final String base64, final String sessionId) {
        if (isCapturing) return;
        isCapturing = true;
        if (base64 != null && base64.length() > 0) {
            ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
                @Override
                public String doInBackground() throws Throwable {
                    OkGo.<String>post(url)
                            .tag(this)
                            .params("macId", ToolDevice.getMac())
                            .params("sessionId", sessionId)
                            .params("baseStr", base64)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    isCapturing = false;
                                    ToolLog.efile(TAG, "onSuccess: 截图上传成功：" + response.body());
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    isCapturing = false;
                                    mView.showError("截图上传失败" + response.getException().toString());
                                }
                            });
                    return null;
                }

                @Override
                public void onSuccess(String result) {

                }
            });
        } else {
            mView.showError("截图失败");
            isCapturing = false;

        }
    }

    boolean isCapturing = false;

    /**
     * 上传截图 PHP
     *
     * @param url
     * @param base64
     * @param sessionId
     * @param file
     */
    public void uploadCapture(final String url, final String base64, final String sessionId, File file) {
        if (isCapturing) return;
        isCapturing = true;
        JSONObject content = new JSONObject();
        content.put("beaseStr", base64);
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
                                mView.showError("截图上传失败" + response.getException().toString());
                            }
                        });
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });


    }


    /**
     * 上传日志
     *
     * @param url
     */
    private boolean isLoging = false;
    private List<File> fileList = new ArrayList<>();

    /**
     * 上传日志 PHP
     *
     * @param url
     */
    public void uploadLogs(String url) {
        if (isLoging) return;
        isLoging = true;
        JSONObject content = new JSONObject();
        content.put("beaseStr", "");
        content.put("sessionId", "123");
        content.put("mac", ToolDevice.getMac());
        File dir = new File(IConfigs.PATH_LOG);
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                fileList.clear();
                if (dir.isDirectory()) {
                    dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            fileList.add(pathname);
                            return false;
                        }
                    });
                    if (fileList.size() > 0) {
                        OkGo.<String>post(url)
                                .params("content", content.toJSONString())
                                .params("checkinfo", "{\"timestamp\":\"123\",\"token\":\"123\"}")
                                .params("method", IConfigs.METHOD_UPLOAD_LOG)
                                // .params("file", files[0])//单个文件
                                .addFileParams("file[]", fileList)//文件集合

                                .tag(this)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        isLoging = false;
                                        LogUtils.file("HTTP 日志上传结果" + response.body());
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        super.onError(response);
                                        isLoging = false;
                                        mView.showError("日志上传失败" + response.getException().toString());
                                    }
                                });
                    }
                }

                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });

    }

    /**
     * 上传日志 java
     *
     * @param url
     * @param sessionId
     * @param mac
     */
    public void uploadLogs(String url, String sessionId, String mac) {
        if (isLoging) return;

        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                File dir = new File(IConfigs.PATH_LOG);
                fileList.clear();
                if (dir.isDirectory()) {
                    dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            fileList.add(pathname);
                            return false;
                        }
                    });
                    if (fileList.size() > 0) {
                        isLoging = true;
                        OkGo.<String>post(url)
                                .params("macId", mac)
                                .params("sessionId", sessionId)
                                .addFileParams("files", fileList)//日志文件集合
                                .tag(this)
                                .execute(new JsonCallBack<String>(String.class) {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        LogUtils.file("HTTP", "上传日志：" + response.body());
                                        isLoging = false;
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        super.onError(response);
                                        isLoging = false;
                                        mView.showError("日志上传失败" + response.getException().toString());
                                    }
                                });
                    }
                }
                return null;
            }

            @Override
            public void onSuccess(String result) {

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
            if (AndPermission.hasPermissions(mContext, mPermissions)) {
                mView.initData();
            } else {
                AndPermission.with(mContext)
                        .runtime()
                        .permission(mPermissions)
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                mView.initData();
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                mView.showError("权限请求被拒绝将无法正常使用！");
                            }
                        })
                        .start();
            }
        } else {
            throw new RuntimeException("权限列表为空");
        }
    }

    /**
     * 下载更新软件
     *
     * @param url
     */
    public void downloadApk(String url) {
        OkGo.<File>get(url)
                .tag(this)
                .execute(new FileCallback(IConfigs.PATH_APK, "") {
                    @Override
                    public void onSuccess(Response<File> response) {
                        final File apk = response.body();
                        if (apk != null) {
                            String mPackageName = AppUtils.getApkInfo(apk.getAbsolutePath()).getPackageName();
                            //如果当前包名是root包 说明可以安装其他软件  否则只能安装同包名软件
                            if (mContext.getPackageName().endsWith("root")) {
                                installApk(apk);
                            } else {
                                if (mPackageName.equals(mContext.getPackageName())) {
                                    //包名一样  是正确的apk
                                    //如果更新包的apk的版本号大 则更新apk
                                    installApk(apk);


                                } else {
                                    mView.showError("应用程序不匹配");
                                }
                            }


                        }
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        mView.showError("apk下载失败" + response.getException().toString());
                    }
                });
    }


    public void installApk(File apk) {
        //7.0以下 安装升级
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Intent intentapk = new Intent(Intent.ACTION_VIEW);
            intentapk.setDataAndType(Uri.fromFile(apk),
                    "application/vnd.android.package-archive");
            intentapk.putExtra("IMPLUS_INSTALL", "SILENT_INSTALL");
            mContext.startActivity(intentapk);

        } else {
            String SHELL = "am start -a android.intent.action.VIEW -d %1$s " +
                    "-t application/vnd.android.package-archive -e IMPLUS_INSTALL SILENT_INSTALL";
            String apkPath = "file:///" + apk.getAbsolutePath();
            Lztek mLztek = Lztek.create(mContext);
            LogUtils.file("【7.0系统升级】", "onSuccess: " + String.format(SHELL, apkPath));
            mLztek.suExec(String.format(SHELL, apkPath));
        }
    }

    public void checkPhpRegister(RegisterListener listener) {
        BRegisterResult mRegisterResult = ToolRegister.Instance(mContext).checkDeviceRegisteredPhp();
        if (listener != null)
            listener.RegisterCallBack(mRegisterResult);
    }


    public void checkJavaRegister(RegisterListener listener) {
        BRegisterResult mRegisterResult = ToolRegister.Instance(mContext).checkDeviceRegisteredJava();
        if (listener != null)
            listener.RegisterCallBack(mRegisterResult);
    }

}
