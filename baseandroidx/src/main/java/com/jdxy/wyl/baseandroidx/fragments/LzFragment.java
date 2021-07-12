package com.jdxy.wyl.baseandroidx.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.bean.BVoiceSetting;
import com.jdxy.wyl.baseandroidx.tools.ToolVoiceXF;

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
        BVoiceSetting mBVoiceSetting=new BVoiceSetting();
       // ToolTtsXF.Instance().InitTtsSetting(mBVoiceSetting);
       // ToolVoiceXF.Instance().setVoiceSetting(mBVoiceSetting).InitTtsListener();
    }

    private void initFindViewById(View view) {
        mLlRootSetting = view.findViewById(R.id.llLzSetting);
        mHolder = new BaseHolder(mLlRootSetting);

        mHolder.mBtnTestAlarm.setOnClickListener(this::BtnClick);
        mHolder.mBtnEnableAlarm.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceSystemAdd.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceSystemMinus.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceMusicAdd.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceMusicMinus.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceRingAdd.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceRingmMinus.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceAlarmAdd.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnVoiceAlarmMinus.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnTestTts.setOnClickListener(v -> BtnClick(v));
        mHolder.mBtnEnableTts.setOnClickListener(v -> BtnClick(v));

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
            ToolVoiceXF.Instance().TtsSpeakTest(mHolder.mEtVoice.getText().toString());
        } else if (id == R.id.btnEnableTts) {
        } else if (id == R.id.btnGetSetting) {
        } else if (id == R.id.tvBack || id == R.id.imgBack) {
            // finish();
        }
    }

    static class BaseHolder {
        @BindView(R2.id.etVoice)
        EditText mEtVoice;

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