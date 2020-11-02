package com.jdxy.wyl.baseandroidx.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseSettingActivity extends AppCompatActivity {
    public String TAG = "【" + this.getClass().getSimpleName() + "】";
    public static final String SOCKET = "【socket】";
    public static final String HTTP = "【http】";
    public static final String ERROR = "【error】";
    public Context mContext;
    ImageView mImgBack;
    TextView mTvBack;
    TextView mTvTitleLeft;
    TextView mTvTitleMiddle;
    RelativeLayout mRlTopBar;
    LinearLayout mLlRootSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_setting);
        mContext = this;

        mLlRootSetting = findViewById(R.id.llRootSetting);
        mTvBack = findViewById(R.id.tvBack);
        mImgBack = findViewById(R.id.imgBack);
        mTvTitleLeft = findViewById(R.id.tvTitleLeft);
        mTvTitleMiddle = findViewById(R.id.tvTitleMiddle);
        mRlTopBar = findViewById(R.id.rlTopBar);


        mTvBack.setOnClickListener(v -> onViewClicked());
        mImgBack.setOnClickListener(v -> onViewClicked());

    }

    @Override
    public void setContentView(int layoutResID) {

        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        ToolLog.e(TAG, "setContentView: " + mLlRootSetting);
        if (mLlRootSetting == null) return;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLlRootSetting.addView(view, lp);
    }


    public void onViewClicked() {
        ToolLog.e(TAG, "onViewClicked: ");
        this.finish();
    }
}