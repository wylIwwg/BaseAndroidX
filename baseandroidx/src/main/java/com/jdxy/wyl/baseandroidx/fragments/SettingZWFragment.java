package com.jdxy.wyl.baseandroidx.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.base.IDescription;
import com.jdxy.wyl.baseandroidx.bean.BAddress;
import com.jdxy.wyl.baseandroidx.bean.BBaseSetting;
import com.jdxy.wyl.baseandroidx.bean.ConfigSetting;
import com.jdxy.wyl.baseandroidx.thread.JsonCallBack;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.view.WrapLinearLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingZWFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingZWFragment extends Fragment {


    private static final String TAG = " SettingFragment ";

    public SettingZWFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingZWFragment newInstance(String api, String apps, boolean clear) {
        SettingZWFragment fragment = new SettingZWFragment();
        Bundle args = new Bundle();
        args.putString(IConfigs.SP_API, api);
        args.putString(IConfigs.INTENT_APP_TYPE, apps);
        args.putBoolean(IConfigs.INTENT_CLEAR_APP_TYPE, clear);
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
    String apps;
    boolean clear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mInflate = inflater.inflate(R.layout.fragment_setting_zw, container, false);
        mHolder = new Holder(mInflate);
        if (getArguments() != null) {
            mApi = getArguments().getString(IConfigs.SP_API);
            apps = getArguments().getString(IConfigs.INTENT_APP_TYPE);
            clear = getArguments().getBoolean(IConfigs.INTENT_CLEAR_APP_TYPE);
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


    BAddress mAddress;
    CommonAdapter<BAddress.Area> mAreaAdapter;
    CommonAdapter<BAddress.Floor> mFloorAdapter;

    CommonAdapter<BAddress.Unit> mUnitAdapter;
    CommonAdapter<BAddress.Window> mWindAdapter;

    TextView tvUnit = null;
    TextView tvFloor = null;
    TextView tvArea = null;
    TextView tvWindow = null;
    Holder mHolder;
    List<TextView> areas = new ArrayList<>();

    final List<BAddress.Unit> mUnits = new ArrayList<>();
    final List<BAddress.Floor> mFloors = new ArrayList<>();
    final List<BAddress.Area> mAreas = new ArrayList<>();
    final List<BAddress.Window> mWinds = new ArrayList<>();

    RadioButton rblast;

    public void showSetting() {

        mHolder.mEtIp.setText(ToolSP.getDIYString(IConfigs.SP_IP));
        mHolder.mEtPort.setText(ToolSP.getDIYString(IConfigs.SP_PORT_HTTP));
        mHolder.mEtSocketPort.setText(ToolSP.getDIYString(IConfigs.SP_PORT_SOCKET));

        mHolder.mEtDomainName.setText(ToolSP.getDIYString(IConfigs.SP_DOMAIN_NAME));


        //设置项目名  获取系统项目名称
        mHolder.mEtProjectName.setText(ToolSP.getDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME));


        mHolder.mBtnGetArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mHost;
                String api;

                //优先使用域名
                if (!TextUtils.isEmpty(mHolder.mEtDomainName.getText().toString())) {
                    //使用域名
                    ToolSP.putDIYString(IConfigs.SP_DOMAIN_NAME, mHolder.mEtDomainName.getText().toString());
                    mHost = mHolder.mEtDomainName.getText().toString() + mHolder.mEtProjectName.getText().toString();
                } else {
                    String ip = mHolder.mEtIp.getText().toString();
                    String port = mHolder.mEtPort.getText().toString();
                    if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port) && RegexUtils.isIP(ip)) {
                        //使用IP 和端口
                        mHost = String.format(IConfigs.HOST, ip, port);
                        //设置项目名  修改后设置的
                        mHost += mHolder.mEtProjectName.getText().toString();

                    } else {
                        Toasty.error(getActivity(), "请输入合法的ip地址：" + ip).show();
                        return;
                    }
                    ToolSP.putDIYString(IConfigs.SP_IP, ip);
                    ToolSP.putDIYString(IConfigs.SP_PORT_HTTP, port);
                    ToolSP.putDIYString(IConfigs.SP_DOMAIN_NAME, "");
                }
                ToolSP.putDIYString(IConfigs.SP_HOST, mHost);
                ToolSP.putDIYString(IConfigs.SP_PORT_SOCKET, mHolder.mEtSocketPort.getText().toString());

                api = mHost + mApi;

                String pn = mHolder.mEtProjectName.getText().toString();
                if (!TextUtils.isEmpty(pn)) {
                    ToolSP.putDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME, pn);
                    if (!pn.startsWith("/"))
                        pn = "/" + pn;
                    ToolSP.putDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME, pn);
                }
                //onClick: /baseConsultaioninfo/departAlls
                ToolLog.efile("SETTING", "onClick: " + api);

                JSONObject jo = new JSONObject();
                jo.put("timestamp", System.currentTimeMillis());
                jo.put("token", "123");
                OkGo.<BAddress>post(api)
                        .params("method", IConfigs.METHOD_AREA)
                        .params("checkinfo", jo.toJSONString())
                        .params("content", "{}")
                        .execute(new JsonCallBack<BAddress>(BAddress.class) {
                            @Override
                            public void onSuccess(Response<BAddress> response) {
                                if (response != null) {
                                    mHolder.mLlArea.setVisibility(View.VISIBLE);
                                    mAddress = response.body();
                                    if (mAddress != null && "200".equals(mAddress.getCode())) {
                                        mUnits.clear();
                                        mAreas.clear();
                                        mFloors.clear();
                                        mWinds.clear();
                                        mUnits.addAll(mAddress.getData().getUnit());
                                        mUnitAdapter.notifyDataSetChanged();
                                        mAreaAdapter.notifyDataSetChanged();
                                        mFloorAdapter.notifyDataSetChanged();
                                        mWindAdapter.notifyDataSetChanged();
                                    } else {
                                        Toasty.error(getActivity(), "获取位置信息为空", Toast.LENGTH_LONG, true).show();

                                    }

                                } else
                                    Toasty.error(getActivity(), "获取位置信息失败:" + (response == null ? "" : response.body()), Toast.LENGTH_LONG, true).show();

                            }

                            @Override
                            public void onError(Response<BAddress> response) {
                                super.onError(response);
                                Toasty.error(getActivity(), "获取位置信息失败:" + (response == null ? "" : response.body()), Toast.LENGTH_LONG, true).show();
                            }
                        });


            }
        });
        mHolder.mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.exitApp();
            }
        });

        mHolder.mBtnShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSP.putDIYBoolean(IConfigs.SP_SHOWLOG, true);
                getActivity().finish();
            }
        });

        mHolder.mBtnShowSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalSettings();
            }
        });

        mHolder.mBtnClearSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHolder.mBtnClearSetting.getText().toString().equals("清除数据"))
                    mHolder.mBtnClearSetting.setText("确定吗？");
                else {
                    //清楚数据 重启
                    ToolSP.clearAll();
                    Toasty.info(getActivity(), "数据已清除，即将重启").show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppUtils.relaunchApp(true);
                        }
                    }, 1000);
                }
            }
        });

        mHolder.mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ip = mHolder.mEtIp.getText().toString();
                //未输入域名  则判断ip
                if (TextUtils.isEmpty(mHolder.mEtDomainName.getText().toString()) && !RegexUtils.isIP(ip)) {
                    Toasty.error(getActivity(), "请输入合法的ip地址：" + ip).show();
                    return;
                }
                //优先使用域名
                if (!TextUtils.isEmpty(mHolder.mEtDomainName.getText().toString())) {
                    ToolSP.putDIYString(IConfigs.SP_DOMAIN_NAME, mHolder.mEtDomainName.getText().toString());
                }
                //重新启动应用
                ToolSP.putDIYString(IConfigs.SP_IP, ip);
                ToolSP.putDIYString(IConfigs.SP_PORT_HTTP, mHolder.mEtPort.getText().toString());
                ToolSP.putDIYString(IConfigs.SP_PORT_SOCKET, mHolder.mEtSocketPort.getText().toString());
                String pn = mHolder.mEtProjectName.getText().toString().trim();
                if (!TextUtils.isEmpty(pn)) {
                    ToolSP.putDIYString(IConfigs.SP_MODIFIED_PROJECT_NAME, pn);
                    if (!pn.startsWith("/"))
                        pn = "/" + pn;
                    ToolSP.putDIYString(IConfigs.SP_DEFAULT_PROJECT_NAME, pn);
                }


                if (tvUnit == null || areas.size() == 0 || tvFloor == null || tvWindow == null) {

                } else {
                    String are = "";
                    for (int i = 0; i < areas.size(); i++) {
                        if (i < areas.size() - 1)
                            are += areas.get(i).getTag().toString() + ",";
                        else are += areas.get(i).getTag().toString();
                    }

                    ToolSP.putDIYString(IConfigs.SP_FLOOR_ID, tvFloor.getTag().toString());
                    ToolSP.putDIYString(IConfigs.SP_AREA_ID, are);
                    ToolSP.putDIYString(IConfigs.SP_UNIT_ID, tvUnit.getTag().toString());
                    ToolSP.putDIYString(IConfigs.SP_WINDOW_NUM, tvWindow.getTag().toString());
                }


                //延迟启动
                //验证位置位置数据 后再启动
                //取消确认弹窗
              /*  if (TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_CLINIC_ID)) || TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_DEPART_ID))) {
                    new AlertDialog.Builder(getActivity()).setMessage("位置信息未设置，是否重启？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToolLog.efile(TAG, "【保存位置】: " + ToolSP.getDIYString(IConfigs.SP_CLINIC_ID));
                            ToolLog.efile(TAG, "【保存位置】: " + ToolSP.getDIYString(IConfigs.SP_DEPART_ID));
                            AppUtils.relaunchApp(true);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();

                } else*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.relaunchApp(true);
                    }
                }, 500);

            }
        });


        mUnitAdapter = new CommonAdapter<BAddress.Unit>(getActivity(), R.layout.item_area, mUnits) {
            @Override
            protected void convert(ViewHolder holder, final BAddress.Unit Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getName());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvUnit != null) {
                            tvUnit.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvUnit = tv;
                        tvUnit.setTag(Unit.getUnitid());

                        if (mAddress != null) {
                            int id = Unit.getUnitid();
                            List<BAddress.Floor> mFloor = mAddress.getData().getFloor();
                            mFloors.clear();
                            for (BAddress.Floor v : mFloor) {
                                if (v.getUnitid() == id) {
                                    mFloors.add(v);
                                }
                            }
                            tvFloor = null;
                            tvArea = null;

                            mAreas.clear();

                            mFloorAdapter.notifyDataSetChanged();
                            mAreaAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        };
        mFloorAdapter = new CommonAdapter<BAddress.Floor>(getActivity(), R.layout.item_area, mFloors) {
            @Override
            protected void convert(ViewHolder holder, final BAddress.Floor Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getFloor());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvFloor != null) {
                            tvFloor.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvFloor = tv;
                        tvFloor.setTag(Unit.getFloor());

                        if (mAddress != null) {
                            List<BAddress.Area> mArea = mAddress.getData().getArea();
                            mAreas.clear();
                            for (BAddress.Area v : mArea) {
                                if (v.getFloor().equals(Unit.getFloor())) {
                                    mAreas.add(v);
                                }
                            }
                            tvArea = null;
                            mAreaAdapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        };
        mAreaAdapter = new CommonAdapter<BAddress.Area>(getActivity(), R.layout.item_area, mAreas) {
            @Override
            protected void convert(ViewHolder holder, final BAddress.Area Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getArea());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (areas.contains(tv)) {
                            tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                            areas.remove(tv);
                        } else {
                            tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                            tv.setTag(Unit.getArea());
                            areas.add(tv);
                        }

                        if (mAddress != null) {
                            List<BAddress.Window> mWindows = mAddress.getData().getWindow();
                            mWinds.clear();

                            for (BAddress.Window v : mWindows) {
                                if (v.getArea().equals(Unit.getArea())) {
                                    mWinds.add(v);
                                }
                            }
                            tvWindow = null;
                            mWindAdapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        };
        mWindAdapter = new CommonAdapter<BAddress.Window>(getActivity(), R.layout.item_area, mWinds) {
            @Override
            protected void convert(ViewHolder holder, final BAddress.Window Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getWindownum());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvWindow != null) {
                            tvWindow.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvWindow = tv;
                        tvWindow.setTag(Unit.getWindownum());
                    }
                });
            }
        };


        mHolder.mRlvUnit.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mRlvUnit.setAdapter(mUnitAdapter);
        mHolder.mRlFloor.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mRlFloor.setAdapter(mFloorAdapter);
        mHolder.mRlvArea.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mRlvArea.setAdapter(mAreaAdapter);
        mHolder.mRlvWindow.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.mRlvWindow.setAdapter(mWindAdapter);
    }


    static class Holder {
        @BindView(R2.id.etIp)
        EditText mEtIp;
        @BindView(R2.id.etPort)
        EditText mEtPort;
        @BindView(R2.id.etSocketPort)
        EditText mEtSocketPort;
        @BindView(R2.id.etProjectName)
        EditText mEtProjectName;
        @BindView(R2.id.etDomainName)
        EditText mEtDomainName;
        @BindView(R2.id.btnGetArea)
        Button mBtnGetArea;
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
        WrapLinearLayout mRgSynthesisType;
        @BindView(R2.id.btnClearSetting)
        Button mBtnClearSetting;
        @BindView(R2.id.rlvUnit)
        RecyclerView mRlvUnit;
        @BindView(R2.id.rlvFloor)
        RecyclerView mRlFloor;
        @BindView(R2.id.rlvArea)
        RecyclerView mRlvArea;
        @BindView(R2.id.rlvWindow)
        RecyclerView mRlvWindow;
        @BindView(R2.id.rlvSetting)
        RecyclerView mRlvSetting;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}