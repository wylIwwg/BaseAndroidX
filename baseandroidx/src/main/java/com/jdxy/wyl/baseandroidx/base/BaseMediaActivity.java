package com.jdxy.wyl.baseandroidx.base;

import android.os.Bundle;
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
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.view.ScrollTipsView;
import com.jdxy.wyl.baseandroidx.view.SuperBanner;


/**
 * 描述 媒体展示界面
 * 作者 wyl
 * 日期 2021/2/4 16:54
 */
public class BaseMediaActivity extends BaseHosActivity {
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
    public void initListener() {
        super.initListener();

    }

    @Override
    public void moreMessage(String type, String socket) {
        super.moreMessage(type, socket);
        JSONObject mObject = JSON.parseObject(socket, JSONObject.class);
        String data = null;
        if (socket.contains("data")) {
            data = mObject.getString("data");
        }
        switch (type) {
            case "program"://接收到推送的节目包
                BProgram mProgram = JSON.parseObject(socket, BProgram.class);
                mPresenter.downProgram(mProgram.getData());
                break;
        }
    }


}
