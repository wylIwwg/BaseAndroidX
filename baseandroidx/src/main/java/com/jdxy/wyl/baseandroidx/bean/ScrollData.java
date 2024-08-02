package com.jdxy.wyl.baseandroidx.bean;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏幕滚动显示
 *
 * @param <T>
 */
public class ScrollData<T> {

    String tag;
    RecyclerView mRecyclerView;
    TextView mTextView;//用于展示文字
    List<T> data;//数据
    String totalCount;//总数
    String format;//数据格式

    public String getFormat() {
        return format == null ? "" : format;
    }

    public void setFormat(String format) {
        this.format = format == null ? "" : format;
    }

    public String getTotalCount() {
        return totalCount == null ? "" : totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount == null ? "" : totalCount;
    }

    public String getTag() {
        return tag == null ? "" : tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? "" : tag;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setTextView(TextView textView) {
        mTextView = textView;
    }

    public List<T> getData() {
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
