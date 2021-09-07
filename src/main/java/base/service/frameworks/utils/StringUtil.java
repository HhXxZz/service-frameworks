package base.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by someone on 2017-02-23.
 *
 * 字符串工具类
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class StringUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(StringUtil.class);

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
     * 获取字符串长度
     *
     * @param pString 字符串
     * @return 长度
     */
    public static int length(String pString) {
        if (isEmpty(pString)) {
            return 0;
        }
        return pString.length();
    }

    /**
     * 判断字符串是否为空
     *
     * @param pString 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String pString) {
        return pString == null || "".equals(pString);
    }

    /**
     * 字符集转换
     *
     * @param pString 字符串
     * @param pFrom   原编码
     * @param pTo     转化编码
     * @return 转化结果
     */
    public static String iconv(String pString, String pFrom, String pTo) {
        pString = pString == null ? "" : pString;
        if (pString.length() > 0) {
            try {
                return new String(pString.getBytes(pFrom), pTo);
            } catch (Exception e) {
                e.printStackTrace();
                return pString;
            }
        }
        return pString;
    }

    /**
     * 将ISO-8859-1转换为GBK
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String ISOtoGBK(String pString) {
        return iconv(pString, "ISO-8859-1", "GBK");
    }

    /**
     * 将GBK转换为ISO-8859-1
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String GBKtoISO(String pString) {
        return iconv(pString, "GBK", "ISO-8859-1");
    }

    /**
     * 将ISO-8859-1转换为UTF-8
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String ISOtoUTF(String pString) {
        return iconv(pString, "ISO-8859-1", "UTF-8");
    }

    /**
     * 将UTF-8转换为ISO-8859-1
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String UTFtoISO(String pString) {
        return iconv(pString, "UTF-8", "ISO-8859-1");
    }

    /**
     * 将GBK字符串转换为UTF-8
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String GBKtoUTF(String pString) {
        String l_temp = GBKToUnicode(pString);
        l_temp = UnicodeToUTF(l_temp);

        return l_temp;
    }

    /**
     * 将UTF-8字符串转换为GBK
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String UTFtoGBK(String pString) {
        String l_temp = UTFtoUnicode(pString);
        l_temp = UnicodeToGBK(l_temp);

        return l_temp;
    }

    /**
     * 将GBK字符串转换为Unicode
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String GBKToUnicode(String pString) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pString.length(); i++) {
            char chr1 = pString.charAt(i);

            if (!isNeedConvert(chr1)) {
                result.append(chr1);
                continue;
            }

            result.append("\\u").append(Integer.toHexString(chr1));
        }

        return result.toString();
    }

    /**
     * 将Unicode字符串转换为GBK
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String UnicodeToGBK(String pString) {
        int           index  = 0;
        StringBuilder buffer = new StringBuilder();

        int li_len = pString.length();
        while (index < li_len) {
            if (index >= li_len - 1 || !"\\u".equals(pString.substring(index, index + 2))) {
                buffer.append(pString.charAt(index));

                index++;
                continue;
            }

            String charStr = pString.substring(index + 2, index + 6);

            char letter = (char) Integer.parseInt(charStr, 16);

            buffer.append(letter);
            index += 6;
        }

        return buffer.toString();
    }

    private static boolean isNeedConvert(char para) {
        return ((para & (0x00FF)) != para);
    }

    /**
     * UTF-8 转 Unicode
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String UTFtoUnicode(String pString) {
        char[] myBuffer = pString.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pString.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short  s       = (short) myBuffer[i];
                String hexS    = Integer.toHexString(s);
                String unicode = "\\u" + (hexS.length() > 4 ? hexS.substring(4) : hexS);
                sb.append(unicode.toLowerCase(Locale.getDefault()));
            }
        }
        return sb.toString();
    }

    /**
     * Unicode 转 UTF-8
     *
     * @param pString 字符串
     * @return 转化结果
     */
    public static String UnicodeToUTF(String pString) {
        char          aChar;
        int           len       = pString.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len; ) {
            aChar = pString.charAt(x++);
            if (aChar == '\\') {
                aChar = pString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = pString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 获取Unicode编码
     *
     * @param pString 字符串
     * @return 编码结果
     */
    public static String toUnicode(String pString) {
        StringBuilder sb = new StringBuilder();
        if (pString == null || "".equals(pString)) return "";
        for (int i = 0; i < pString.length(); i++) {
            if ((pString.charAt(i) >= 0x4e00) && (pString.charAt(i) <= 0x9fbb)) {
                sb.append("\\u").append(Integer.toHexString(pString.charAt(i)));
            } else {
                sb.append(pString.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param pLength 字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int pLength) { // length表示生成字符串的长度
        String        base   = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
        Random        random = new Random();
        StringBuilder sb     = new StringBuilder();
        for (int i = 0; i < pLength; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成22位长度BASE64SafeString
     * 根据UUID生成，存在一定重复几率
     * 可以用来批量生成兑换码
     *
     * @return 22位字符串
     */
    public static String generateBase64SafeFromUUID() {
        UUID       uuid = UUID.randomUUID();
        ByteBuffer bb   = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        String result = EncryptUtil.base64_encode(bb.array());

        Pattern p = Pattern.compile("[A-Za-z0-9]{22}");
        Matcher m = p.matcher(result);

        if (!m.find()) {
            return generateBase64SafeFromUUID();
        }
        return result.toUpperCase(Locale.getDefault());
    }

    /**
     * 生成UUID，过滤-符号，可以用来做唯一KEY，重复几率甚微
     *
     * @return UUID
     */
    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * <br/> 通用签名计算
     * <br/> 按照 (pHeadKey@notNull)+pParams(key=value join)+(pTailKey@notNull) 拼接字符串，+为pSeparator
     * <br/> 拼接字符串以MD5 小写形式输出
     * @param pParams    参数 TreeMap 会自动按ASCII码排序
     * @param pSeparator 连接符
     * @param pHeadKey   参数字符串头部KEY
     * @param pTailKey   参数字符串尾部KEY
     * @return 小写MD5签名
     */
    public static String makeSignCommon(TreeMap<String, String> pParams, String pSeparator, String pHeadKey, String pTailKey, boolean pPrintString){
        StringBuilder s = new StringBuilder();
        if(!isEmpty(pHeadKey)){
            s.append(pHeadKey);
        }
        for(String key : pParams.keySet()){
            if(isEmpty(pParams.get(key))){
                continue;
            }
            if(s.length() > 0){
                s.append(pSeparator);
            }
            s.append(key).append("=").append(pParams.get(key));
        }
        if(!isEmpty(pTailKey)){
            s.append(pSeparator).append(pTailKey);
        }

        if(pPrintString){
            LOG.debug("Sign String [{}]", s.toString());
        }

        return MD5.getMD5(s.toString());
    }
    public static String makeSignCommon(TreeMap<String, String> pParams, String pSeparator, String pHeadKey, String pTailKey){
        return makeSignCommon(pParams, pSeparator, pHeadKey, pTailKey, false);
    }

    /**
     * 获取XML中某个节点中的内容
     * @param pXML XML文本
     * @param pNode 节点TAG
     * @return 该节点中的内容
     */
    public static String getXMLValue(String pXML, String pNode) {
        if (pXML != null && pNode != null){
            Pattern pattern = Pattern.compile("<" + pNode + "><!\\[CDATA\\[(.*?)]]></" + pNode + ">");
            Matcher matcher = pattern.matcher(pXML);
            if (matcher.find()) {
                return matcher.group(1);
            }else{
                pattern = Pattern.compile("<" + pNode + ">(.*?)</" + pNode + ">");
                matcher = pattern.matcher(pXML);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return "";
    }


    /**
     * 获取XML中某个节点中的内容
     * @param pXML XML文本
     * @param pNode 节点TAG
     * @return 该节点中的内容
     */
    public static List<String> getXMLValues(String pXML, String pNode) {
        ArrayList<String> resList = new ArrayList<>();
        if (pXML != null && pNode != null){
            Pattern           pattern  = Pattern.compile("<" + pNode + "><!\\[CDATA\\[(.*?)]]></" + pNode + ">");
            Matcher           matcher  = pattern.matcher(pXML);
            while (matcher.find()){
                resList.add(matcher.group(1));
            }
        }
        return resList;
    }

    /**
     * 使用指定正则式匹配字符串，获取第一个匹配组内容
     *
     * @param pRegex 正则式，需要包含组，否则返回空字符
     * @param pOrigin 字符串
     * @return 匹配成功时的第一组内容
     */
    public static String getFirstMatchGroup(String pRegex, String pOrigin){
        if (!isEmpty(pRegex) && !isEmpty(pOrigin)){
            Pattern pattern = Pattern.compile(pRegex);
            Matcher matcher = pattern.matcher(pOrigin);
            if (matcher.find() && matcher.groupCount() > 0) {
                return matcher.group(1);
            }
        }
        return "";
    }

    /**
     * 将为null的字符串处理为""
     * @param pValue 处理的值
     * @return 处理之后的值
     */
    public static String formatString(String pValue){
        if(isEmpty(pValue)){
            return "";
        }
        return pValue;
    }

    public static void main(String[] args){
//        System.out.println(getFirstMatchGroup("/user/attr/(.+)/history", "/user/attr/exp/history"));
//        System.out.println(getFirstMatchGroup("/user/attr/.+/history", "/user/attr/history"));
//
//        System.out.println(generateUUID());
//        System.out.println(generateUUID());

        String str = "鎿嶄綔鎴愬姛";
        String[] charset = {"ISO8859-1", "GBK", "UTF-8"};

        for(int x = 0; x < charset.length; x++){
            for(int y = 0; y < charset.length; y++){
                String temp = iconv(str, charset[x], charset[y]);
                System.out.println(charset[x] + " -> " + charset[y] + " = \"" + temp + "\"");

            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
