package com.jdxy.wyl.baseandroidx.adapter;

/**
 * Created by wyl on 2019/11/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "  CommonAdapter ";
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private boolean isLoop;

    /**
     * 更新数据源
     *
     * @param datas
     */
    public void setDatas(List<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    /**
     * 设置重复循环
     *
     * @param loop
     */
    public void setLoop(boolean loop) {
        isLoop = loop;
        notifyDataSetChanged();
    }

    public CommonAdapter(final Context context, final int layoutId, List<T> data) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = data;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, mLayoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDatas != null && mDatas.size() > 0)
            convert(holder, mDatas.get(position % mDatas.size()), position);
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }


    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        if (isLoop) {
            return Integer.MAX_VALUE;
        }
        return mDatas == null ? 0 : mDatas.size();
    }

    protected abstract void convert(ViewHolder holder, T t, int position);


    protected OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ViewHolder holder, int position);

        boolean onItemLongClick(View view, ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}

