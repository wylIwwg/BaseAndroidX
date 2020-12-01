package com.jdxy.wyl.baseandroidx.media;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.base.BaseHospitalActivity;
import com.jdxy.wyl.baseandroidx.bean.BBanner;
import com.jdxy.wyl.baseandroidx.bean.BProgram;
import com.jdxy.wyl.baseandroidx.bean.BRegister;
import com.jdxy.wyl.baseandroidx.bean.BRegisterResult;
import com.jdxy.wyl.baseandroidx.listeners.RegisterListener;
import com.jdxy.wyl.baseandroidx.media.player.SimpleJZPlayer;
import com.jdxy.wyl.baseandroidx.media.zip.DownLoaderTask;
import com.jdxy.wyl.baseandroidx.thread.JsonCallBack;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolRegister;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.tools.ToolToast;
import com.jdxy.wyl.baseandroidx.view.ItemScrollLayoutManager;
import com.jdxy.wyl.baseandroidx.view.SuperBanner;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.Jzvd;
import es.dmoral.toasty.Toasty;

public class MediaActivity extends BaseHospitalActivity {
    List<BBanner> mBanners = new ArrayList<>();
    SuperBanner mMediaBanner;
    TextView mTvCover;


    File dir;
    String PATH_DATA;
    CommonAdapter<BBanner> mCommonAdapter;
    boolean isInit = false;
    String sessionId = "";
    boolean isUping = false;
    boolean stop = false;

    BProgram.Data mData;

    public int scrollTime;//图片滚动时间
    public int delayTime;//间隔多少时间滚动
    int cloudVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Jzvd.WIFI_TIP_DIALOG_SHOWED = true;

        mMediaBanner = findViewById(R.id.banner);
        mTvCover = findViewById(R.id.tvCover);

        PATH_DATA = ToolSP.getDIYString(IConfigs.SP_PATH_DATA);
        ToolLog.e(TAG, "onCreate: " + PATH_DATA);
        mIP = ToolSP.getDIYString(IConfigs.SP_IP);
        mSocketPort = ToolSP.getDIYString(IConfigs.SP_PORT_SOCKET);
        mHttpPort = ToolSP.getDIYString(IConfigs.SP_PORT_HTTP);

        //读取默认配置信息
        String scroll = ToolSP.getDIYString(IConfigs.SP_SETTING_SCROLL_TIME);
        if (scroll.length() > 0) {
            scrollTime = Integer.parseInt(scroll);

        }
        String delay = ToolSP.getDIYString(IConfigs.SP_SETTING_DELAY_TIME);
        if (delay.length() > 0) {
            delayTime = Integer.parseInt(delay);

        }

        scrollTime = Math.max(scrollTime, 5);
        delayTime = Math.max(delayTime, 10);


        mCommonAdapter = new CommonAdapter<BBanner>(mContext, R.layout.item_video, mBanners) {
            @Override
            protected void convert(ViewHolder holder, BBanner banner, int position) {
                SimpleJZPlayer mPlayer = holder.getView(R.id.videoplayer);
                if ("0".equals(banner.getType())) {
                    Glide.with(mContext).load(banner.getUrl()).into(mPlayer.thumbImageView);

                } else {
                    mPlayer.setUp(banner.getUrl(), "", Jzvd.SCREEN_FULLSCREEN);
                }
            }
        };

