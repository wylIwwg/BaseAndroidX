package com.jdxy.wyl.baseandroidx.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.adapter.CommonAdapter;
import com.jdxy.wyl.baseandroidx.adapter.ViewHolder;
import com.jdxy.wyl.baseandroidx.bean.BBanner;
import com.jdxy.wyl.baseandroidx.media.player.SimpleJZPlayer;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolCommon;
import com.jdxy.wyl.baseandroidx.tools.ToolDevice;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.jdxy.wyl.baseandroidx.view.DialogLogs;
import com.jdxy.wyl.baseandroidx.view.ItemScrollLayoutManager;
import com.jdxy.wyl.baseandroidx.view.SuperBanner;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import es.dmoral.toasty.Toasty;

public class BaseHosActivity extends AppCompatActivity implements IView {
    public String TAG = "【" + this.getClass().getSimpleName() + "】";
    public Context mContext;
    public RelativeLayout mBaseRlRoot;//根布局

    public boolean isRegistered = false;//是否注册
    public int mRegisterCode = 0;//注册code
    public String mRegisterViper = "";//注册码

    public String mHost;
    public String mMac;//设备mac地址

    public String[] mPermissions;//申请权限

    public Presenter mPresenter;

    //展示节目
    public RelativeLayout mRlvBanner;
    public SuperBanner mSuperBanner;
    public TextView mTvCover;

    //主内容
    public View mViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseRlRoot = findViewById(R.id.baseRlRoot);
        mRlvBanner = findViewById(R.id.rlvBanner);
        mTvCover = findViewById(R.id.tvCover);
        mSuperBanner = findViewById(R.id.banner);

        mPresenter = new Presenter(mContext, this);

