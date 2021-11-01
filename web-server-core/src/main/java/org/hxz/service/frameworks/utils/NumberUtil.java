package org.hxz.service.frameworks.utils;

import java.text.NumberFormat;

/**
 * Created by someone on 2017-02-23.
 *
 * 数字工具类
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NumberUtil {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * String转换为long
     *
     * @param pNumber 数字字符串
     * @param pDefaultValue 出错时的默认值
     * @return 转化后的数字
     */
    public static long parseLong(String pNumber, int pDefaultValue) {
        long value;
        try {
            value = Long.parseLong(pNumber);
        } catch (Exception e) {
            value = pDefaultValue;
        }
        return value;
    }

    /**
     * String转换为int
     *
     * @param pNumber 数字字符串
     * @param pDefaultValue 出错时的默认值
     * @return 转化后的数字
     */
    public static int parseInt(String pNumber, int pDefaultValue) {
        int value;
        try {
            value = Integer.parseInt(pNumber);
        } catch (Exception e) {
            value = pDefaultValue;
        }
        return value;
    }

    /**
     * String转换为double
     *
     * @param pNumber 数字字符串
     * @param pDefaultValue 出错时的默认值
     * @return 转化后的数字
     */
    public static double parseDouble(String pNumber, double pDefaultValue) {
        double value;
        try {
            value = Double.parseDouble(pNumber);
        } catch (Exception e) {
            value = pDefaultValue;
        }
        return value;
    }

    /**
     * 返回指定位数小数位的数字字符串
     *
     * @param pNumber 需要格式化的数字
     * @param pDigits 小数位数
     * @return 转化后的数字字符串
     */
    public static String getDigitsFormatString(double pNumber, int pDigits) {
        return getDigitsFormatString(pNumber, pDigits, pDigits);
    }

    /**
     * 返回指定位数小数位的数字字符串
     *
     * @param pNumber    需要格式化的数字
     * @param pMinDigits 最小小数位数
     * @param pMaxDigits 最大小数位数
     * @return 转化后的数字字符串
     */
    public static String getDigitsFormatString(double pNumber, int pMinDigits, int pMaxDigits) {
        NumberFormat NF = NumberFormat.getInstance();
        NF.setMaximumFractionDigits(pMaxDigits);
        NF.setMinimumFractionDigits(pMinDigits);
        return NF.format(pNumber);
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
