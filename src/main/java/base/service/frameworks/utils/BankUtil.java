package base.service.frameworks.utils;

import com.google.common.collect.ImmutableMap;
import okhttp3.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by someone on 2020/6/17 16:44.
 * <pre>
 * 银行卡号可以通过Luhn算法来验证。
 * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
 * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，则将其减去9），再求和。
 * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
 *
 * 银行卡是由"发卡行标识代码 + 自定义 + 校验码"等部分组成的BIN号
 * 银联标准卡与以往发行的银行卡最直接的区别就是其卡号前6位数字的不同
 * 银行卡卡号的前6位是用来表示发卡银行或机构的，称为"发卡行识别码"(Bank Identification Number，缩写为"BIN")。
 * 银联标准卡是由国内各家商业银行(含邮储、信用社)共同发行、符合银联业务规范和技术标准、卡正面右下角带有"银联"标识
 * 目前，新发行的银联标准卡一定带有国际化的银联新标识，新发的非银联标准卡使用旧的联网通用银联标识
 * 卡号前6位为622126至622925之一的银行卡，是中国银行卡产业共有的民族品牌。
 * </pre>
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class BankUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final ImmutableMap<String, String> BANK_CARD_TYPE;
    public static final ImmutableMap<String, String> BANK_NAME;
    static{
        Map<String, String> types = new HashMap<>();
        types.put("CC", "信用卡");
        types.put("DC", "储蓄卡");
        BANK_CARD_TYPE = ImmutableMap.copyOf(types);

        Map<String, String> names = new HashMap<>();
        names.put("ABC", "中国农业银行");
        names.put("ARCU", "安徽省农村信用社");
        names.put("ASCB", "鞍山银行");
        names.put("AYCB", "安阳银行");
        names.put("BANKWF", "潍坊银行");
        names.put("BGB", "广西北部湾银行");
        names.put("BHB", "河北银行");
        names.put("BJBANK", "北京银行");
        names.put("BJRCB", "北京农村商业银行");
        names.put("BOC", "中国银行");
        names.put("BOCD", "承德银行");
        names.put("BOCY", "朝阳银行");
        names.put("BOD", "东莞银行");
        names.put("BODD", "丹东银行");
        names.put("BOHAIB", "渤海银行");
        names.put("BOJZ", "锦州银行");
        names.put("BOP", "平顶山银行");
        names.put("BOQH", "青海银行");
        names.put("BOSZ", "苏州银行");
        names.put("BOYK", "营口银行");
        names.put("BOZK", "周口银行");
        names.put("BSB", "包商银行");
        names.put("BZMD", "驻马店银行");
        names.put("CBBQS", "城市商业银行资金清算中心");
        names.put("CBKF", "开封市商业银行");
        names.put("CCB", "中国建设银行");
        names.put("CCQTGB", "重庆三峡银行");
        names.put("CDB", "国家开发银行");
        names.put("CDCB", "成都银行");
        names.put("CDRCB", "成都农商银行");
        names.put("CEB", "中国光大银行");
        names.put("CGNB", "南充市商业银行");
        names.put("CIB", "兴业银行");
        names.put("CITIC", "中信银行");
        names.put("CMB", "招商银行");
        names.put("CMBC", "中国民生银行");
        names.put("COMM", "交通银行");
        names.put("CQBANK", "重庆银行");
        names.put("CRCBANK", "重庆农村商业银行");
        names.put("CSCB", "长沙银行");
        names.put("CSRCB", "常熟农村商业银行");
        names.put("CZBANK", "浙商银行");
        names.put("CZCB", "浙江稠州商业银行");
        names.put("CZRCB", "常州农村信用联社");
        names.put("DAQINGB", "龙江银行");
        names.put("DLB", "大连银行");
        names.put("DRCBCL", "东莞农村商业银行");
        names.put("DYCB", "德阳商业银行");
        names.put("DYCCB", "东营市商业银行");
        names.put("DZBANK", "德州银行");
        names.put("EGBANK", "恒丰银行");
        names.put("FDB", "富滇银行");
        names.put("FJHXBC", "福建海峡银行");
        names.put("FJNX", "福建省农村信用社联合社");
        names.put("FSCB", "抚顺银行");
        names.put("FXCB", "阜新银行");
        names.put("GCB", "广州银行");
        names.put("GDB", "广东发展银行");
        names.put("GDRCC", "广东省农村信用社联合社");
        names.put("GLBANK", "桂林银行");
        names.put("GRCB", "广州农商银行");
        names.put("GSRCU", "甘肃省农村信用");
        names.put("GXRCU", "广西省农村信用");
        names.put("GYCB", "贵阳市商业银行");
        names.put("GZB", "赣州银行");
        names.put("GZRCU", "贵州省农村信用社");
        names.put("H3CB", "内蒙古银行");
        names.put("HANABANK", "韩亚银行");
        names.put("HBC", "湖北银行");
        names.put("HBHSBANK", "湖北银行黄石分行");
        names.put("HBRCU", "河北省农村信用社");
        names.put("HBYCBANK", "湖北银行宜昌分行");
        names.put("HDBANK", "邯郸银行");
        names.put("HKB", "汉口银行");
        names.put("HKBEA", "东亚银行");
        names.put("HNRCC", "湖南省农村信用社");
        names.put("HNRCU", "河南省农村信用");
        names.put("HRXJB", "华融湘江银行");
        names.put("HSBANK", "徽商银行");
        names.put("HSBK", "衡水银行");
        names.put("HURCB", "湖北省农村信用社");
        names.put("HXBANK", "华夏银行");
        names.put("HZCB", "杭州银行");
        names.put("HZCCB", "湖州市商业银行");
        names.put("ICBC", "中国工商银行");
        names.put("JHBANK", "金华银行");
        names.put("JINCHB", "晋城银行JCBANK");
        names.put("JJBANK", "九江银行");
        names.put("JLBANK", "吉林银行");
        names.put("JLRCU", "吉林农信");
        names.put("JNBANK", "济宁银行");
        names.put("JRCB", "江苏江阴农村商业银行");
        names.put("JSB", "晋商银行");
        names.put("JSBANK", "江苏银行");
        names.put("JSRCU", "江苏省农村信用联合社");
        names.put("JXBANK", "嘉兴银行");
        names.put("JXRCU", "江西省农村信用");
        names.put("JZBANK", "晋中市商业银行");
        names.put("KLB", "昆仑银行");
        names.put("KORLABANK", "库尔勒市商业银行");
        names.put("KSRB", "昆山农村商业银行");
        names.put("LANGFB", "廊坊银行");
        names.put("LSBANK", "莱商银行");
        names.put("LSBC", "临商银行");
        names.put("LSCCB", "乐山市商业银行");
        names.put("LYBANK", "洛阳银行");
        names.put("LYCB", "辽阳市商业银行");
        names.put("LZYH", "兰州银行");
        names.put("MTBANK", "浙江民泰商业银行");
        names.put("NBBANK", "宁波银行");
        names.put("NBYZ", "鄞州银行");
        names.put("NCB", "南昌银行");
        names.put("NHB", "南海农村信用联社");
        names.put("NHQS", "农信银清算中心");
        names.put("NJCB", "南京银行");
        names.put("NXBANK", "宁夏银行");
        names.put("NXRCU", "宁夏黄河农村商业银行");
        names.put("NYBANK", "广东南粤银行");
        names.put("ORBANK", "鄂尔多斯银行");
        names.put("PSBC", "中国邮政储蓄银行");
        names.put("QDCCB", "青岛银行");
        names.put("QLBANK", "齐鲁银行");
        names.put("SCCB", "三门峡银行");
        names.put("SCRCU", "四川省农村信用");
        names.put("SDEB", "顺德农商银行");
        names.put("SDRCU", "山东农信");
        names.put("SHBANK", "上海银行");
        names.put("SHRCB", "上海农村商业银行");
        names.put("SJBANK", "盛京银行");
        names.put("SPABANK", "平安银行");
        names.put("SPDB", "上海浦东发展银行");
        names.put("SRBANK", "上饶银行");
        names.put("SRCB", "深圳农村商业银行");
        names.put("SXCB", "绍兴银行");
        names.put("SXRCCU", "陕西信合");
        names.put("SZSBK", "石嘴山银行");
        names.put("TACCB", "泰安市商业银行");
        names.put("TCCB", "天津银行");
        names.put("TCRCB", "江苏太仓农村商业银行");
        names.put("TRCB", "天津农商银行");
        names.put("TZCB", "台州银行");
        names.put("URMQCCB", "乌鲁木齐市商业银行");
        names.put("WHCCB", "威海市商业银行");
        names.put("WHRCB", "武汉农村商业银行");
        names.put("WJRCB", "吴江农商银行");
        names.put("WRCB", "无锡农村商业银行");
        names.put("WZCB", "温州银行");
        names.put("XABANK", "西安银行");
        names.put("XCYH", "许昌银行");
        names.put("XJRCU", "新疆农村信用社");
        names.put("XLBANK", "中山小榄村镇银行");
        names.put("XMBANK", "厦门银行");
        names.put("XTB", "邢台银行");
        names.put("XXBANK", "新乡银行");
        names.put("XYBANK", "信阳银行");
        names.put("YBCCB", "宜宾市商业银行");
        names.put("YDRCB", "尧都农商行");
        names.put("YNRCC", "云南省农村信用社");
        names.put("YQCCB", "阳泉银行");
        names.put("YXCCB", "玉溪市商业银行");
        names.put("ZBCB", "齐商银行");
        names.put("ZGCCB", "自贡市商业银行");
        names.put("ZJKCCB", "张家口市商业银行");
        names.put("ZJNX", "浙江省农村信用社联合社");
        names.put("ZJTLCB", "浙江泰隆商业银行");
        names.put("ZRCBANK", "张家港农村商业银行");
        names.put("ZYCBANK", "遵义市商业银行");
        names.put("ZZBANK", "郑州银行");
        BANK_NAME = ImmutableMap.copyOf(names);
    }

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
     * 校验银行卡卡号是否合法
     *
     * @param pBankCard 银行卡号
     * @return true为有效
     */
    public static boolean isBankCardValid(String pBankCard) {
        if (StringUtil.isEmpty(pBankCard)
                || !pBankCard.matches("\\d+")) {
            return false;
        }
        pBankCard = pBankCard.trim();
        char[] chs = pBankCard.substring(0, pBankCard.length() - 1).toCharArray();
        int sum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k = k > 4 ? k*2-9 : k*2;
            }
            sum += k;
        }
        char code = (sum % 10 == 0) ? '0' : (char) ((10 - sum % 10) + '0');
        return pBankCard.charAt(pBankCard.length() - 1) == code;
    }

    /**
     * 根据银行卡号获取银行名称
     * @param pCard 银行卡号
     * @return 银行名称
     */
    public static String getBankNameByCard(String pCard){
        if(!StringUtil.isEmpty(pCard)) {
            Request request = new Request.Builder()
                    .url("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=" + pCard)
                    .build();

            BankCard card = HTTPUtil.INSTANCE.executeToObject(request, BankCard.class);
            if(card != null && card.validated){
                return BANK_NAME.getOrDefault(card.bank, "");
            }
        }
        return "";
    }

    /**
     * 根据银行卡号获取银行简称
     * @param pCard 银行卡号
     * @return 银行名称
     */
    public static String getBankShortNameByCard(String pCard){
        if(!StringUtil.isEmpty(pCard)) {
            Request request = new Request.Builder()
                    .url("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=" + pCard)
                    .build();

            BankCard card = HTTPUtil.INSTANCE.executeToObject(request, BankCard.class);
            if(card != null && card.validated){
                return card.bank;
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(getBankNameByCard("6217582000032736407"));
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private static final class BankCard{
        public String cardType;
        public String bank;
        public String key;
        public boolean validated;
    }
}
