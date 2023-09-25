package com.jdxy.wyl.baseandroidx.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.blankj.utilcode.util.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * 公共工具类
 * Created by wyl on 2019/9/2.
 */
public class ToolCommon {
    private static final String TAG = " ToolCommon ";

    /**
     * 获取view 的base64字符串
     *
     * @param view
     * @return
     */
    public static String getBitmapString(View view) {

        String bitmapString = "";
        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        if (bitmap != null) {
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap result = ScaleBitmap(copy, 0.5f, 0.5f);
            bitmapString = bitmapToBase64(result);

        } else {
            bitmapString = "";
        }
        view.setDrawingCacheEnabled(false);
        return bitmapString;

    }


    /**
     * 获取Bitmap base64字符串
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 保存view的视图到文件
     *
     * @param view
     * @return
     */
    public static File getBitmapFile(View view) {

        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();
        File file = new File(IConfigs.PATH_CAPTURE + "/capture.jpg");
        if (bitmap != null) {
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            ImageUtils.save(copy, file, Bitmap.CompressFormat.JPEG);
        }
        view.setDrawingCacheEnabled(false);

        return file;

    }


    /**
     * 缩放Bitmap
     *
     * @param bitmap
     * @param x
     * @param y
     * @return
     */
    private static Bitmap ScaleBitmap(Bitmap bitmap, float x, float y) {
        Matrix matrix = new Matrix();
        matrix.postScale(x, y); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 姓名特殊字符处理
     * name= 张小三
     * start =1      start=1
     * end =2        end =3
     * return  张*三   张**
     * 需要判断索引
     *
     * @param name  原名字
     * @param ch    特殊字符 一般为星号 *
     * @param start 开始索引 （包含）
     * @param end   结束索引 （不包含）
     * @return 如果字符串本身包含特殊字符 直接返回不处理
     */
    public static String SplitStarName(String name, String ch, int start, int end) {

        try {
            if (TextUtils.isEmpty(name) || end > name.length() || start >= end || name.contains(ch))
                return name;
            String result = "";
            String c = "";
            for (int i = 0; i < end - start; i++) {
                c += ch;
            }
            if (name.length() > 1)
                result = name.replaceFirst(name.substring(start, end), c);
            else result = name;
            return result;
        } catch (Exception ex) {
            ToolLog.efile(TAG, "[SplitStarName]" + ex.toString());
            return name;
        }

    }

    /**
     * 默认处理第二个字符为星号
     *
     * @param name
     * @return
     */
    public static String SplitStarName(String name) {

        if (TextUtils.isEmpty(name) || name.length() < 2 || name.contains("*"))
            return name;
        //return name.replaceFirst(name.substring(1, 2), "*");
        return SplitStarName(name, "*", 1, 2);
    }
}
