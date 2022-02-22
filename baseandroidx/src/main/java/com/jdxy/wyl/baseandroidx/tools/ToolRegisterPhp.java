package com.jdxy.wyl.baseandroidx.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.jdxy.wyl.baseandroidx.bean.BRegister;
import com.jdxy.wyl.baseandroidx.bean.BRegisterResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;

public class ToolRegisterPhp {


    /**
     * Created by wyl on 2019/3/29.
     */


    //public static String publicKey = Configs.PublicKey;
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCA3aNm/ra4JgBYZzABSy5TAepEuSCE2x8OqBwtCAXITv/Ei0cn/l09ot4kIYXHZQ9hZ+B1W558AGAxfHZkrYZNj1Hn53AXVqw4/ojeP3RyfcXo8GJom/F9+1kd56NqEm/iv2ETB3vcrGwFJOldy7lMEXfHDtFQaj+i2U+eVfckUQIDAQAB";
    private static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIDdo2b+trgmAFhnMAFLLlMB6kS5IITbHw6oHC0IBchO/8SLRyf+XT2i3iQhhcdlD2Fn4HVbnnwAYDF8dmSthk2PUefncBdWrDj+iN4/dHJ9xejwYmib8X37WR3no2oSb+K/YRMHe9ysbAUk6V3LuUwRd8cO0VBqP6LZT55V9yRRAgMBAAECgYBq5vKp+33az/OTYq6pNBQO2lTcg/MdI6XlA8Kz/KbHX/m/s4bo/5OcESNVN9YB7q1Osdy7nrCfz7P8+XJB3M2/FFTm9iHsu24P6P1IxvjV05jir8g7ycs4RoZuxk2+2Ln8Kd6H+0M7qL528os5YRWu0PUhMOR1y103pqWT20UTkQJBAMwFoJ9xvcaH558Iqy/rPpre/25SyfnZbq/KrI2tv18ItfW5flXq3dfVrOYy9i6zWPu9gL9npuPjY5wK3mhdpyUCQQChsk0iT1+uyFhdg0VJeV4+fjd6Fukq7NjqRUIWGXxHoondtLM4qe7rcWMkFXN0U0gZH3XE/KRqrEM20jsXMma9AkEAwoiHHCDe2+MQJiKk378F5bPFiFMmRLZfBP1SRJErzRjILzGcVZ3pw3f5MVHcTLEzom2Rym+xwM87VjlC0e6ihQJAdoE1lMK1bmR4lrhhbFLd5lEcmYb3BjWlWDTAFXBCLEIMZodLnmi0qKtmLIjoH8X1niv3ZRJ/8YokjKYRFpQixQJAXP70G3X9n/OaPR2uCATh2LGcaaLBHBiqKcLGxmkfFTlbl811nG+sW5gyWIhiv+3/JmCmKCTTRhWTjs/YjKdAeQ==";
    private static final String TAG = " ToolRegister2 ";
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/sjjd";

    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"  
    private static String transform = "RSA/NONE/PKCS1Padding";
    //private static String transform = "RSA";
    private static int base64Mode = Base64.DEFAULT;
    private byte[] mPublicBytes = Base64.decode(publicKey, base64Mode);
    private byte[] mPrivateBytes = Base64.decode(privateKey, base64Mode);

    private static ToolRegisterPhp instance;
    private static Context mContext;
    private BRegister mRegister;

    public BRegister getRegister() {
        return mRegister;
    }

    private ToolRegisterPhp(Context context, String privateKey, String publicKey) {
        if (mContext == null) mContext = context;
        if (privateKey != null) {
            mPrivateBytes = Base64.decode(privateKey, base64Mode);
        }
        if (publicKey != null) {
            mPublicBytes = Base64.decode(publicKey, base64Mode);
        }
    }

    public static ToolRegisterPhp Instance(Context context) {

        return Instance(context, null, null);

    }

    public static ToolRegisterPhp Instance(Context context, String privateKey, String publicKey) {
        if (mContext == null) mContext = context;
        if (instance == null) {
            instance = new ToolRegisterPhp(context, privateKey, publicKey);
        }
        return instance;
    }

    /**
     * 封装注册信息 转为base64字符串
     * 公钥加密
     *
     * @param just64 是否只是转为base64
     * @return
     */
    public String register2Base64(boolean just64, String mark) {

        String mac = ToolDevice.getMac();
        BRegister r = new BRegister();
        r.setMac(mac);
        r.setMessage(mark);
        String sb = JSON.toJSONString(r);

        byte[] mDataBytes = sb.getBytes();
        String result;
        byte[] mEncode;
        if (just64) {
            mEncode = Base64.encode(mDataBytes, base64Mode);
            result = new String(mEncode);
        } else {
            mEncode = ToolEncrypt.encryptRSA(mDataBytes, mPublicBytes, true, transform);
            result = Base64.encodeToString(mEncode, base64Mode);
        }

        ToolLog.e(TAG, "BRegister: 加密后的数据： " + result);
        return result;

    }


    /**
     * //设备注册成功，写入本地
     *
     * @param data 加密数据
     * @return 是否写入成功
     */
    public boolean registerDevice(String data) {
        try {
            if (data == null || data.equals(""))
                return false;
            String result = str2Register(data, false);//解密数据
            ToolLog.efile(TAG, "registerDevice: 允许注册： " + result);
            if (result != null) {
                mRegister = JSON.parseObject(result, BRegister.class);
                if (mRegister != null) {
                    return writeDevice(data);//直接写入
                }
                return false;
            }
        } catch (Exception e) {
            ToolLog.efile(TAG, "registerDevice: 允许注册error： " + e.toString());
            e.printStackTrace();
            return false;
        }
        return false;

    }

