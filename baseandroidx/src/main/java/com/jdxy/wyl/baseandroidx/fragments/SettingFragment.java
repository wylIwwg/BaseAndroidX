package com.jdxy.wyl.baseandroidx.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.base.IDescription;
import com.jdxy.wyl.baseandroidx.bean.BBaseSetting;
import com.jdxy.wyl.baseandroidx.bean.BHosSetting;
import com.jdxy.wyl.baseandroidx.bean.ConfigSetting;
import com.jdxy.wyl.baseandroidx.thread.JsonCallBack;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {


    private static final String TAG = " SettingFragment ";

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

    public BBaseSetting mSetting;
    public List<ConfigSetting> datas = new ArrayList<>();


    public CommonAdapter<ConfigSetting> mAdapter;

    /**
     * 获取本地所有配置
     */
    public void getLocalSettings() {

        if (mHolder.mRlvSetting.getVisibility() == View.VISIBLE) {
            mHolder.mRlvSetting.setVisibility(View.GONE);
            mHolder.mBtnShowSetting.setText("查看设置");
            return;
        }
        mHolder.mRlvSetting.setVisibility(View.VISIBLE);
        mHolder.mBtnShowSetting.setText("隐藏设置");
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
                    ToolLog.e(TAG, "getLocalSettings: " + c.getName() + " = " + c.getValue());
                    datas.add(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mAdapter = new CommonAdapter<ConfigSetting>(getActivity(), R.layout.base_item_setting, datas) {
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

        mHolder.mRlvSetting.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mHolder.mRlvSetting.setAdapter(mAdapter);
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


        //设置项目名  获取系统项目名称
        if (!TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME))) {
            mHolder.mTvModify.setText("* 项目名已修改*");
            mHolder.mTvModify.setTextColor(getResources().getColor(R.color.grey));
            mHolder.mEtProjectName.setText(ToolSP.getDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME));
            mHolder.mEtProjectName.setEnabled(false);
        } else {
            mHolder.mEtProjectName.setText(ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME));
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
                    String api = mApi;
                    //onClick: /baseConsultaioninfo/departAlls
                    ToolLog.efile("SETTING", "onClick: " + api);
                    if (!mApi.startsWith("http")) {//说明第一次
                        String mHost = String.format(IConfigs.HOST, ip, port);
                        //设置项目名  修改后设置的
                        mHost += mHolder.mEtProjectName.getText().toString();
                        ToolSP.putDIYString(IConfigs.SP_HOST, mHost);
                        mApi = mHost + mApi;
                        api = mApi;
                    }
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
                AppUtils.exitApp();
            }
        });

        if (ToolSP.getDIYBoolean(IConfigs.SP_SHOWLOG)) {
            mHolder.mBtnShowLog.setText("关闭日志");
        } else {
            mHolder.mBtnShowLog.setText("查看日志");
        }
        mHolder.mBtnShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSP.putDIYBoolean(IConfigs.SP_SHOWLOG, !ToolSP.getDIYBoolean(IConfigs.SP_SHOWLOG));
                getActivity().finish();

            }
        });

        mHolder.mBtnShowSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalSettings();
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
                String pn = mHolder.mEtProjectName.getText().toString();
                if (!TextUtils.isEmpty(pn)) {
                    ToolSP.putDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME, pn);
                    if (!pn.startsWith("/"))
                        pn = "/" + pn;
                    ToolSP.putDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME, pn);
                }
                AppUtils.relaunchApp(true);

            }
        });

        int type = ToolSP.getDIYInt(IConfigs.SP_APP_TYPE);

        switch (type) {
            case IConfigs.APP_TYPE_YaoFang://药房
                mHolder.mRbYaofang.setChecked(true);
                break;
            case IConfigs.APP_TYPE_YiJi://医技
                mHolder.mRbYiji.setChecked(true);
                break;
            case IConfigs.APP_TYPE_MenZhen://门诊
                mHolder.mRbMenzhen.setChecked(true);
                break;
        }


        mHolder.mRbYiji.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ToolSP.putDIYInt(IConfigs.SP_APP_TYPE, IConfigs.APP_TYPE_YiJi);
            }
        });
        mHolder.mRbYaofang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ToolSP.putDIYInt(IConfigs.SP_APP_TYPE, IConfigs.APP_TYPE_YaoFang);
            }
        });
        mHolder.mRbMenzhen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    ToolSP.putDIYInt(IConfigs.SP_APP_TYPE, IConfigs.APP_TYPE_MenZhen);
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
        @BindView(R2.id.tvModify)
        TextView mTvModify;
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
        @BindView(R2.id.btnShowSetting)
        Button mBtnShowSetting;
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

        @BindView(R2.id.rlvSetting)
        RecyclerView mRlvSetting;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}