package com.jdxy.wyl.baseandroidx.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.base.NetworkFeedBean;
import com.jdxy.wyl.baseandroidx.network.NetDeviceUtils;
import com.jdxy.wyl.baseandroidx.network.NetWorkUtils;
import com.jdxy.wyl.baseandroidx.network.NetworkTool;
import com.jdxy.wyl.baseandroidx.ping.PingView;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DeviceFragment extends Fragment {

    private Activity activity;
    private TextView tvPhoneContent;
    private TextView mTvAppInfo;
    private TextView tvContentInfo;
    private TextView tvWebInfo;
    private PingView tvNetInfo;
    private List<NetworkFeedBean> mNetworkFeedList;
    private static final int MESSAGE = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE) {
                String content = (String) msg.obj;
                tvWebInfo.setText(content);
            }
        }
    };

    public DeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_phone_info, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFindViewById(view);
        initData();
    }


    private void initFindViewById(View view) {
        tvPhoneContent = view.findViewById(R.id.tv_phone_content);
        mTvAppInfo = view.findViewById(R.id.tv_app_info);
        tvContentInfo = view.findViewById(R.id.tv_content_info);
        tvWebInfo = view.findViewById(R.id.tv_web_info);
        tvNetInfo = view.findViewById(R.id.tv_net_info);
    }

    private void initData() {
        //手机设备信息
        setPhoneInfo();
        //设置手机信息
        setAppInfo();
        //本机信息
        //比如mac地址，子网掩码，ip，wifi名称
        setLocationInfo();
        //网络诊断，也就是ping一下
        setPingInfo();
    }


    private void setPhoneInfo() {
        Application application = NetworkTool.getInstance().getApplication();
        final StringBuilder sb = new StringBuilder();
        sb.append("是否root:").append(NetDeviceUtils.isDeviceRooted());
        sb.append("\n系统硬件商:").append(NetDeviceUtils.getManufacturer());
        sb.append("\n设备的品牌:").append(NetDeviceUtils.getBrand());
        sb.append("\n手机的型号:").append(NetDeviceUtils.getModel());
        sb.append("\n设备版本号:").append(NetDeviceUtils.getId());
        sb.append("\nCPU的类型:").append(NetDeviceUtils.getCpuType());
        sb.append("\n系统的版本:").append(NetDeviceUtils.getSDKVersionName());
        sb.append("\n系统版本值:").append(NetDeviceUtils.getSDKVersionCode());
        sb.append("\nSd卡剩余控件:").append(NetDeviceUtils.getSDCardSpace(application));
        sb.append("\n系统剩余控件:").append(NetDeviceUtils.getRomSpace(application));
        sb.append("\n手机总内存:").append(NetDeviceUtils.getTotalMemory(application));
        sb.append("\n手机可用内存:").append(NetDeviceUtils.getAvailMemory(application));
        sb.append("\n手机分辨率:").append(NetDeviceUtils.getWidthPixels(application))
                .append("x").append(NetDeviceUtils.getRealHeightPixels(application));
        sb.append("\n屏幕尺寸:").append(NetDeviceUtils.getScreenInch(activity));
        tvPhoneContent.setText(sb.toString());
        tvPhoneContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkUtils.copyToClipBoard(activity, sb.toString());
            }
        });
    }


    private void setAppInfo() {
        Application application = NetworkTool.getInstance().getApplication();
        //版本信息
        String versionName = "";
        String versionCode = "";
        try {
            PackageManager pm = application.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(application.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = String.valueOf(pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("软件App包名:").append(NetworkTool.getInstance().getApplication().getPackageName());
        //  sb.append("\n是否是DEBUG版本:").append(AppUtils.isAppDebug());
        if (versionName != null && versionName.length() > 0) {
            sb.append("\n版本名称:").append(versionName);
            sb.append("\n版本号:").append(versionCode);
        }
        ApplicationInfo applicationInfo = application.getApplicationInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sb.append("\n最低系统版本号:").append(applicationInfo.minSdkVersion);
            sb.append("\n当前系统版本号:").append(applicationInfo.targetSdkVersion);
            sb.append("\n进程名称:").append(applicationInfo.processName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sb.append("\nUUID:").append(applicationInfo.storageUuid);
            }
            sb.append("\nAPK完整路径:").append(applicationInfo.sourceDir);
        }
        mTvAppInfo.setText(sb.toString());
    }


    private void setLocationInfo() {
        Application application = NetworkTool.getInstance().getApplication();
        StringBuilder sb = new StringBuilder();
        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            sb.append("wifi网络");
            sb.append("\nwifi信号强度:").append(NetDeviceUtils.getWifiState(application));
            sb.append("\nAndroidID:").append(NetDeviceUtils.getAndroidID(application));
            sb.append("\nMac地址:").append(NetDeviceUtils.getMacAddress(application).toUpperCase());
            sb.append("\nWifi名称:").append(NetDeviceUtils.getWifiName(application));
            int wifiIp = NetDeviceUtils.getWifiIp(application);
            String ip = NetDeviceUtils.intToIp(wifiIp);
            sb.append("\nWifi的Ip地址:").append(ip);
            DhcpInfo dhcpInfo = NetDeviceUtils.getDhcpInfo(application);
            if (dhcpInfo != null) {
                sb.append("\n子网掩码地址：").append(NetDeviceUtils.intToIp(dhcpInfo.netmask));
                sb.append("\n网关地址：").append(NetDeviceUtils.intToIp(dhcpInfo.gateway));
                sb.append("\nserverAddress：").append(NetDeviceUtils.intToIp(dhcpInfo.serverAddress));
                sb.append("\nDns1：").append(NetDeviceUtils.intToIp(dhcpInfo.dns1));
                sb.append("\nDns2：").append(NetDeviceUtils.intToIp(dhcpInfo.dns2));
            }
            tvContentInfo.setText(sb.toString());
        } else if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO){
            sb.append("无网络");
            sb.append("\nAndroidID:").append(NetDeviceUtils.getAndroidID(application));
            sb.append("\nMac地址:").append(ToolDevice.getMac());
            sb.append("\nIp地址:").append(NetworkUtils.getIPAddress(true));
            DhcpInfo dhcpInfo = NetDeviceUtils.getDhcpInfo(application);
            if (dhcpInfo != null) {
                sb.append("\n子网掩码地址：").append(NetDeviceUtils.intToIp(dhcpInfo.netmask));
                sb.append("\n网关地址：").append(NetDeviceUtils.intToIp(dhcpInfo.gateway));
                sb.append("\nserverAddress：").append(NetDeviceUtils.intToIp(dhcpInfo.serverAddress));
                sb.append("\nDns1：").append(NetDeviceUtils.intToIp(dhcpInfo.dns1));
                sb.append("\nDns2：").append(NetDeviceUtils.intToIp(dhcpInfo.dns2));
            }
            tvContentInfo.setText(sb.toString());
        }else {
            sb.append("移动网络");
            sb.append("\nAndroidID:").append(NetDeviceUtils.getAndroidID(application));
            sb.append("\nMac地址:").append(ToolDevice.getMac());
            sb.append("\nIp地址:").append(NetworkUtils.getIPAddress(true));
            DhcpInfo dhcpInfo = NetDeviceUtils.getDhcpInfo(application);
            if (dhcpInfo != null) {
                sb.append("\n子网掩码地址：").append(NetDeviceUtils.intToIp(dhcpInfo.netmask));
                sb.append("\n网关地址：").append(NetDeviceUtils.intToIp(dhcpInfo.gateway));
                sb.append("\nserverAddress：").append(NetDeviceUtils.intToIp(dhcpInfo.serverAddress));
                sb.append("\nDns1：").append(NetDeviceUtils.intToIp(dhcpInfo.dns1));
                sb.append("\nDns2：").append(NetDeviceUtils.intToIp(dhcpInfo.dns2));
            }
            tvContentInfo.setText(sb.toString());
        }

    }


    private void setPingInfo() {
        tvNetInfo.setDeviceId(DeviceUtils.getAndroidID());
        tvNetInfo.setUserId(AppUtils.getAppPackageName());
        tvNetInfo.setVersionName(AppUtils.getAppVersionName());

        if (!TextUtils.isEmpty(ToolSP.getDIYString(IConfigs.SP_IP))) {
            tvNetInfo.pingHost(ToolSP.getDIYString(IConfigs.SP_IP));
        }
    }
}