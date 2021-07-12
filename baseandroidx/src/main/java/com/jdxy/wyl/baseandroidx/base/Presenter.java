package com.jdxy.wyl.baseandroidx.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.bean.BProgram;
import com.jdxy.wyl.baseandroidx.bean.BRegisterResult;
import com.jdxy.wyl.baseandroidx.listeners.RegisterListener;
import com.jdxy.wyl.baseandroidx.network.LogDownloadListener;
import com.jdxy.wyl.baseandroidx.thread.JsonCallBack;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolLZ;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolRegister;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


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
        String host = ToolSP.getDIYString(IConfigs.SP_HOST);
        //通知后台更新
        ToolLog.efile(TAG, "operationProgram:通知更新后台： " + host);
        HttpParams hp = new HttpParams();
        hp.put("pushTem", ToolSP.getDIYString(IConfigs.SP_PROGRAM_ID));
        hp.put("pushMac", ToolDevice.getMac());
        hp.put("pushState", "1");
        OkGo.<String>post(host + IConfigs.URL_ADD_PUSH)
                .params(hp)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });

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

    /**
     * 上传日志
     *
     * @param url
     */
    private boolean isLoging = false;

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
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                List<File> mFiles = FileUtils.listFilesInDir(IConfigs.PATH_LOG);
                if (mFiles.size() > 0) {
                    OkGo.<String>post(url)
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
        isLoging = true;
        ToolLog.mLogger.setLogging(true);
        ThreadUtils.executeByCachedWithDelay(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                isLoging = true;
                ToolLog.mLogger.setLogging(true);
                List<File> mFiles = FileUtils.listFilesInDir(IConfigs.PATH_LOG);
                if (mFiles.size() > 0) {
                    OkGo.<String>post(url)
                            .params("macId", mac)
                            .params("sessionId", sessionId)
                            .addFileParams("files", mFiles)//日志文件集合
                            .tag(this)
                            .execute(new JsonCallBack<String>(String.class) {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    LogUtils.file("HTTP", "上传日志：" + response.body());
                                    isLoging = false;
                                    ToolLog.mLogger.setLogging(false);
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    isLoging = false;
                                    ToolLog.mLogger.setLogging(false);
                                    mView.showError("日志上传失败" + response.getException().toString());
                                }
                            });
                } else {
                    ToolLog.mLogger.setLogging(false);
                    isLoging = false;
                }

                return null;
            }

            @Override
            public void onSuccess(String result) {
                isLoging = false;
                ToolLog.mLogger.setLogging(false);
            }
        }, 1, TimeUnit.SECONDS);


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
        ToolLog.efile(TAG, "downloadApk: " + "【下载更新】" + url);
        if (!TextUtils.isEmpty(url) && url.endsWith(".apk")) {
            OkGo.<File>get(url)
                    .tag(this)
                    .execute(new FileCallback(IConfigs.PATH_APK, "") {
                        @Override
                        public void onSuccess(Response<File> response) {
                            final File apk = response.body();
                            if (apk != null) {
                                AppUtils.AppInfo mApkInfo = AppUtils.getApkInfo(apk.getAbsolutePath());
                                if (mApkInfo == null) {
                                    mView.showError("apk软件解析异常！");
                                    return;
                                }
                                String mPackageName = mApkInfo.getPackageName();
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
                            } else {
                                mView.showError("apk文件不存在");
                            }
                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                            mView.showError("apk下载失败" + response.getException().toString());
                        }
                    });
        } else {
            mView.showError("文件链接无效！");
        }

    }


    public void installApk(File apk) {
        //亮钻
        if (Build.USER.contains("liaokai")) {
            //7.0以下 安装升级
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                LogUtils.file("【亮钻7.0以下系统升级】" + Build.USER);
                Intent intentapk = new Intent(Intent.ACTION_VIEW);
                intentapk.setDataAndType(Uri.fromFile(apk),
                        "application/vnd.android.package-archive");
                intentapk.putExtra("IMPLUS_INSTALL", "SILENT_INSTALL");
                mContext.startActivity(intentapk);

            } else {
                String SHELL = "am start -a android.intent.action.VIEW -d %1$s " +
                        "-t application/vnd.android.package-archive -e IMPLUS_INSTALL SILENT_INSTALL";
                String apkPath = "file:///" + apk.getAbsolutePath();

                LogUtils.file("【亮钻7.0以上系统升级】" + Build.USER, "onSuccess: " + String.format(SHELL, apkPath));
                ToolLZ.Instance().suExec(String.format(SHELL, apkPath));
            }
        } else {
            //普通板子
            Intent intent = new Intent();
            //执行动作
            intent.setAction(Intent.ACTION_VIEW);
            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LogUtils.file("【普通7.0以上系统升级】" + Build.USER);
                Uri apkUri = FileProvider.getUriForFile(mContext, "com.test.fileprovider", apk);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                LogUtils.file("【普通7.0以下系统升级】" + Build.USER);
            }
            mContext.startActivity(intent);
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