        mMac = ToolDevice.getMac();


    }


    //展示未注册view
    public View viewRegister;

    /**
     * @param msg
     */
    public void showRegister(String msg) {
        if (viewRegister != null) {
            mBaseRlRoot.removeView(viewRegister);
            viewRegister = null;
        }
        viewRegister = View.inflate(mContext, R.layout.item_register, null);
        TextView tv = viewRegister.findViewById(R.id.tvRegister);
        tv.setText(msg);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ToolDisplay.dip2px(mContext, 250), ToolDisplay.dip2px(mContext, 180));
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        viewRegister.setLayoutParams(lp);
        mBaseRlRoot.addView(viewRegister);
        viewRegister.bringToFront();
    }

    CommonAdapter<BBanner> mBannerAdapter;
    public List<BBanner> mBanners;

    @Override
    public void showBanner(List<BBanner> banners) {
        if (banners != null && mBanners != null) {
            //banners不为空 说明已经获取到数据了 已经进行过初始化
            mBanners.clear();
            mBanners.addAll(banners);
            if (mBanners.size() > 1) {
                mBannerAdapter.setLoop(true);
                mSuperBanner.start();
            } else {
                mBannerAdapter.setLoop(false);
                mSuperBanner.stop();
            }
            return;
        }
        mBanners = new ArrayList<>();
        Jzvd.WIFI_TIP_DIALOG_SHOWED = true;
        //读取默认配置信息
        String scroll = ToolSP.getDIYString(IConfigs.SP_SETTING_SCROLL_TIME);
        int scrollTime = 0, delayTime = 0;
        if (scroll.length() > 0) {
            scrollTime = Integer.parseInt(scroll);

        }
        String delay = ToolSP.getDIYString(IConfigs.SP_SETTING_DELAY_TIME);
        if (delay.length() > 0) {
            delayTime = Integer.parseInt(delay);
        }

        scrollTime = Math.max(scrollTime, 1) * 100;
        delayTime = Math.max(delayTime, 10);
        mBannerAdapter = new CommonAdapter<BBanner>(mContext, R.layout.item_video, mBanners) {
            @Override
            protected void convert(ViewHolder holder, BBanner banner, int position) {
                SimpleJZPlayer mPlayer = holder.getView(R.id.videoplayer);

                if ("0".equals(banner.getType())) {
                    Glide.with(mContext).load(banner.getUrl()).into(mPlayer.thumbImageView);

                } else {
                    mPlayer.setUp(banner.getUrl(), "", Jzvd.SCREEN_FULLSCREEN);

                    //有且只有一个视频
                    if (position == 0 && mBanners.size() == 1) {
                        mPlayer.startButton.performClick();
                        mSuperBanner.stop();
                        mPlayer.setOnCompleteListener(new SimpleJZPlayer.CompleteListener() {
                            @Override
                            public void complete(SimpleJZPlayer player, String url, int screen) {
                                //mSuperBanner.start(true);
                                player.startButton.performClick();
                            }
                        });
                    }
                }
            }
        };

        mSuperBanner.setTypeTime(SuperBanner.ROLL_ITEM, delayTime * 1000);
        ItemScrollLayoutManager mLayoutManager = new ItemScrollLayoutManager(mContext, LinearLayoutManager.HORIZONTAL);
        mLayoutManager.setScrollTime(scrollTime);

        mSuperBanner.setLayoutManager(mLayoutManager);
        mSuperBanner.setAdapter(mBannerAdapter);
        mSuperBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (mBanners.size() < 1)
                    return;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int mVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int index = mVisibleItemPosition % mBanners.size();
                    if (index < mBanners.size() && index > 0) {
                        BBanner mBanner = mBanners.get(index);
                        if ("1".equals(mBanner.getType())) {
                            View mView = mSuperBanner.getChildAt(0);
                            if (mView != null) {
                                SimpleJZPlayer mPlayer = mView.findViewById(R.id.videoplayer);
                                mPlayer.startButton.performClick();
                                mSuperBanner.stop();
                                mPlayer.setOnCompleteListener(new SimpleJZPlayer.CompleteListener() {
                                    @Override
                                    public void complete(SimpleJZPlayer player, String url, int screen) {
                                        mSuperBanner.start(true);
                                    }
                                });
                            }
                        }
                    }

                }
            }

        });

        //禁止触摸
        mTvCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //获取文件数据
        String PathData = ToolSP.getDIYString(IConfigs.SP_PATH_DATA);
        if (FileUtils.listFilesInDir(PathData).size() > 0) {
            //主目录有数据
            mPresenter.listProgramFiles(PathData);
        } else {
            //否则尝试获取备份
            PathData = ToolSP.getDIYString(IConfigs.SP_PATH_DATA_BACKUP);
            if (FileUtils.listFilesInDir(PathData).size() > 0) {
                mPresenter.listProgramFiles(PathData);
            } else {
                showTips(IConfigs.MESSAGE_ERROR, "未获取到资源");
            }
        }

    }

    @Override
    public void showData() {
        if (mBanners != null) {
            mBanners.clear();
        }
        if (mSuperBanner != null) {
            mSuperBanner.stop();
            Jzvd.releaseAllVideos();
            mSuperBanner.clearOnScrollListeners();
        }

        mRlvBanner.setVisibility(View.GONE);
        mSuperBanner.stop();
        if (mViewContent != null)
            mViewContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTime(String dateStr, String timeStr, String week) {

    }

    @Override
    public void moreMessage(String type, String data) {

    }


    public void hasPermission() {
        //6.0以上申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissions != null && mPermissions.length > 0)
                mPresenter.checkPermission(mPermissions);
            else throw new RuntimeException("6.0以上申请权限 请先调用hasPermission方法");
        } else {
            initData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToolDisplay.hideBottomUIMenu(this);
        if (ToolSP.getDIYBoolean(IConfigs.SP_SHOWLOG)) {
            showLogsDialog();
        } else {
            if (mDialogLogs != null) {
                mDialogLogs.dismiss();
            }
        }
    }


    public void initListener() {

    }


    public void initData() {
        initListener();

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        mViewContent = view;
        if (mBaseRlRoot == null) return;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseRlRoot.addView(view, lp);
    }

    @Override
    public void showTips(int type, String message) {
        ToolLog.efile(TAG, "showTips: " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case IConfigs.MESSAGE_ERROR:
                        Toasty.error(mContext, message, Toast.LENGTH_LONG, true).show();
                        break;
                    case IConfigs.MESSAGE_INFO:
                        Toasty.info(mContext, message, Toast.LENGTH_LONG, true).show();
                        break;
                    case IConfigs.MESSAGE_SUCCESS:
                        Toasty.success(mContext, message, Toast.LENGTH_SHORT, true).show();
                        break;
                }

            }
        });
    }


    public DialogLogs mDialogLogs;

    public void showLogsDialog() {
        mDialogLogs = DialogLogs.newInstance();
        mDialogLogs.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        mDialogLogs.show(getSupportFragmentManager(), "");
    }

    /**
     * 上传截图
     *
     * @param url
     * @param sessionId
     */
    @Override
    public void uploadScreen(String url, String sessionId) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<String>() {
            @Override
            public String doInBackground() throws Throwable {
                //需要获取到根布局视图
                String res = ToolCommon.getBitmapString(mBaseRlRoot);
                mPresenter.uploadScreen(url, res, sessionId);
                return null;
            }

            @Override
            public void onSuccess(String result) {

            }
        });

    }


    /**
     * 添加设备
     *
     * @param clientId
     */
    public void addDevice(String clientId) {

    }

    @Override
    public void release() {
//        if (mTimeThread != null) {
//            mTimeThread.onDestroy();
//            mTimeThread = null;
//
//        }if
      /*  if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
            mDataHandler = null;
        }*/

        // ToolSocket.getInstance().stopConnect();
    }


    public void close() {
        this.finish();

    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
//        if (mDataHandler != null) {
//            mDataHandler.removeCallbacksAndMessages(null);
//            mDataHandler = null;
//        }
    }


    /**
     * 处理语音类容 是否显示
     *
     * @param txt
     */
    public void showVoice(String txt) {

    }


}
