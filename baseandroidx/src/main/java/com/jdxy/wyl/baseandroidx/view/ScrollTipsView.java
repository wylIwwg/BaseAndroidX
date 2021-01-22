package com.jdxy.wyl.baseandroidx.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 描述 TODO
 * 作者 wyl
 * 日期 2021/1/22 14:15
 */
public class ScrollTipsView extends AppCompatTextView {

    /*
    *  <com.jdxy.cqhcht.doorplate.MarqueeTextView
            android:marqueeRepeatLimit="marquee_forever"
            android:focusableInTouchMode="true"
            android:focusable="true"
            android:ellipsize="marquee"
            android:scrollHorizontally="true"
            android:singleLine="true"
            android:id="@+id/tvTips"
            android:layout_marginRight="40dp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_centerVertical="true"
            android:layout_marginLeft="50dp"
            android:text="请妥善保管好您的随身物品请妥善保管好您的随身物品请妥善保管好您的随身物品,谨防丢失"
            android:textColor="@color/white"
            android:textSize="15sp" />
    * */
    public ScrollTipsView(Context context) {
        super(context);
    }

    public ScrollTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写isFocused方法，让其一直返回true
    @Override
    public boolean isFocused() {
        return true;

    }


    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

    }
}
