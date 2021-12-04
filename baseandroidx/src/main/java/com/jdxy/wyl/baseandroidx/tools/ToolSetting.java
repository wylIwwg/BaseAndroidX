package com.jdxy.wyl.baseandroidx.tools;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.jdxy.wyl.baseandroidx.bean.BBaseSetting;

import java.nio.charset.Charset;

public class ToolSetting {
    public static String TAG = " ToolSetting ";
    public BBaseSetting mSetting;

    static ToolSetting mToolSetting = new ToolSetting();

    public static ToolSetting Instance() {
        return mToolSetting;
    }

    public void InitSetting(String pathSetting) {
        if (FileUtils.isFileExists(pathSetting)) {
            //存在 则直接取值
            ToolSetting.Instance().InitSetting(pathSetting);
            String content = FileIOUtils.readFile2String(pathSetting, Charset.forName("utf-8").name());
            ToolLog.efile(TAG, "配置文件内容：" + content);
            mSetting = JSON.parseObject(content, BBaseSetting.class);
        } else {
            ToolLog.efile(TAG, "配置文件不存：" + pathSetting);
            mSetting = new BBaseSetting();
        }
    }
}
