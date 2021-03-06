package com.jdxy.wyl.baseandroidx.view;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Scroller;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.jdxy.wyl.baseandroidx.bean.BBanner;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 纯图片轮播
 * Created by wyl on 2019/12/13.
 */
public class ImageBanner extends ViewPager {
    private static final String TAG = " ImageBanner ";
    Context mContext;

    // 自动播放的标识符
    static final int ACTION_PLAY = 1;
    //轮播图片的适配器
    BannerAdapter mAdapter;
    //图片轮播间隔时间
    int mDuration = 3500;
    //图片滚动时间
    int mScrollTime = 1000;

    Timer mTimer;
    MyHandler mHandler;
    boolean mAutoSwitch = false;

    public ImageBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHandler = new MyHandler();
        setListener();
    }

    private void setListener() {
        setOnTouchListener();
    }


    /**
     * 设置触摸页面的事件监听
     */


    private void setOnTouchListener() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    mAutoSwitch = false;
                }
                return false;
            }
        });
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ACTION_PLAY) {//若是播放操作
                if (!mAutoSwitch) {//若不是自动播放状态
                    mAutoSwitch = true;//设置为自动播放状态
                } else {//设置为下一个item
                    //取出当前item的下标
                    int currentItem = ImageBanner.this.getCurrentItem();
                    currentItem++;//递增
                    //设置当前item为下一个
                    ImageBanner.this.setCurrentItem(currentItem);
                }
            }
        }
    }


    public void reFresh(List<BBanner> BBanner) {
        mAdapter.reFresh(BBanner);
    }

    class BannerAdapter extends PagerAdapter {
        Context mContext;
        List<BBanner> mList;

        public BannerAdapter(Context context, List<BBanner> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size() < 1 ? 0 : Integer.MAX_VALUE;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView mImageView = new ImageView(mContext);
            ViewGroup.LayoutParams l = new RadioGroup.LayoutParams(-1, -1);
            mImageView.setLayoutParams(l);
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            container.addView(mImageView);
            final BBanner mData = mList.get(position % mList.size());

            Glide.with(mContext).load(mData.getUrl().length() > 0 ? mData.getUrl() : mData.getResId()).fitCenter().into(mImageView);

            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mBannerClickListener != null) {
                        mAutoSwitch = false;
                        mBannerClickListener.bannerClick(mData.getUrl());
                    }
                }
            });
            return mImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void reFresh(List<BBanner> BBanner) {
            if (mList != null) {
                mList.clear();
                mList.addAll(BBanner);
                notifyDataSetChanged();
            }
        }
    }

    public interface BannerClickListener {
        void bannerClick(String url);
    }

    BannerClickListener mBannerClickListener;

    public void setBannerClickListener(BannerClickListener lisener) {
        mBannerClickListener = lisener;
    }

    /**
     * 开始图片的轮播
     *
     * @param BBanner    轮播的图片集合
     * @param duration   轮播间隔
     * @param scrollTime 轮播时间（一张图片滚动的时间）
     */
    public void startPlayLoop(List<BBanner> BBanner, int duration, int scrollTime) {
        mDuration = duration;
        mScrollTime = scrollTime;
        startPlayLoop(BBanner);

    }

    /**
     * 开始图片的轮播
     *
     * @param BBanner
     */
    public void startPlayLoop(List<BBanner> BBanner) {
        if (mAdapter == null) {
            mAdapter = new BannerAdapter(mContext, BBanner);
            this.setAdapter(mAdapter);
            try {
                Field field = ViewPager.class.getDeclaredField("mScroller");
                field.setAccessible(true);
                MyScroller scroller = new MyScroller(mContext, new LinearInterpolator());
                scroller.setDuration(mScrollTime);
                scroller.startScroll(0, 0, 0, 0);
                field.set(this, scroller);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(ACTION_PLAY);
            }
        }, 10, mDuration);
    }

    /**
     * 停止图片轮播
     */
    public void stopPlayLoop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * ViewPager列表项滚动的距离、时间间隔的设置
     */
    class MyScroller extends Scroller {
        public MyScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
            // TODO Auto-generated constructor stub
        }

        int duration;//图片移动的时间间隔

        public void setDuration(int duration) {
            this.duration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy,
                                int duration) {
            super.startScroll(startX, startY, dx, dy, this.duration);
        }
    }
}

