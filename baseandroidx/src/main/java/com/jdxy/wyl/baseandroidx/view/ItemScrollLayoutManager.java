package com.jdxy.wyl.baseandroidx.view;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by wyl on 2019/11/16.
 */
public class ItemScrollLayoutManager extends LinearLayoutManager {

    public float scrollTime = 300f;

    public void setScrollTime(float scrollTime) {
        this.scrollTime = scrollTime;
    }

    public ItemScrollLayoutManager(Context context, int orientation) {
        super(context);
        if (orientation == RecyclerView.VERTICAL || orientation == RecyclerView.HORIZONTAL) {
            setOrientation(orientation);

        } else {
            setOrientation(RecyclerView.VERTICAL);
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    // 返回：滑过1px时经历的时间(ms)。
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return scrollTime / displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}