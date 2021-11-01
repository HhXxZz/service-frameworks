package org.hxz.service.frameworks.utils;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;
import org.hxz.service.frameworks.vo.APKInfo;
import org.hxz.service.frameworks.vo.Unzipped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by someone on 2020/7/20 16:00.
 */
public class APKParser {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(APKParser.class);

    private static final float[]  RADIX_MULTS     = new float[]{0.00390625F, 3.051758E-5F, 1.192093E-7F, 4.656613E-10F};
    private static final String[] DIMENSION_UNITS = new String[]{"px", "dip", "sp", "pt", "in", "mm", "", ""};
    private static final String[] FRACTION_UNITS  = new String[]{"%", "%p", "", "", "", "", "", ""};

    // <manifest android:versionCode="1" android:versionName="1.0.18" package="tech.yude.ipts.app.tv.scoreboard">
    // <uses-sdk android:minSdkVersion="19" android:targetSdkVersion="30"></uses-sdk>
    private static final Pattern PATTERN_MANIFEST           = Pattern.compile("<manifest (.*?)>");
    private static final Pattern PATTERN_USER_SDK           = Pattern.compile("<uses-sdk (.*?)>");
    private static final Pattern PATTERN_VERSION_CODE       = Pattern.compile("android:versionCode=\"(.*?)\"");
    private static final Pattern PATTERN_VERSION_NAME       = Pattern.compile("android:versionName=\"(.*?)\"");
    private static final Pattern PATTERN_PACKAGE_NAME       = Pattern.compile("package=\"(.*?)\"");
    private static final Pattern PATTERN_MIN_SDK_VERSION    = Pattern.compile("android:minSdkVersion=\"(.*?)\"");
    private static final Pattern PATTERN_TARGET_SDK_VERSION = Pattern.compile("android:targetSdkVersion=\"(.*?)\"");

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
    private static float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }

    private static String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }

    private static String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TypedValue.TYPE_DIMENSION) {
            return complexToFloat(data) +
                    DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type == TypedValue.TYPE_FRACTION) {
            return complexToFloat(data) +
                    FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }

    public static APKInfo parse(Path pAPKPath) {
        if (pAPKPath != null && pAPKPath.toString().endsWith("apk")) {
            try {
                Map<String, Unzipped> resources = FileUtil.unzip(Files.readAllBytes(pAPKPath), false);
                if (resources.containsKey("AndroidManifest.xml")) {
                    return parse(resources.get("AndroidManifest.xml").raw);
                }
            } catch (IOException e) {
                LOG.error("read apk file failed.",e);
            }
        }
        return null;
    }

    public static APKInfo parse(byte[] pAndroidManifestContent) {
        if (pAndroidManifestContent != null) {
            AXmlResourceParser parser = new AXmlResourceParser();
            parser.open(new ByteArrayInputStream(pAndroidManifestContent));
            StringBuilder indent  = new StringBuilder(10);
            StringBuilder content = new StringBuilder();
            String        tab     = "    ";

            try {
                int type;
                int lastType = 0;
                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        case XmlPullParser.START_DOCUMENT: {
                            content.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                            break;
                        }
                        case XmlPullParser.START_TAG: {
                            String prefix = parser.getPrefix();
                            prefix = prefix != null && prefix.length() != 0 ? prefix + ":" : "";
                            content.append(System.lineSeparator()).append(indent).append("<").append(prefix).append(parser.getName());
                            indent.append(tab);

                            int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
                            int namespaceCount       = parser.getNamespaceCount(parser.getDepth());

                            for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
                                content.append(" ").append("xmlns:").append(parser.getNamespacePrefix(i)).append("=\"").append(parser.getNamespaceUri(i)).append("\"");
                            }

                            for (int i = 0; i != parser.getAttributeCount(); ++i) {
                                String attribPrefix = parser.getAttributePrefix(i);
                                attribPrefix = attribPrefix != null && attribPrefix.length() != 0 ? attribPrefix + ":" : "";
                                content.append(" ").append(attribPrefix).append(parser.getAttributeName(i)).append("=\"").append(getAttributeValue(parser, i)).append("\"");
                            }

                            content.append(">");
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            indent.setLength(indent.length() - tab.length());
                            String prefix = parser.getPrefix();
                            prefix = prefix != null && prefix.length() != 0 ? prefix + ":" : "";
                            content.append(lastType == XmlPullParser.END_TAG ? System.lineSeparator() : "");
                            content.append(lastType == XmlPullParser.END_TAG ? indent : "").append("</").append(prefix).append(parser.getName()).append(">");
                            break;
                        }
                        case XmlPullParser.TEXT: {
                            content.append(indent).append(parser.getText());
                            break;
                        }
                    }
                    lastType = type;
                }
                parser.close();

                APKInfo info = new APKInfo();
                Matcher manifestMatcher = PATTERN_MANIFEST.matcher(content.toString());
                if (manifestMatcher.find()) {
                    String manifest = manifestMatcher.group(1);
                    Matcher packageNameMatcher = PATTERN_PACKAGE_NAME.matcher(manifest);
                    Matcher versionCodeMatcher = PATTERN_VERSION_CODE.matcher(manifest);
                    Matcher versionNameMatcher = PATTERN_VERSION_NAME.matcher(manifest);
                    info.packageName = packageNameMatcher.find() ? packageNameMatcher.group(1) : "";
                    info.versionCode = versionCodeMatcher.find() ? NumberUtil.parseInt(versionCodeMatcher.group(1), 0) : 0;
                    info.versionName = versionNameMatcher.find() ? versionNameMatcher.group(1) : "";
                }
                Matcher userSDKMatcher = PATTERN_USER_SDK.matcher(content.toString());
                if (userSDKMatcher.find()) {
                    String useSDK = userSDKMatcher.group(1);
                    Matcher minSDKVersionMatcher = PATTERN_MIN_SDK_VERSION.matcher(useSDK);
                    Matcher targetSDKVersionMatcher = PATTERN_TARGET_SDK_VERSION.matcher(useSDK);
                    info.minSDKVersion = minSDKVersionMatcher.find() ? NumberUtil.parseInt(minSDKVersionMatcher.group(1), 0) : 0;
                    info.targetSDKVersion = targetSDKVersionMatcher.find() ? NumberUtil.parseInt(targetSDKVersionMatcher.group(1), 0) : 0;
                }
                //LOG.dd("%s", GsonUtil.toJson(info));
                return info;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        APKInfo info = parse(Paths.get("C:\\Users\\seven\\Desktop\\发布内容\\IPTS\\正式环境\\ipts-app-release-v1.0.28.apk"));
        System.out.println(GsonUtil.toPrettyJson(info));
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
