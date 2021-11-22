package com.jdxy.wyl.baseandroidx.tools;

import com.jdxy.wyl.baseandroidx.bean.BRegister;

/**
 * 描述 TODO
 * 作者 wyl
 * 日期 2021/2/5 10:23
 */
public class ToolRegister {
    static {
        System.loadLibrary("register");
    }

    public native String getVersion();

    public native BRegister checkRegisterState(String path, String key);
}
