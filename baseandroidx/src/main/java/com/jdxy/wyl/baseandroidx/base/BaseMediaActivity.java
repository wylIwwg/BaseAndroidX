package com.jdxy.wyl.baseandroidx.base;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.bean.BProgram;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.view.ScrollTipsView;
import com.jdxy.wyl.baseandroidx.view.SuperBanner;


/**
 * 描述 媒体展示界面
 * 作者 wyl
 * 日期 2021/2/4 16:54
 */
public class BaseMediaActivity extends BaseActivity {
    public SuperBanner mBanner;
    public TextView mTvCover;
    public TextView mTvProSetting;
    public RelativeLayout mRlvBanner;
    public ScrollTipsView mTvTips;
    public TextView mTvVersion;
    public RelativeLayout mRlvBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        mBanner = findViewById(R.id.banner);
        mTvCover = findViewById(R.id.tvCover);
        mTvProSetting = findViewById(R.id.tvProSetting);
        mRlvBanner = findViewById(R.id.rlvBanner);
        mTvTips = findViewById(R.id.tvTips);
        mTvVersion = findViewById(R.id.tvVersion);
        mRlvBottomBar = findViewById(R.id.rlvBottomBar);

        if (!TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_TIPS))) {
            mTvTips.setText(Html.fromHtml(ToolSP.getDIYString(IConfigs.SP_TIPS)));
            mRlvBottomBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void userHandler(Message msg) {
        super.userHandler(msg);
        if (msg.what == IConfigs.MSG_SOCKET_RECEIVED) {
            String obj = msg.obj.toString();
            JSONObject mObject = JSONObject.parseObject(obj);
            String mType = mObject.getString("type");
            //不打印心跳日志
            if (!"pong".equals(mType)) {
                ToolLog.efile(SOCKET, obj);
            }
            ToolLog.e(TAG, "handleMessage: socket  " + obj);
            switch (mType) {
                case "tips":
                    String mTips = mObject.getString("data");
                    ToolSP.putDIYString(IConfigs.SP_TIPS, mTips);
                    if (!TextUtils.isEmpty(mTips)) {
                        mTvTips.setText(Html.fromHtml(mTips));
                        //收到提示 将一直展示
                        mRlvBottomBar.setVisibility(View.VISIBLE);
                    }else {
                        mRlvBottomBar.setVisibility(View.GONE);
                    }
                    break;
                case "program"://接收到推送的节目包
                    showInfo("收到节目");
                    BProgram mProgram = JSON.parseObject(msg.obj.toString(), BProgram.class);
                    mPresenter.downProgram(mProgram.getData());

                    break;
            }
        }
    }
}
