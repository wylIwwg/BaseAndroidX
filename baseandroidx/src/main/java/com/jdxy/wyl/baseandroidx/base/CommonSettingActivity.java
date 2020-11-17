package com.jdxy.wyl.baseandroidx.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.fragments.BasePagerAdapter;
import com.jdxy.wyl.baseandroidx.fragments.DeviceFragment;
import com.jdxy.wyl.baseandroidx.fragments.PingFragment;
import com.jdxy.wyl.baseandroidx.ping.PingView;
import com.jdxy.wyl.baseandroidx.thread.TimeThread;
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


public class CommonSettingActivity extends BaseHospitalActivity {
    ImageView mImgBack;
    TextView mTvBack;
    TextView mTvTitleLeft;
    TextView mTvTitleMiddle;
    RelativeLayout mRlTopBar;
    TextView mTvSystemTime;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    String mApi = "";


    public static final int JSON_INDENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_setting);


        initFindViewById();
        initTabLayout();

        startLocalTime();
    }

    @Override
    public void userHandler(Message msg) {
        super.userHandler(msg);
    }

    @Override
    public void showTime(String dateStr, String timeStr, String week) {
        mTvSystemTime.setText(dateStr + " " + timeStr);
    }

    private void initFindViewById() {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mTvSystemTime = findViewById(R.id.tvSystemTime);

        mTvBack = findViewById(R.id.tvBack);
        mImgBack = findViewById(R.id.imgBack);
        mTvBack.setOnClickListener(v -> {
            finish();
        });
        mImgBack.setOnClickListener(v -> {
            finish();
        });
    }


    private void initTabLayout() {
        ArrayList<String> mTitleList = new ArrayList<>();
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mTitleList.add("网络诊断");
        mTitleList.add("设备信息");
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mApi = mIntent.getStringExtra("api");
            ToolLog.e(TAG, "initTabLayout: " + mApi);
        }
        mFragments.add(PingFragment.newInstance(mApi));
        mFragments.add(new DeviceFragment());


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


}