        mMediaBanner.setTypeTime(SuperBanner.ROLL_ITEM, delayTime * 1000);
        ItemScrollLayoutManager mLayoutManager = new ItemScrollLayoutManager(mContext, LinearLayoutManager.HORIZONTAL);
        mLayoutManager.setScrollTime(200);
        mMediaBanner.setLayoutManager(mLayoutManager);
        mMediaBanner.setAdapter(mCommonAdapter);
        mMediaBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int mVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int index = mVisibleItemPosition % mBanners.size();
                    if (index < mBanners.size() && index > 0) {
                        BBanner mBanner = mBanners.get(index);
                        if ("1".equals(mBanner.getType())) {
                            View mView = mMediaBanner.getChildAt(0);
                            if (mView != null) {
                                SimpleJZPlayer mPlayer = mView.findViewById(R.id.videoplayer);
                                mPlayer.startButton.performClick();
                                mMediaBanner.stop();
                                mPlayer.setOnCompleteListener(new SimpleJZPlayer.CompleteListener() {
                                    @Override
                                    public void complete(SimpleJZPlayer player, String url, int screen) {
                                        mMediaBanner.start(true);
                                    }
                                });
                            }
                        }
                    }

                }
            }

        });

        mTvCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initSocket();

        initData();

    }

    @Override
    public void initData() {
        super.initData();

        //如果主目录存在 找数据
        if (PATH_DATA != null && PATH_DATA.length() > 1) {
            File p = new File(PATH_DATA);
            if (p.exists() && p.isDirectory() && p.listFiles() != null) {
                dir = new File(PATH_DATA);
            } else {//找不到数据
                //备份目录找数据
                PATH_DATA = ToolSP.getDIYString(IConfigs.SP_PATH_DATA_BACKUP);
                if (PATH_DATA == null || PATH_DATA.length() < 1) {
                    ToolToast.showToast(mContext, "未获取到节目信息！", 2000);
                    return;
                }
                File p2 = new File(PATH_DATA);
                if (p2.exists() && p2.isDirectory() && p2.listFiles() != null && p2.listFiles().length > 0) {
                    dir = new File(PATH_DATA);
                } else {
                    ToolToast.showToast(mContext, "未获取到节目信息！", 2000);
                    return;
                }
            }
        } else {//主目录不存在 找备份
            PATH_DATA = ToolSP.getDIYString(IConfigs.SP_PATH_DATA_BACKUP);
            if (PATH_DATA == null || PATH_DATA.length() < 1) {
                ToolToast.showToast(mContext, "未获取到节目信息！", 2000);
                return;
            }
            File p = new File(PATH_DATA);
            if (p.exists() && p.isDirectory() && p.listFiles() != null && p.listFiles().length > 0) {
                dir = new File(PATH_DATA);
            } else {
                ToolToast.showToast(mContext, "未获取到节目信息！", 2000);
                return;
            }
        }


        if (dir.exists() && dir.isDirectory()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] files = dir.listFiles();
                    if (files != null && files.length > 0) {
                        mBanners.clear();
                        for (File f : files) {
                            String mPath = f.getAbsolutePath().toLowerCase();
                            BBanner mBanner = new BBanner();
                            mBanner.setUrl(f.getAbsolutePath());
                            ToolLog.e(TAG, "run: " + mPath);
                            if (mPath.endsWith("jpg") || mPath.endsWith("jpeg") || mPath.endsWith("png")) {
                                mBanner.setType("0");
                                mBanners.add(mBanner);
                            } else if (mPath.endsWith("mp4") || mPath.endsWith("avi") || mPath.endsWith("flv")) {
                                mBanner.setType("1");
                                mBanners.add(mBanner);

                            }
                        }
                    }

                    if (mBanners.size() > 0) {
                        mDataHandler.sendEmptyMessage(IConfigs.MSG_MEDIA_INIT);
                    } else {
                        close();
                    }

                }
            }).start();
        }


    }

    @Override
    public void userHandler(Message msg) {
        super.userHandler(msg);
        switch (msg.what) {
            case IConfigs.MSG_MEDIA_INIT:
                //控制轮播
                if (mBanners.size() > 1) {
                    mCommonAdapter.setLoop(true);
                    mMediaBanner.start();
                } else {
                    mCommonAdapter.setLoop(false);
                    mMediaBanner.stop();
                }

                break;

        }
    }

    /**
     * 添加设备信息
     *
     * @param clientId
     */
    @Override
    public void addDevice(String clientId) {

        BRegister mRegister = ToolRegister.Instance(mContext).getRegister();
        HttpParams hp = new HttpParams();
        hp.put("terminalIdentity", mMac);
        hp.put("terminalVersion", AppUtils.getAppVersionCode());
        hp.put("terminalClientId", clientId);
        hp.put("registerState", mRegisterCode);
        hp.put("roomId", ToolSP.getDIYString(IConfigs.SP_CLINIC_ID));
        hp.put("deptId", ToolSP.getDIYString(IConfigs.SP_DEPART_ID));
        hp.put("terminalType", ToolSP.getDIYString(IConfigs.SP_APP_TYPE));//终端设备类型：1-签到机；2-呼叫器；3-门牌屏；4-综合屏；5-排班机；6-评价器；7-自助机；8-信息屏；
        hp.put("systemType", 0);//终端系统类型: 0-Android；1-Windows；
        hp.put("terminalLimit", mRegister == null ? 0 : mRegister.getResidue());//终端注册到期天数 <0:永不过期 0:到期 >0:到期天数 由服务器注册时设置
        hp.put("terminalIp", NetworkUtils.getIPAddress(true));
        hp.put("terminalEncrypt", mRegisterViper);

        ToolLog.efile(TAG, "addDevice: " + JSON.toJSONString(hp));

        OkGo.<String>post(mHost + IConfigs.URL_ADD_TERMINAL)
                .params(hp)
                .tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                ToolLog.efile(TAG, "onSuccess: " + response.body());
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToolLog.efile(TAG, "onError: " + response.getException().toString());
                showError("初始化失败！" + (response.body() == null ? "" : response.body().toString()));
            }
        });
    }


}
