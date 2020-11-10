package com.jdxy.wyl.baseandroidx.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wyl on 2020/5/11.
 */
public class DialogLogs extends DialogFragment {

    private static final String TAG = " DialogLogs  ";

    public static DialogLogs newInstance() {

        Bundle args = new Bundle();
        DialogLogs fragment = new DialogLogs();
        fragment.setArguments(args);
        return fragment;
    }

    TextView tvLogsReal;

    public void showSocketMsg(String msg) {
        if (localLogs)
            return;
        if (tvLogsReal != null) {
            tvLogsReal.setText(tvLogsReal.getText().toString() + "\n" + msg);
            tvLogsReal.computeScroll();
        }
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onResume();
    }

    boolean localLogs = true;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String tittle = getArguments().getString("tittle");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(tittle);
        builder.setMessage("查看日志");
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getContext(), "确定", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        View mInflate = View.inflate(getActivity(), R.layout.dialog_logs, null);
        TextView tvLogsLocal = mInflate.findViewById(R.id.tvLogsLocal);
        tvLogsReal = mInflate.findViewById(R.id.tvLogsReal);
        Button btnLogsLocal = mInflate.findViewById(R.id.btnLogsLocal);
        Button btnBgTans = mInflate.findViewById(R.id.btnBgTans);
        Button btnLogsReal = mInflate.findViewById(R.id.btnLogsReal);
        //设置内容可滚动
        tvLogsReal.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvLogsLocal.setMovementMethod(ScrollingMovementMethod.getInstance());

        btnLogsReal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLogsLocal.setText("");
                tvLogsReal.setText("");
                localLogs = false;
                ToolLog.e(TAG, "onClick:查看实时： " + tvLogsReal);

            }
        });
        btnLogsLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localLogs = true;
                List<File> mFiles = FileUtils.listFilesInDirWithFilter(IConfigs.PATH_LOG, new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        ToolLog.e("TAG", "accept: " + pathname);
                        return pathname.lastModified() > System.currentTimeMillis() - 24 * 60 * 60 * 1000 && pathname.getAbsolutePath().endsWith("txt");
                    }
                });
                tvLogsReal.setText("");
                if (mFiles.size() > 0 && FileUtils.isFileExists(mFiles.get(0))) {
                    String mFile2String = FileIOUtils.readFile2String(mFiles.get(0));
                    tvLogsLocal.setText(mFile2String);
                }

            }
        });
        AlertDialog alertDialog = builder.setView(mInflate).create();
        // 设置宽度为屏宽、位置在屏幕底部
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawableResource(R.color.halftrans);
        btnBgTans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.setBackgroundDrawableResource(R.color.white);
            }
        });
        //竖屏 960dp  横屏 540
        if (getResources().getConfiguration().orientation == 1) {
            tvLogsLocal.setMaxHeight(ToolDisplay.dip2px(getActivity(), 600));
            tvLogsReal.setMaxHeight(ToolDisplay.dip2px(getActivity(), 600));
        } else {
            tvLogsLocal.setMaxHeight(ToolDisplay.dip2px(getActivity(), 350));
            tvLogsReal.setMaxHeight(ToolDisplay.dip2px(getActivity(), 350));
        }
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        return alertDialog;
    }

    static class Holder {
        @BindView(R2.id.btnLogsLocal)
        Button mBtnLogsLocal;
        @BindView(R2.id.tvLogsLocal)
        TextView mTvLogsLocal;
        @BindView(R2.id.tvLogsReal)
        TextView mTvLogsReal;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
