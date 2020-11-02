package com.jdxy.wyl.baseandroidx.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lzy.okgo.model.HttpParams;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.bean.BRegisterResult;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;


import java.util.List;

import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity implements com.jdxy.wyl.baseandroidx.base.BaseDataHandler.MessageListener {
    public String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public LinearLayout mBaseLlRoot;//根布局
    public com.jdxy.wyl.baseandroidx.base.BaseDataHandler mDataHandler;
    public String HOST = "";
    public boolean isRegistered = false;
    public String MARK = "";//软件类型

    public String REGISTER_STR = "";
    public String Client_ID = "";
    public int RegisterCode = 0;
    public String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseLlRoot = findViewById(R.id.baseLlRoot);
        mDataHandler = new com.jdxy.wyl.baseandroidx.base.BaseDataHandler(this);
        mDataHandler.setMessageListener(this);

    }

    public void hasPermission() {
        if (PERMISSIONS != null && PERMISSIONS.length > 0) {
            if (AndPermission.hasPermissions(mContext, PERMISSIONS)) {
                initData();
            } else {
                AndPermission.with(mContext)
                        .runtime()
                        .permission(PERMISSIONS)
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                initData();
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                showError("权限请求被拒绝将无法正常使用！");
                            }
                        })
                        .start();
            }
        } else {
            initWithoutPermission();
        }
    }

    public void initWithoutPermission() {

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

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {

        if (mBaseLlRoot == null) return;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseLlRoot.addView(view, lp);
    }

    @Override
    public void showError(String error) {
        Toasty.error(mContext, error, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void userHandler(Message msg) {


    }

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

    BRegisterResult mRegisterResult;


    HttpParams mParams;

    public void addDeviceBefore() {
        mParams = new HttpParams();
        mParams.put("mac", ToolDevice.getMac());
        // mParams.put("",  NetworkUtils.getIPAddress(true));
    }

}
