package com.jdxy.wyl.baseandroidx.network;

import com.blankj.utilcode.util.Utils;
import com.jdxy.wyl.baseandroidx.base.IPresenter;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

/**
 * Created by wyl on 2019/9/16.
 */
public class LogDownloadListener extends DownloadListener {


    private String TAG = "  LogDownloadListener ";
    IPresenter presenter;

    public LogDownloadListener(IPresenter presenter) {
        super("LogDownloadListener");
        this.presenter = presenter;
    }

    @Override
    public void onStart(Progress progress) {
        // LogUtils.e(TAG, "onStart: " + progress);
    }

    @Override
    public void onProgress(Progress progress) {
        //ToolLog.e(TAG, "下载进度onProgress: " + progress.fraction);
        Toasty.info(Utils.getApp(), "下载进度: " + (int) (progress.fraction * 100) + "%").show();
    }

    @Override
    public void onError(Progress progress) {
        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(File file, Progress progress) {
        //节目包下载完成
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
        //2020-11-20-18-11-22_123
        //目标解压目录
        String dir = sdf.format(new Date(System.currentTimeMillis())) + "_" + progress.extra1;
        ToolLog.efile(TAG, "onSuccess: 节目文件下载完成 ");
        if (presenter != null) presenter.operationProgram(file, dir);
    }

    @Override
    public void onRemove(Progress progress) {
    }
}

