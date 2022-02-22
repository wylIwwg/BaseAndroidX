package com.jdxy.wyl.baseandroidx.view;

import android.app.Dialog;
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

import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolDisplay;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String tittle = getArguments().getString("tittle");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(tittle);
        builder.setMessage("查看日志");

        View mInflate = View.inflate(getActivity(), R.layout.dialog_logs, null);
        tvLogsReal = mInflate.findViewById(R.id.tvLogs);
        Button btnBgTans = mInflate.findViewById(R.id.btnBgTans);
        Button btnClose = mInflate.findViewById(R.id.btnClose);
        //设置内容可滚动
        tvLogsReal.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSP.putDIYBoolean(IConfigs.SP_SHOWLOG, false);
                dismiss();
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
            tvLogsReal.setMaxHeight(ToolDisplay.dip2px(getActivity(), 600));
        } else {
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

}
