package com.jdxy.wyl.baseandroidx.tools;

/**
 * 描述 用于正则
 * 作者 wyl
 * 日期 2021/2/3 14:23
 */
public interface ToolRegex {

    /*语音格式替换对象*/
    String regPatientName = "name";//用于替换姓名
    String regPatientNum = "line";//用于替换排队号
    String regPatientType = "type";//替换类型  出诊 复制 过号
    String regDeptName = "department";//科室名称
    String regClinicName = "room";//诊室名称
    String regDoctorName = "doctor";//医生名称
    String regClinicNum = "roNum";//诊室门牌号
    String regLeftParentheses = "(";//左括号
    String regRightParentheses = ")";//右括号

    /*用于替换数字 在需要将数字逐个念出*/
    String regNum0 = "0";
    String regNum1 = "1";
    String regNum2 = "2";
    String regNum3 = "3";
    String regNum4 = "4";
    String regNum5 = "5";
    String regNum6 = "6";
    String regNum7 = "7";
    String regNum8 = "8";
    String regNum9 = "9";

    String regCN0 = "零";
    String regCN1 = "一";
    String regCN2 = "二";
    String regCN3 = "三";
    String regCN4 = "四";
    String regCN5 = "五";
    String regCN6 = "六";
    String regCN7 = "七";
    String regCN8 = "八";
    String regCN9 = "九";
    String regCN10 = "十";
}
