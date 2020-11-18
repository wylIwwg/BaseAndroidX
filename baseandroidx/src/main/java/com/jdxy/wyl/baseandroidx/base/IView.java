package com.jdxy.wyl.baseandroidx.base;

import com.jdxy.wyl.baseandroidx.bean.BResult;
import com.jdxy.wyl.baseandroidx.bean.BResult2;

/**
 * Created by wyl on 2020/5/12.
 */
public interface IView {

    void showSuccess(String success);

    void showError(String error);

    void showInfo(String info);

    void release();

    void initData();

}
