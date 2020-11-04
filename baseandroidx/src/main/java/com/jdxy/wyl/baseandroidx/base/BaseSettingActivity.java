package com.jdxy.wyl.baseandroidx.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ReflectUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.bean.BBaseSetting;
import com.jdxy.wyl.baseandroidx.bean.ConfigSetting;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseSettingActivity extends AppCompatActivity {
    public String TAG = "【" + this.getClass().getSimpleName() + "】";
    public static final String SOCKET = "【socket】";
    public static final String HTTP = "【http】";
    public static final String ERROR = "【error】";
    public Context mContext;

    public CommonAdapter<ConfigSetting> mAdapter;
    public LinearLayout mLlRootSetting;

    public BaseHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_setting);
        mContext = this;
        mLlRootSetting = findViewById(R.id.llRootSetting);
        mHolder = new BaseHolder(mLlRootSetting);
        mHolder.mTvBack.setOnClickListener(v -> BtnClick(v));
        mHolder.mImgBack.setOnClickListener(v -> BtnClick(v));


    }


    public BBaseSetting mSetting;
    public List<ConfigSetting> datas = new ArrayList<>();

    /**
     * 获取本地所有配置
     */
    public void getLocalSettings() {
        //获取本地所有配置
        Map<String, ?> mAll = ToolSP.getAll();
        if (mSetting == null) {
            mSetting = new BBaseSetting();
        }
        //生成对应的设置对象
        datas.clear();
        Field[] mDeclaredFields = mSetting.getClass().getDeclaredFields();
        for (Field f : mDeclaredFields) {
            IDescription mAnnotation = f.getAnnotation(IDescription.class);
            f.setAccessible(true);
            try {
                if (mAll.containsKey(f.getName())) {
                    ConfigSetting c = new ConfigSetting();
                    c.setDescription(mAnnotation.value());
                    c.setName(f.getName());
                    c.setValue(mAll.get(f.getName()).toString());
                    datas.add(c);
                    ToolLog.e(TAG, "getLocalSettings: " + c.getName() + " " + c.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mAdapter = new CommonAdapter<ConfigSetting>(this, R.layout.base_item_setting, datas) {
            @Override
            protected void convert(ViewHolder holder, ConfigSetting configSetting, int position) {
                holder.setText(R.id.tvDesc, configSetting.getDescription());
                EditText mView = (EditText) holder.getView(R.id.etContent);
                holder.setText(R.id.etContent, configSetting.getValue());
                if (!configSetting.getDescription().contains(":")) {
                    mView.setEnabled(false);
                }
                if (mView.getTag() != null && mView.getTag() instanceof TextWatcher) {
                    mView.removeTextChangedListener((TextWatcher) mView.getTag());
                }

                TextWatcher intervalTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // datas.get(position).setValue(s.toString());
                        ReflectUtils.reflect(mSetting).field(configSetting.getName(), s.toString());

                    }
                };

                mView.addTextChangedListener(intervalTextWatcher);
                mView.setTag(intervalTextWatcher);

            }
        };

        mHolder.mRlvSetting.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mHolder.mRlvSetting.setAdapter(mAdapter);
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


    public void BtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnTestAlarm) {
        } else if (id == R.id.btnEnableAlarm) {
        } else if (id == R.id.btnVoiceSystemAdd) {
        } else if (id == R.id.btnVoiceSystemMinus) {
        } else if (id == R.id.btnVoiceMusicAdd) {
        } else if (id == R.id.btnVoiceMusicMinus) {
        } else if (id == R.id.btnVoiceRingAdd) {
        } else if (id == R.id.btnVoiceRingmMinus) {
        } else if (id == R.id.btnVoiceAlarmAdd) {
        } else if (id == R.id.btnVoiceAlarmMinus) {
        } else if (id == R.id.btnTestTts) {
        } else if (id == R.id.btnEnableTts) {
        } else if (id == R.id.btnGetSetting) {
            getLocalSettings();
        } else if (id == R.id.tvBack || id == R.id.imgBack) {
            finish();
        }
        ToolLog.e(TAG, "BtnClick: " + view.getId());
    }


    static class BaseHolder {
        @BindView(R2.id.imgBack)
        ImageView mImgBack;
        @BindView(R2.id.tvBack)
        TextView mTvBack;
        @BindView(R2.id.tvTitleLeft)
        TextView mTvTitleLeft;
        @BindView(R2.id.tvTitleMiddle)
        TextView mTvTitleMiddle;
        @BindView(R2.id.rlTopBar)
        RelativeLayout mRlTopBar;
        @BindView(R2.id.tvSystemTime)
        TextView mTvSystemTime;
        @BindView(R2.id.btnTestAlarm)
        Button mBtnTestAlarm;
        @BindView(R2.id.btnEnableAlarm)
        Button mBtnEnableAlarm;
        @BindView(R2.id.btnVoiceSystemAdd)
        Button mBtnVoiceSystemAdd;
        @BindView(R2.id.btnVoiceSystemMinus)
        Button mBtnVoiceSystemMinus;
        @BindView(R2.id.btnVoiceMusicAdd)
        Button mBtnVoiceMusicAdd;
        @BindView(R2.id.btnVoiceMusicMinus)
        Button mBtnVoiceMusicMinus;
        @BindView(R2.id.btnVoiceRingAdd)
        Button mBtnVoiceRingAdd;
        @BindView(R2.id.btnVoiceRingmMinus)
        Button mBtnVoiceRingmMinus;
        @BindView(R2.id.btnVoiceAlarmAdd)
        Button mBtnVoiceAlarmAdd;
        @BindView(R2.id.btnVoiceAlarmMinus)
        Button mBtnVoiceAlarmMinus;
        @BindView(R2.id.btnTestTts)
        Button mBtnTestTts;
        @BindView(R2.id.btnEnableTts)
        Button mBtnEnableTts;
        @BindView(R2.id.btnGetSetting)
        Button mBtnGetSetting;
        @BindView(R2.id.rlvSetting)
        RecyclerView mRlvSetting;

        BaseHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}