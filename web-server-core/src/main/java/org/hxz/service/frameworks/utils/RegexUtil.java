package org.hxz.service.frameworks.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by someone on 2017-02-23.
 *
 * 正则式工具类
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RegexUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Pattern PATTERN_HTTP_URL             = Pattern.compile("\\b(([hH])([tT])([tT])([pP])s?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]");
    private static final Pattern PATTERN_IP                   = Pattern.compile("^(?=\\d+\\.\\d+\\.\\d+\\.\\d+$)(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.?){4}$");
    private static final Pattern PATTERN_EMAIL                = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
    private static final Pattern PATTERN_MD5                  = Pattern.compile("^[a-fA-F0-9]{32}$");
    private static final Pattern PATTERN_MOBILE_PHONE         = Pattern.compile("\\+?(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)?\\d{1,14}$");
    private static final Pattern PATTERN_VIETNAM_MOBILE_PHONE = Pattern.compile("(03[0-9]|07[0-9]|08[0-9]|09[0-9]|01[2689])[0-9]{7}");
    private static final Pattern PATTERN_PURE_NUMBER          = Pattern.compile("^\\d*$");
    private static final Pattern PATTERN_MONTH_yyyy_MM        = Pattern.compile("^(19[0-9]{2}|2[0-9]{3})-(0[1-9]|1[012])$");
    private static final Pattern PATTERN_NAME                 = Pattern.compile("^[a-zA-Z0-9_\u4e00-\u9fa5]+$");
    private static final Pattern PATTERN_NAME_WITH_SPACE      = Pattern.compile("^[a-zA-Z0-9_\u4e00-\u9fa5\u0020]+$");
    private static final Pattern PATTERN_VERSION              = Pattern.compile("v[0-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,4}");
    private static final Pattern PATTERN_ID_CARD              = Pattern.compile("(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)"); //定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）

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
     * 字符串input是否是http或https类型的url
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isHttpURL(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_HTTP_URL.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是IP地址
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isIPAddress(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_IP.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是邮箱
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isEmail(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_EMAIL.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isMD5(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_MD5.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是手机号码
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isMobilePhoneNumber(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_MOBILE_PHONE.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是越南手机号码
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isVietnamMobilePhoneNumber(String pInput) {
        /*
            Before September 15 2018, Vietnam has phone number start with 09*, 01(2|6|8|9).
            After that, the phone number can start with 03, 07 or 08.
        */
        if(StringUtil.isEmpty(pInput)){
            return false;
        }
        Matcher matcher = PATTERN_VIETNAM_MOBILE_PHONE.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是纯数字字符串
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isPureNumberString(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_PURE_NUMBER.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串input是否是yyyy-MM格式的月份
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isMonth_yyyy_MM(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_MONTH_yyyy_MM.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串是否是 汉字数字字母_
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isName(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_NAME.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串是否是 汉字数字字母_空格
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isNameWithSpace(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_NAME_WITH_SPACE.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串是否是标准版本名称 v11.222.3333
     *
     * @param pInput 版本名称
     * @return 是否符合
     */
    public static boolean isVersion(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_VERSION.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 字符串是账户名形式，仅包含数字、字母或者是邮箱格式
     *
     * @param pInput 字符串
     * @return 是否符合
     */
    public static boolean isAccount(String pInput, int pMin, int pMax) {
        if (pInput != null) {
            int length = pInput.length();
            if (length < pMin || length > pMax) {
                return false;
            }
            Matcher matcher = Pattern.compile("^[a-zA-Z0-9.@_\\-]+$").matcher(pInput);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 身份证校验
     *
     * @param pInput 身份证
     * @return 是否符合
     */
    public static boolean isIDCard(String pInput) {
        if (StringUtil.isEmpty(pInput)) {
            return false;
        }
        Matcher matcher = PATTERN_ID_CARD.matcher(pInput);
        boolean matches = matcher.matches();
        // 判断第18位校验值
        if (matches) {
            if (pInput.length() == 18) {
                try {
                    char[] charArray = pInput.toCharArray();
                    // 前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    // 这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int      sum     = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count   = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int  idCardMod  = sum % 11;
                    if (idCardY[idCardMod].toUpperCase()
                            .equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase()
                                + "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(isAccount("test哈哈sssss", 4, 20));
        System.out.println(isAccount("testsssss", 4, 20));
        System.out.println(isAccount("test ssss", 4, 20));
        System.out.println(isAccount(" test哈sssss哈", 4, 20));
        System.out.println(isAccount(" test哈sss", 4, 20));
        System.out.println(isAccount("12a", 4, 20));
        System.out.println(isAccount("timememetest", 4, 20));
        System.out.println(isAccount("timememetest12312312123", 4, 20));
        System.out.println(isAccount("xi.qin@genlot.com", 4, 20));
        System.out.println(isVietnamMobilePhoneNumber("0912345678"));
        System.out.println(isVietnamMobilePhoneNumber("0912419333"));
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