    /**
     * //设备注册成功，写入本地
     *
     * @return
     */
    public boolean registerDevice(BRegister result) {
        try {
            ToolLog.e(TAG, "registerDevice: 允许注册： " + result);
            if (result != null) {
                String mLimit = result.getRegisterDays();
                if (mLimit != null && mLimit.length() > 0) {
                    int mParseInt = Integer.parseInt(mLimit);
                    if (mParseInt == 0) {//不允许注册
                        return false;
                    } else {
                        return writeDevice(JSON.toJSONString(result));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    /**
     * //设备注册成功，写入本地
     *
     * @param data 加密数据
     * @return
     */
    public boolean writeDevice(String data) {
        try {
            if (data == null || data.equals(""))
                return false;
            File mFile = new File(PATH);
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFile, false), Charset.forName("UTF-8")));
                bw.write(data);//写入密文数据
                bw.flush();
                bw.close();
                ToolLog.e(TAG, "writeDevice: ： ");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 获取注册数据  在 PATH 目录/文件
     *
     * @return
     */
    private BRegister getRegisterText() {

        try {
            File mFile = new File(PATH);
            if (mFile.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mFile), Charset.forName("UTF-8")));
                String data = br.readLine();
                br.close();
                if (data != null && data.length() > 0) {

                    String result = str2Register(data, false);//解密获取明文数据json
                    BRegister register = JSON.parseObject(result, BRegister.class);//将数据转成对象
                    return register;//将数据转成对象
                }
            }//文件不存在 表示未注册

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    /**
     * PHP   1 未注册  2 注册  3 过期
     * JAVA  0 未注册  1 注册  2 过期
     * 哎 就是不统一 能怎么办？？
     *
     * @return
     */
    public BRegisterResult checkDeviceRegisteredPhp() {
        BRegisterResult mResult = new BRegisterResult();
        mResult.setRegisterCode(1);
        mResult.setRegistered(false);
        try {
            this.mRegister = this.getRegisterText();
            if (this.mRegister != null) {
                String mac = ToolDevice.getMac();
                //未获取到mac
                if (mac == null || mac.equals("02:00:00:00:00:00")) {
                    //  Toasty.icon_error(mContext, "MAC获取不正确：" + mac, 1, true).show();
                    mResult.setRegisterCode(1);
                    mResult.setRegistered(false);
                    mResult.setRegisterStr(this.register2Base64(false, ToolSP.getDIYString("app_type")));
                    return mResult;
                }

                if (this.mRegister.getMac().equals(mac)) {
                    String mLimit = this.mRegister.getRegisterDays();
                    if (mLimit != null && mLimit.length() > 0) {
                        int mInt = Integer.parseInt(mLimit);
                        if (mInt <= -1) {
                            //永久
                            mResult.setRegistered(true);
                            mResult.setRegisterCode(2);
                            mRegister.setResidue(2);
                        } else if (mInt > 0) {
                            mResult.setRegistered(true);
                            mResult.setRegisterCode(2);
                            long rt = Long.parseLong(this.mRegister.getRegisterDate());
                            long mMillis = System.currentTimeMillis();//采用系统时间  如果是亮钻的板子
                            //如果注册时间 + 注册天数 的总时间 小于当前时间 注册过期
                            Date newDate2 = new Date(rt + (long) mInt * 24L * 60L * 60L * 1000L);
                            long mL = newDate2.getTime() - mMillis;

                            long days = (mL / (24L * 60L * 60L * 1000L));
                            if (days > 0) {
                                //若还有剩余天数。更新到本地
                                int result = (int) days;
                                //若还有剩余天数。更新到本地
                                ToolLog.e(TAG, "还有剩余天数。更新到本地: " + days + "  " + result);
                                mRegister.setResidue(result);
                                // registerDevice(mRegister);
                            }
                            if (mL < 0) {
                                mResult.setRegisterCode(3);
                                mResult.setRegisterStr(this.register2Base64(false, ToolSP.getDIYString("app_type")));
                                mResult.setRegistered(false);
                            }
                        }
                        return mResult;
                    }
                }
                //mac值不一致 不允许注册

            } else {
                //未获取到注册信息
                mResult.setRegisterStr(this.register2Base64(false, ToolSP.getDIYString("app_type")));
                mResult.setRegisterCode(1);
                mResult.setRegistered(false);
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }
        return mResult;
    }


    /**
     * 将字符串解密为明文数据
     *
     * @param data
     * @return
     */
    public String str2Register(String data, boolean usePublic) {
        //解密
        try {
            ToolLog.efile(TAG, "str2Regsiter:源数据 " + data);
            byte[] mDataBytes = Base64.decode(data, base64Mode);
            byte[] mDecryptBytes = ToolEncrypt.decryptRSA(mDataBytes, usePublic ? mPublicBytes : mPrivateBytes, usePublic, transform);

            String b64 = Base64.encodeToString(mDecryptBytes, base64Mode);
            byte[] mEncode = Base64.decode(b64, base64Mode);
            String result = new String(mEncode);
            ToolLog.efile(TAG, "str2Regsiter: 解密后的数据： " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}
