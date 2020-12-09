package com.jdxy.wyl.baseandroidx.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.tabs.TabLayout;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.fragments.BasePagerAdapter;
import com.jdxy.wyl.baseandroidx.fragments.DeviceFragment;
import com.jdxy.wyl.baseandroidx.fragments.LzFragment;
import com.jdxy.wyl.baseandroidx.fragments.PingFragment;
import com.jdxy.wyl.baseandroidx.fragments.SettingFragment;
import com.jdxy.wyl.baseandroidx.ping.PingView;
import com.jdxy.wyl.baseandroidx.thread.TimeThread;
import com.jdxy.wyl.baseandroidx.tools.ToolLZ;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.AutoSizeConfig;


public class CommonSettingActivity extends AppCompatActivity implements IView {
    ImageView mImgBack;
    TextView mTvBack;
    TextView mTvTitleLeft;
    TextView mTvTitleMiddle;
    RelativeLayout mRlTopBar;
    TextView mTvSystemTime;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    String mApi = "";

    Button mBtnLZSetting;
    Button mBtnPsw;
    EditText mEtPsw;
    LinearLayout mLlPsw;

    public static final int JSON_INDENT = 4;
    private String TAG = "CommonSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_setting);

        initFindViewById();
    }


    private void initFindViewById() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mTvSystemTime = findViewById(R.id.tvSystemTime);
        mLlPsw = findViewById(R.id.llPsw);
        mEtPsw = findViewById(R.id.etAdmin);
        mBtnLZSetting = findViewById(R.id.btnLZSetting);
        mBtnPsw = findViewById(R.id.btnPswConfirm);

        mTvBack = findViewById(R.id.tvBack);
        mImgBack = findViewById(R.id.imgBack);

        mTvBack.setOnClickListener(v -> {
            finish();
        });

        mImgBack.setOnClickListener(v -> {
            finish();
        });
        if (ToolLZ.Instance().isLZDevice()) {
            mBtnLZSetting.setVisibility(View.VISIBLE);
        }

        mBtnLZSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPsw()) {
                    showInfo("功能后续完善");
                }
            }
        });
        mBtnPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPsw()) {
                    initTabLayout();
                    mLlPsw.setVisibility(View.GONE);
                    mTabLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    boolean checkPsw() {
        String psw = mEtPsw.getText().toString();
        if (TextUtils.isEmpty(psw)) {
            showError("请输入密码");
            return false;
        }
        if ("sjjd".equals(psw) || "jdxy".equals(psw)) {
            return true;
        }
        showError("密码不正确");
        return false;
    }

    private void initTabLayout() {
        ArrayList<String> mTitleList = new ArrayList<>();
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mTitleList.add("区域设置");
        mTitleList.add("网络诊断");
        mTitleList.add("设备信息");

        Intent mIntent = getIntent();
        if (mIntent != null) {
            mApi = mIntent.getStringExtra("api");
            ToolLog.e(TAG, "initTabLayout: " + mApi);
        }

        mFragments.add(SettingFragment.newInstance(mApi));
        mFragments.add(PingFragment.newInstance(mApi));
        mFragments.add(new DeviceFragment());

        if (ToolLZ.Instance().isLZDevice()) {
            mTitleList.add("亮钻设备");
            mFragments.add(new LzFragment());
        }

        /*
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        BasePagerAdapter myAdapter = new BasePagerAdapter(supportFragmentManager,
                mFragments, mTitleList);
        mViewPager.setAdapter(myAdapter);
        // 左右预加载页面的个数
        mViewPager.setOffscreenPageLimit(mFragments.size());
        myAdapter.notifyDataSetChanged();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public Resources getResources() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            AutoSizeCompat.autoConvertDensity(super.getResources(), 960f, AutoSizeConfig.getInstance().getScreenWidth() > AutoSizeConfig.getInstance().getScreenHeight());
        return super.getResources();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            AutoSize.autoConvertDensity(this, 960f, true);

    }
    @Override
    public void showSuccess(String success) {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void showInfo(String info) {

    }

    @Override
    public void release() {

    }

    @Override
    public void initData() {

    }
}