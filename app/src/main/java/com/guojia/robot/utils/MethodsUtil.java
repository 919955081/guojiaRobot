package com.guojia.robot.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.guojia.robot.application.GJApplication;
import com.guojia.robot.floatwindow.FloatWindowManager;
import com.guojia.robot.iflytek.ApkInstaller;
import com.guojia.robot.iflytek.FucUtil;
import com.guojia.robot.permission.FloatPermission;
import com.iflytek.cloud.SpeechUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by YL on 2017/10/16.
 */

public class MethodsUtil {
    private static LogUtil log = new LogUtil("MethodsUtil", LogUtil.LogLevel.V);
    /** "[，,。.？?！!]" */
    private static String symbolStr = "[，,。.？?！! ]";

    /** 获取当前Activity名称 */
    public static String getActivityName(Context context) {
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }

    /** 判断是否包含第*个,并获取，数字 >=0 代表包含 */
    public static int getItemNumber(String str) {
        if (str.length()>2 && str.contains("第") && str.split("第")[1].contains("个")) {
            String numStr = (str.split("第")[1]).split("个")[0];
            return chNumber2Int(numStr);
        } else {
            return -1;
        }
    }

    /** 中文数字转阿拉伯数字 */
    public static int chNumber2Int(String chineseNumber) {
        if(TextUtils.isEmpty(chineseNumber)){
            return -1;
        }
        int result = 0;
        int temp = -1;// 存放一个单位的数字如：十万
        boolean first = false;
        if (isNumeric(chineseNumber)) {
            result = Integer.valueOf(chineseNumber);
        } else {
            int count = 0;// 判断是否有chArr
            char[] cnArr = new char[] { '一', '二', '三', '四', '五', '六', '七', '八', '九' };
            char[] chArr = new char[] { '十', '百', '千', '万', '亿' };
            for (int i = 0; i < chineseNumber.length(); i++) {
                boolean b = true;// 判断是否是chArr
                char c = chineseNumber.charAt(i);
                for (int j = 0; j < cnArr.length; j++) {// 非单位，即数字
                    if (c == cnArr[j]) {
                        if (0 != count) {// 添加下一个单位之前，先把上一个单位值添加到结果中
                            result += temp;
                            temp = 1;
                            count = 0;
                        }
                        // 下标+1，就是对应的值
                        temp = j + 1;
                        b = false;
                        first = true;
                        break;
                    }
                }
                if (b) {// 单位{'十','百','千','万','亿'}
                    for (int j = 0; j < chArr.length; j++) {
                        if (c == chArr[j]) {
                            switch (j) {
                                case 0:
                                    if(!first){
                                        temp = 1;
                                    }
                                    temp *= 10;
                                    break;
                                case 1:
                                    temp *= 100;
                                    break;
                                case 2:
                                    temp *= 1000;
                                    break;
                                case 3:
                                    temp *= 10000;
                                    break;
                                case 4:
                                    temp *= 100000000;
                                    break;
                                default:
                                    break;
                            }
                            count++;
                        }
                    }
                }
                if (i == chineseNumber.length() - 1) {// 遍历到最后一个字符
                    result += temp;
                }
            }
        }
        return result;
    }

    /** 判断str是否全为数字 */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    /** 判断str是否为数字英文 */
    public static boolean isNumEn(String str) {
        Pattern pattern = Pattern.compile("^[a-z0-9A-Z]+$");
        Matcher isNumEn = pattern.matcher(str);
        if (isNumEn.matches()) {
            return true;
        }
        return false;
    }

    /** 判断str是否为arrayStr数组里的字符串 */
    public static boolean isEquals(String str, String[] arrayStr) {
        for (int i = 0; i < arrayStr.length; i++) {
            if (str.equals(arrayStr[i])) {
                return true;
            }
        }
        return false;
    }
    /** 判断str是否为arrayStr数组里的字符串,去语气符号 */
    public static boolean isEqualsLike(String str, String[] arrayStr) {
        for (int i = 0; i < arrayStr.length; i++) {
            if (str.replaceAll(symbolStr, "").equals(arrayStr[i])) {
                return true;
            }
        }
        return false;
    }

    /** 删掉arrayStr包含的字符串 */
    public static String replace(String str, String[] arrayStr) {
        String strRes = str;
        for (int i = 0; i < arrayStr.length; i++) {
            strRes = strRes.replaceAll(arrayStr[i], "");
        }
        return strRes.replaceAll(symbolStr, "");
    }

    /** 规则rule.* */
    public static boolean isMatcher(String str, String rule){
        Pattern p = Pattern.compile(rule+"(.*)");
        Matcher m = p.matcher(str.replace(" ", ""));
        return m.find();
    }
    /** 规则rule.* */
    public static String getMatcher(String str, String rule){
        Pattern p = Pattern.compile(rule+"(.*)");
        Matcher m = p.matcher(str.replace(" ", ""));
        if(m.find()){
            return m.group(1).replaceAll(symbolStr, "");
        }else{
            return "";
        }
    }

    public static String string2wifipwd(String str) {
        String returnStr = "";
        String pwdStr = str.replaceAll(symbolStr, "");
        for (int i = 0; i < pwdStr.length(); i++){
            String text = pwdStr.substring(i,i+1);
            if (isNumEn(text)) {
                returnStr += text;
            }else{
                String[] numStr = new String[] {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
                for (int j = 0; j < numStr.length; j++) {
                    if (text.equals(numStr[j])) {
                        returnStr += j;
                    }
                }
            }
        }
        return returnStr;
    }

    /** dp转px */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /** px转dp */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
