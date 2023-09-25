package com.jdxy.wyl.baseandroidx.listeners;

import android.view.View;

/**
 * 防重单击事件
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    //两次点击按钮的最小间隔，目前为800
    private static final int MIN_CLICK_DELAY_TIME = 800;

    private long lastClickTime;

    public abstract void onSingleClick(View view);

    @Override

    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            onSingleClick(v);
        }
    }
}