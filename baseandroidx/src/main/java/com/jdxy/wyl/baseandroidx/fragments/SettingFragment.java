package com.jdxy.wyl.baseandroidx.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.bean.BHosSetting;
import com.jdxy.wyl.baseandroidx.thread.JsonCallBack;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String api) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(IConfigs.SP_API, api);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    String mApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mInflate = inflater.inflate(R.layout.fragment_setting, container, false);
        mHolder = new Holder(mInflate);
        if (getArguments() != null) {
            mApi = getArguments().getString(IConfigs.SP_API);
            showSetting();
        }
        return mInflate;
    }

    CommonAdapter<BHosSetting.Data> mDepartAdapter;
    CommonAdapter<BHosSetting.Sublevel> mClinicAdapter;
    TextView tvDepart = null;
    TextView tvClinic = null;

    public void showSetting() {
        final List<BHosSetting.Data> mDepartsList = new ArrayList<>();
        final List<BHosSetting.Sublevel> mClinicList = new ArrayList<>();

        mHolder.mEtIp.setText(ToolSP.getDIYString(IConfigs.SP_IP));
        mHolder.mEtPort.setText(ToolSP.getDIYString(IConfigs.SP_PORT_HTTP));
        mHolder.mEtSocketPort.setText(ToolSP.getDIYString(IConfigs.SP_PORT_SOCKET));

        //如果是综合屏
        if (ToolSP.getDIYString(IConfigs.SP_APP_TYPE).equals("4")) {
            mHolder.mRgSynthesisType.setVisibility(View.VISIBLE);

        }

        //设置项目名  获取系统项目名称
        if (!TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME))) {
            mHolder.mEtProjectName.setText(ToolSP.getDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME));
            mHolder.mEtProjectName.setEnabled(false);
        }


        mHolder.mBtnGetArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mHolder.mEtIp.getText().toString().trim();
                String port = mHolder.mEtPort.getText().toString().trim();

                ToolSP.putDIYString(IConfigs.SP_IP, mHolder.mEtIp.getText().toString().trim());
                ToolSP.putDIYString(IConfigs.SP_PORT_HTTP, mHolder.mEtPort.getText().toString().trim());
                ToolSP.putDIYString(IConfigs.SP_PORT_SOCKET, mHolder.mEtSocketPort.getText().toString().trim());
                if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port)) {
                    String api=mApi;
                    if (!api.contains(ip)) {
                        String mp = mHolder.mEtProjectName.getText().toString();
                        ToolLog.efile("SETTING", "onClick: " + mp);
                        api = String.format(IConfigs.HOST, ip, port) +
                                (TextUtils.isEmpty(mp) ? ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME) : ("/" + mp)) + api;
                    }
                    ToolLog.efile("SETTING", "onClick: " + api);
                    OkGo.<BHosSetting>get(api).execute(new JsonCallBack<BHosSetting>(BHosSetting.class) {
                        @Override
                        public void onSuccess(Response<BHosSetting> response) {
                            if (response != null) {
                                BHosSetting mBody = response.body();
                                if (mBody != null && mBody.getState() == 1) {
                                    mDepartsList.clear();
                                    mClinicList.clear();
                                    mDepartsList.addAll(mBody.getData());
                                    mClinicAdapter.notifyDataSetChanged();
                                    mDepartAdapter.notifyDataSetChanged();
                                } else {
                                    Toasty.error(getActivity(), "获取失败：数据格式错误", Toast.LENGTH_LONG, true).show();
                                }

                            } else
                                Toasty.error(getActivity(), "获取失败：请求数据为空", Toast.LENGTH_LONG, true).show();
                        }

                        @Override
                        public void onError(Response<BHosSetting> response) {
                            super.onError(response);
                            Toasty.error(getActivity(), "获取失败：" + response.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                        }
                    });

                } else {
                    Toasty.error(getActivity(), "请设置ip和端口号", Toast.LENGTH_LONG, true).show();
                }

            }
        });
        mHolder.mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process.killProcess(Process.myPid());
            }
        });

        if (ToolSP.getDIYBoolean(IConfigs.SP_ShowLog)) {
            mHolder.mBtnShowLog.setText("关闭日志");
        } else {
            mHolder.mBtnShowLog.setText("查看日志");
        }
        mHolder.mBtnShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSP.putDIYBoolean(IConfigs.SP_ShowLog, !ToolSP.getDIYBoolean(IConfigs.SP_ShowLog));
                getActivity().finish();

            }
        });
        mHolder.mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //重新启动应用
                ToolSP.putDIYString(IConfigs.SP_IP, mHolder.mEtIp.getText().toString().trim());
                ToolSP.putDIYString(IConfigs.SP_PORT_HTTP, mHolder.mEtPort.getText().toString().trim());
                ToolSP.putDIYString(IConfigs.SP_PORT_SOCKET, mHolder.mEtSocketPort.getText().toString().trim());
                if (tvDepart != null) {
                    BHosSetting.Data depart = (BHosSetting.Data) tvDepart.getTag();
                    ToolSP.putDIYString(IConfigs.SP_DEPART_ID, depart.getId() + "");
                    ToolSP.putDIYString(IConfigs.SP_DEPART_NAME, depart.getDepartName() + "");
                }
                if (tvClinic != null) {
                    BHosSetting.Sublevel clinic = (BHosSetting.Sublevel) tvClinic.getTag();
                    ToolSP.putDIYString(IConfigs.SP_CLINIC_NAME, clinic.getName() + "");
                    ToolSP.putDIYString(IConfigs.SP_CLINIC_ID, clinic.getId() + "");

                }
                if (!TextUtils.isEmpty(mHolder.mEtProjectName.getText().toString())) {
                    ToolSP.putDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME, mHolder.mEtProjectName.getText().toString());
                    ToolSP.putDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME, "/" + mHolder.mEtProjectName.getText().toString());
                }

                AppUtils.relaunchApp(true);

            }
        });

        int type = ToolSP.getDIYInt(IConfigs.SP_SYNTHESIS_TYPE);

        switch (type) {
            case IConfigs.SYNTHESIS_TYPE_YaoFang://药房
                mHolder.mRbYaofang.setChecked(true);
                break;
            case IConfigs.SYNTHESIS_TYPE_YiJi://医技
                mHolder.mRbYiji.setChecked(true);
                break;
            case IConfigs.SYNTHESIS_TYPE_MenZhen://门诊
                mHolder.mRbMenzhen.setChecked(true);
                break;
        }


        mHolder.mRbYiji.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ToolSP.putDIYInt(IConfigs.SP_SYNTHESIS_TYPE, IConfigs.SYNTHESIS_TYPE_YiJi);
            }
        });
        mHolder.mRbYaofang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ToolSP.putDIYInt(IConfigs.SP_SYNTHESIS_TYPE, IConfigs.SYNTHESIS_TYPE_YaoFang);
            }
        });
        mHolder.mRbMenzhen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ToolSP.putDIYInt(IConfigs.SP_SYNTHESIS_TYPE, IConfigs.SYNTHESIS_TYPE_MenZhen);
            }
        });


        mDepartAdapter = new CommonAdapter<BHosSetting.Data>(getActivity(), R.layout.item_area, mDepartsList) {
            @Override
            protected void convert(ViewHolder holder, final BHosSetting.Data depart, int position) {
                holder.setText(R.id.tvArea, depart.getDepartName());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main3));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvDepart != null) {
                            tvDepart.setBackgroundColor(getResources().getColor(R.color.main3));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main));
                        tvDepart = tv;
                        tvDepart.setTag(depart);

                        int id = depart.getId();
                        List<BHosSetting.Sublevel> mSublevel = depart.getSublevel();
                        mClinicList.clear();
                        tvClinic = null;
                        mClinicList.clear();
                        mClinicList.addAll(mSublevel);
                        mClinicAdapter.notifyDataSetChanged();
                    }
                });

            }
        };

        mClinicAdapter = new CommonAdapter<BHosSetting.Sublevel>(getActivity(), R.layout.item_area, mClinicList) {
            @Override
            protected void convert(ViewHolder holder, final BHosSetting.Sublevel clinic, int position) {
                holder.setText(R.id.tvArea, clinic.getName());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main3));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvClinic != null) {
                            tvClinic.setBackgroundColor(getResources().getColor(R.color.main3));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main));
                        tvClinic = tv;
                        tvClinic.setTag(clinic);
                    }
                });
            }
        };

        mHolder.mRlvClinic.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mRlvClinic.setAdapter(mClinicAdapter);
        mHolder.mRlvDepart.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mRlvDepart.setAdapter(mDepartAdapter);
    }

    Holder mHolder;


    static class Holder {
        @BindView(R2.id.etIp)
        EditText mEtIp;
        @BindView(R2.id.etPort)
        EditText mEtPort;
        @BindView(R2.id.etSocketPort)
        EditText mEtSocketPort;
        @BindView(R2.id.etProjectName)
        EditText mEtProjectName;
        @BindView(R2.id.btnGetArea)
        Button mBtnGetArea;
        @BindView(R2.id.rlvDepart)
        RecyclerView mRlvDepart;
        @BindView(R2.id.rlvClinic)
        RecyclerView mRlvClinic;
        @BindView(R2.id.btnConfirm)
        Button mBtnConfirm;
        @BindView(R2.id.btnShowLog)
        Button mBtnShowLog;
        @BindView(R2.id.btnClose)
        Button mBtnClose;
        @BindView(R2.id.llArea)
        LinearLayout mLlArea;
        @BindView(R2.id.popRoot)
        LinearLayout mPopRoot;
        @BindView(R2.id.rgSynthesisType)
        RadioGroup mRgSynthesisType;

        @BindView(R2.id.rbMenzhen)
        RadioButton mRbMenzhen;
        @BindView(R2.id.rbYaofang)
        RadioButton mRbYaofang;
        @BindView(R2.id.rbYiji)
        RadioButton mRbYiji;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}