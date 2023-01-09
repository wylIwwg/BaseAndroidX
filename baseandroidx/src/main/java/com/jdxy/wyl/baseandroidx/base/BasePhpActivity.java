package com.jdxy.wyl.baseandroidx.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.bean.BBanner;
import com.jdxy.wyl.baseandroidx.thread.TimeThread;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolCommon;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.view.DialogLogs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class BasePhpActivity extends AppCompatActivity implements IView {
    public String TAG = "【" + this.getClass().getSimpleName() + "】";
    public static final String SOCKET = "【socket】";
    public static final String HTTP = "【http】";
    public static final String ERROR = "【icon_error】";
    public Context mContext;
    public RelativeLayout mBaseRlRoot;//根布局
    public String mHost = "";
    public boolean isRegistered = false;
    public int mRegisterCode = 0;
    public String mRegisterViper = "";

    public String mIP;
    public String mHttpPort;
    public String mSocketPort;
    public String mMac;

    public String[] mPermissions;


    public PresenterPhp mPresenter;
    public SimpleDateFormat mDateFormat;
    public SimpleDateFormat mTimeFormat;
    public SimpleDateFormat mWeekFormat;
    public TimeThread mTimeThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseRlRoot = findViewById(R.id.baseRlRoot);
        mMac = ToolDevice.getMac();
        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mWeekFormat = new SimpleDateFormat("EEEE", Locale.CHINA);

    }

    public void startLocalTime() {
        if (mTimeThread != null) {
            mTimeThread.onDestroy();
            mTimeThread = null;
        }
        if (mPresenter.getHandler() != null) {
            mTimeThread = new TimeThread(mContext, mPresenter.getHandler(), "yyyy-MM-dd", "HH:mm", "EEEE");
            mTimeThread.sleep_time = 1000 * 3;
            mTimeThread.start();
        }

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
    }


    public void initListener() {

    }


    public void initData() {
        initListener();
    }


    //展示未注册view
    public View viewRegister;

    @Override
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
        mBaseRlRoot.addView(viewRegister);
        viewRegister.bringToFront();
    }

    @Override
    public void showBanner(List<BBanner> banners) {

    }

    @Override
    public void showData() {

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {

        if (mBaseRlRoot == null) return;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseRlRoot.addView(view, lp);
    }


    public void uploadScreen(String url, String sessionId) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                //需要获取到根布局视图
                File file = ToolCommon.getBitmapFile(mBaseRlRoot);
                mPresenter.uploadScreen(url, file, sessionId);
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });

    }


    public DialogLogs mDialogLogs;

    public void showLogsDialog() {
        mDialogLogs = DialogLogs.newInstance();
        mDialogLogs.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        mDialogLogs.show(getSupportFragmentManager(), "");
    }

    @Override
    public void showTips(int type, String message) {
        ToolLog.efile(TAG, "showTips: " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case IConfigs.MESSAGE_ERROR:
                        Toasty.error(mContext, message, Toast.LENGTH_LONG, true).show();
                        break;
                    case IConfigs.MESSAGE_INFO:
                        Toasty.info(mContext, message, Toast.LENGTH_LONG, true).show();
                        break;
                    case IConfigs.MESSAGE_SUCCESS:
                        Toasty.success(mContext, message, Toast.LENGTH_SHORT, true).show();
                        break;
                }

            }
        });
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
    }


    @Override
    public void showTime(String dateStr, String timeStr, String week) {

    }

    @Override
    public void moreMessage(String type, String data) {

    }


    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        AppUtils.exitApp();
    }


    public void close() {
        this.finish();
    }

    public void InitTtsSetting() {

        //初始化语音sdk
        // ToolTts.Instance(mContext).InitTtsSetting(Integer.parseInt(mVoiceSetting.getVoSpeed()));
        //初始化语音控制
        // ToolVoice.Instance(mDataHandler).setVoiceSetting(mVoiceSetting).setUrlFinishVoice(URL_FINISH_VOICE).InitTtsListener();

    }


}
