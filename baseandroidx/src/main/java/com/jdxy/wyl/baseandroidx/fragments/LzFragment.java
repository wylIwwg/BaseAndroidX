package com.jdxy.wyl.baseandroidx.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ReflectUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.base.IDescription;
import com.jdxy.wyl.baseandroidx.base.LzSettingActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LzFragment extends Fragment {


    private static final String TAG = " LzFragment ";

    public LzFragment() {
        // Required empty public constructor
    }


    public LinearLayout mLlRootSetting;

    public BaseHolder mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFindViewById(view);
        initData();
    }

    private void initData() {
    }

    private void initFindViewById(View view) {
        mLlRootSetting = view.findViewById(R.id.llLzSetting);
        mHolder = new BaseHolder(mLlRootSetting);
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
        } else if (id == R.id.tvBack || id == R.id.imgBack) {
            // finish();
        }
    }

    static class BaseHolder {
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


        BaseHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}