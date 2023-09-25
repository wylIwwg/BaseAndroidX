package com.jdxy.wyl.baseandroidx.tools;

import android.text.TextUtils;

import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.bean.BPushData;
import com.jdxy.wyl.baseandroidx.bean.Dept;
import com.jdxy.wyl.baseandroidx.bean.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolData {


    /**
     * 综合屏测试数据
     *
     * @return
     */
    public static Map<String, BPushData> testSynthesisData() {

        Map<String, BPushData> mapData = new HashMap<>();

        List<Patient> mPatients = new ArrayList<>();
        Dept mDepart = new Dept();
        mDepart.setDepartName("测试科室");
        mDepart.setClinicName("这是诊室");

        for (int j = 0; j < 15; j++) {
            Patient p = new Patient();
            p.setPatientNum("" + j);
            p.setPatientName("测试测");
            p.setState("" + j);
            mPatients.add(p);
        }
        for (int j = 1; j <= 6; j++) {
            BPushData pd = new BPushData();
            pd.setPassedList(mPatients);
            pd.setWaitingList(mPatients);
            pd.setCurrents(mPatients);
            pd.setClinicName("彩超室一" + j);
            pd.setPatientNum("" + j);
            pd.setPatientName("测试测");
            pd.setDoctorName("李明亮");
            pd.setDepartmentName("消化内科");
            pd.setCount("" + j);
            pd.setState("" + j);
            pd.setDocid(j + "");
            mapData.put(pd.getDocid(), pd);

        }
        return mapData;
    }

    /**
     * 显示患者姓名 是否隐私处理
     *
     * @param name 原姓名
     * @return
     */
    public static String showPatientName(String name) {
        return ToolToggle.showPatientFullName ? name : ToolCommon.SplitStarName(name);
    }

    /**
     * 显示患者排队号
     *
     * @param num 原排队号
     * @return
     */
    public static String showPatientNum(String num) {
        return TextUtils.isEmpty(num) ? "" : (num + "号");
    }

    /**
     * 根据state返回对应背景色
     *
     * @param state
     * @return
     */
    public static int state2Background(String state) {
        int txt = R.drawable.bg_state_chuzhen;
        switch (state) {
            case "0":
                txt = R.drawable.bg_state_chuzhen;
                break;
            case "1":
                txt = R.drawable.bg_state_fuzhen;
                break;
            case "2":
                txt = R.drawable.bg_state_passed;
                break;
            case "3":
                txt = R.drawable.bg_state_yuyue;
                break;
            case "4":
                txt = R.drawable.bg_state_chuzhen;
                break;
            case "5":
                txt = R.drawable.bg_state_zhuanzhen;
                break;
            case "6":
                txt = R.drawable.bg_state_old;
                break;
            case "7":
                txt = R.drawable.bg_state_junren;
                break;
            case "8":
                txt = R.drawable.bg_state_child;
                break;
            case "9":
                txt = R.drawable.bg_state_canji;
                break;
            case "10":
                txt = R.drawable.bg_state_jizhen;
                break;
            case "11":
                txt = R.drawable.bg_state_vip;
                break;
            case "12":
                txt = R.drawable.bg_state_green;
                break;
            default:
                txt = 0;
        }
        return txt;
    }

    /**
     * 根据state状态显示患者汉字状态
     * 0初诊// 1复诊// 2	过号// 3预约// 4	回呼// 5转诊// 6	老人// 7军人// 8	儿童// 9残障// 10急诊// 11VIP// 12绿通
     *
     * @param state 原状态
     * @return
     */
    public static String state2Text(String state) {
        String txt = "";
        switch (state) {
            case "0":
                txt = "初诊";
                break;
            case "1":
                txt = "复诊";
                break;
            case "2":
                txt = "过号";
                break;
            case "3":
                txt = "预约";
                break;
            case "4":
                txt = "回呼";
                break;
            case "5":
                txt = "转诊";
                break;
            case "6":
                txt = "老人";
                break;
            case "7":
                txt = "军人";
                break;
            case "8":
                txt = "儿童";
                break;
            case "9":
                txt = "残障";
                break;
            case "10":
                txt = "急诊";
                break;
            case "11":
                txt = "VIP";
                break;
            case "12":
                txt = "绿通";
                break;
            default:
                txt = "";
        }
        return txt;
    }
}
