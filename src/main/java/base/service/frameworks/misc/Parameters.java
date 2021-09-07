package base.service.frameworks.misc;

import base.service.frameworks.utils.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import base.service.frameworks.vo.StatCommon;
import base.service.frameworks.vo.UploadedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by someone on 2017-01-19.
 * <br/>
 * <br/>参数解析及处理类
 * <br/>
 * <br/>1. 自动解析 GET 或 POST 方式的键值对参数
 * <br/>2. 在参数原本不存在的情况下，将uri、ip、body放入参数集
 */
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public class Parameters {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(Parameters.class);

    private static final StatCommon DEFAULT_INFO;

    static {
        DEFAULT_INFO = new StatCommon();
        DEFAULT_INFO.pid = 1; // 产品ID
        DEFAULT_INFO.versionCode = "1000"; // 产品版本号
        DEFAULT_INFO.versionName = "v1.0.0"; // 产品版本名称
        DEFAULT_INFO.gameID = 0; // 游戏ID
        DEFAULT_INFO.channel = "default"; // 渠道
        DEFAULT_INFO.os = OS.osName(); // 系统，android|ios|H5
        DEFAULT_INFO.osVersionName = "CentOS 8"; // 系统版本名称，如android中的 4.4.0
        DEFAULT_INFO.osVersionCode = "8"; // 系统版本号，如Android的API版本
        DEFAULT_INFO.mac = "000000000000"; // MAC地址
        DEFAULT_INFO.deviceID = "000000000000"; // 设备ID
        DEFAULT_INFO.deviceModel = "server"; // 设备型号
        DEFAULT_INFO.ip = "127.0.0.1";
    }

    public static final String _request_ip_   = "_request_ip_";
    public static final String _request_uri_  = "_request_uri_";
    public static final String _request_body_ = "_request_body_";

    // JSONP 保留
    public static final String callback = "callback";

    // 授权参数
    public static final String uid      = "uid";
    public static final String token    = "token";
    public static final String realm    = "realm";
    public static final String process  = "process";
    public static final String appID    = "appID";
    public static final String appToken = "appToken";

    // 通用参数
    public static final String pid             = "pid";
    public static final String versionCode     = "versionCode";
    public static final String versionName     = "versionName";
    public static final String gameID          = "gameID";
    public static final String gameVersionCode = "gameVersionCode";
    public static final String gameVersionName = "gameVersionName";
    public static final String channel         = "channel";
    public static final String os              = "os";
    public static final String osVersionName   = "osVersionName";
    public static final String osVersionCode   = "osVersionCode";
    public static final String mac             = "mac";
    public static final String deviceID        = "deviceID";
    public static final String deviceModel     = "deviceModel";

    // 分页参数
    public static final String page = "page";
    public static final String size = "size";

    // 文件参数
    public static final String file = "file";
    public static final String type = "type";

    // ===========================================================
    // Fields
    // ===========================================================
    private final Map<String, List<String>>       mParams;
    private final Map<String, List<UploadedFile>> mFiles;
    private       HttpMethod                      mMethod;
    private       HttpHeaders                     mHeaders;
    private       long                            mCost;
    private       String                          mIP;
    private       String                          mURI;
    private       String                          mBody;

    // ===========================================================
    // Constructors
    // ===========================================================
    public Parameters(ChannelHandlerContext pContext, FullHttpRequest pRequest) {
        this.mParams = new HashMap<>();
        this.mFiles = new HashMap<>();
        this.parse(pContext, pRequest);
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================
    public HttpMethod getMethod() {
        return mMethod;
    }

    public boolean isGet() {
        return mMethod == HttpMethod.GET;
    }

    public boolean isPost() {
        return mMethod == HttpMethod.POST;
    }

    public boolean isPut() {
        return mMethod == HttpMethod.PUT;
    }

    public boolean isDelete() {
        return mMethod == HttpMethod.DELETE;
    }

    public String getIP() {
        return mIP;
    }

    public String getURI() {
        return mURI;
    }

    public String getBody() {
        return mBody;
    }

    public static StatCommon getDefaultInfo() {
        return DEFAULT_INFO;
    }

    public boolean hasHeader(String pHeaderName) {
        return mHeaders != null && mHeaders.contains(pHeaderName);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        content.append("请求详情").append(System.lineSeparator());
        content.append(" ┣ URI ").append(mURI).append(System.lineSeparator());
        content.append(" ┣ Method ").append(mMethod.name()).append(System.lineSeparator());
        content.append(" ┣ Header ").append(mHeaders.size()).append(System.lineSeparator());
        for (Iterator<Map.Entry<String, String>> it = mHeaders.iteratorAsString(); it.hasNext(); ) {
            Map.Entry<String, String> header = it.next();
            if (it.hasNext()) {
                content.append(" ┃ ┣ [ ").append(header.getKey()).append(" ] ").append(header.getValue()).append(System.lineSeparator());
            } else {
                content.append(" ┃ ┗ [ ").append(header.getKey()).append(" ] ").append(header.getValue()).append(System.lineSeparator());
            }
        }
        content.append(" ┣ IP ").append(mIP).append(System.lineSeparator());
        content.append(" ┣ Parameter ").append(mParams.values().stream().mapToInt(List::size).sum()).append(System.lineSeparator());
        for (Iterator<String> it = mParams.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            if (it.hasNext()) {
                content.append(" ┃ ┣ [ ").append(key).append(" ] ").append(getList(key)).append(System.lineSeparator());
            } else {
                content.append(" ┃ ┗ [ ").append(key).append(" ] ").append(getList(key)).append(System.lineSeparator());
            }
        }
        content.append(" ┣ files ").append(mFiles.values().stream().mapToInt(List::size).sum()).append(System.lineSeparator());
        for (String key : mFiles.keySet()) {
            List<UploadedFile> files = getFile(key);
            for (Iterator<UploadedFile> f = files.iterator(); f.hasNext(); ) {
                UploadedFile file = f.next();
                if (f.hasNext()) {
                    content.append(" ┃ ┣ [ ").append(key).append(" ] ").append(file.toString()).append(System.lineSeparator());
                } else {
                    content.append(" ┃ ┗ [ ").append(key).append(" ] ").append(file.toString()).append(System.lineSeparator());
                }
            }
        }
        if (mFiles.size() < 1) {
            content.append(" ┣ Body Raw ").append(System.lineSeparator());
            content.append(" ┃ ┗ ").append(mBody).append(System.lineSeparator());
        }
        content.append(" ┗ Parse cost ").append(mCost).append(" ns").append(System.lineSeparator());
        return content.toString();
    }

    // ===========================================================
    // Methods
    // ===========================================================
    public void clear() {
        this.mParams.clear();
        this.mFiles.clear();
    }

    public boolean hasFile(String pKey) {
        return mFiles.containsKey(pKey);
    }

    public List<UploadedFile> getFile(String pKey) {
        return mFiles.get(pKey);
    }

    public Set<String> keySet() {
        return this.mParams.keySet();
    }

    public boolean hasProperties(String pKey) {
        // 包含pKey，且值至少包含一个内容
        return mParams.get(pKey) != null && mParams.get(pKey).size() > 0;
    }

    public String getString(String pKey, String pDefaultValue) {
        if (hasProperties(pKey)) {
            return mParams.get(pKey).get(0);
        } else if (_request_ip_.equals(pKey)) {
            return mIP;
        } else if (_request_body_.equals(pKey)) {
            return mBody;
        } else if (_request_uri_.equals(pKey)) {
            return mURI;
        }
        return pDefaultValue;
    }

    public int getInt(String pKey, int pDefaultValue) {
        if (hasProperties(pKey)) {
            try {
                return Integer.parseInt(mParams.get(pKey).get(0));
            } catch (NumberFormatException ignored) {
            }
        }
        return pDefaultValue;
    }

    public long getLong(String pKey, long pDefaultValue) {
        if (hasProperties(pKey)) {
            try {
                return Long.parseLong(mParams.get(pKey).get(0));
            } catch (NumberFormatException ignored) {
            }
        }
        return pDefaultValue;
    }

    public double getDouble(String pKey, double pDefaultValue) {
        if (hasProperties(pKey)) {
            try {
                return Double.parseDouble(mParams.get(pKey).get(0));
            } catch (NumberFormatException ignored) {
            }
        }
        return pDefaultValue;
    }

    public boolean getBoolean(String pKey, boolean pDefaultValue) {
        if (hasProperties(pKey)) {
            return Boolean.parseBoolean(mParams.get(pKey).get(0));
        }
        return pDefaultValue;
    }

    public List<String> getList(String pKey) {
        if (hasProperties(pKey)) {
            return mParams.get(pKey);
        } else if (hasProperties(pKey + "[]")) {
            return mParams.get(pKey + "[]");
        }
        return new ArrayList<>();
    }

    public List<Integer> getIntArray(String pKey) {
        List<Integer> result = new ArrayList<>();
        List<String>  values = null;
        if (hasProperties(pKey)) {
            values = mParams.get(pKey);
        } else if (hasProperties(pKey + "[]")) {
            values = mParams.get(pKey + "[]");
        }
        if (values != null) {
            for (String value : values) {
                try {
                    result.add(Integer.parseInt(value));
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return result;
    }

    public List<Long> getLongArray(String pKey) {
        List<Long>   result = new ArrayList<>();
        List<String> values = null;
        if (hasProperties(pKey)) {
            values = mParams.get(pKey);
        } else if (hasProperties(pKey + "[]")) {
            values = mParams.get(pKey + "[]");
        }
        if (values != null) {
            for (String value : values) {
                try {
                    result.add(Long.parseLong(value));
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return result;
    }

    public <T> List<T> getObjectArray(String pKey, Class<List<T>> pClass, boolean pEnableExpose, boolean pEnableNumberSafe) {
        List<T>      result = new ArrayList<>();
        List<String> values = null;
        if (hasProperties(pKey)) {
            values = mParams.get(pKey);
        } else if (hasProperties(pKey + "[]")) {
            values = mParams.get(pKey + "[]");
        }
        if (values != null) {
            for (String value : values) {
                try {
                    T object = GsonUtil.fromJson(value, (Type) pClass, pEnableExpose, pEnableNumberSafe);
                    result.add(object);
                } catch (NumberFormatException e) {
                    LOG.error(e);
                }
            }
        }
        return result;
    }

    public <T> List<T> getObjectArrayFromBody(@Nonnull Class<T[]> pClass) {
        return getObjectArrayFromBody(pClass, false, false);
    }

    public <T> List<T> getObjectArrayFromBody(@Nonnull Class<T[]> pClass, boolean pEnableExpose) {
        return getObjectArrayFromBody(pClass, pEnableExpose, false);
    }

    public <T> List<T> getObjectArrayFromBody(@Nonnull Class<T[]> pClass, boolean pEnableExpose, boolean pEnableNumberSafe) {
        Map<String, Object> parameters = new HashMap<>();
        try {
            return GsonUtil.listFromJson(mBody, pClass, pEnableExpose, pEnableNumberSafe);
        } catch (Exception e) {
            LOG.error(e);
        }
        return new ArrayList<>();
    }

    public <T> T getObject(@Nonnull Class<T> pClass) {
        return getObject(pClass, false, false, false);
    }

    public <T> T getObject(@Nonnull Class<T> pClass, boolean pIsJSONBody) {
        return getObject(pClass, pIsJSONBody, false, false);
    }

    public <T> T getObject(@Nonnull Class<T> pClass, boolean pIsJSONBody, boolean pEnableExpose) {
        return getObject(pClass, pIsJSONBody, pEnableExpose, false);
    }

    public <T> T getObject(@Nonnull Class<T> pClass, boolean pIsJSONBody, boolean pEnableExpose, boolean pEnableNumberSafe) {
        Map<String, Object> parameters = new HashMap<>();
        try {
            if (pIsJSONBody) {
                return GsonUtil.fromJson(mBody, pClass, pEnableExpose, pEnableNumberSafe);
            } else {
                for (String key : mParams.keySet()) {
                    List<String> value = mParams.get(key);
                    parameters.put(key.replace("[]", ""), value.size() > 1 || key.endsWith("[]") ? value : value.get(0));
                }
                return GsonUtil.fromJson(GsonUtil.toJson(parameters), pClass, pEnableExpose, pEnableNumberSafe);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    /**
     * 分析请求参数，支持GET和POST方式
     * 会增加 ip 和 uri 参数
     *
     * @param pContext ChannelHandlerContext
     * @param pRequest HttpRequest
     */
    private void parse(ChannelHandlerContext pContext, FullHttpRequest pRequest) {
        long start = System.nanoTime();
        mParams.clear();
        mMethod = pRequest.method();
        mHeaders = pRequest.headers();

        if (HttpMethod.GET == mMethod || HttpMethod.DELETE == mMethod) {
            // GET
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(pRequest.uri(), CharsetUtil.UTF_8);
            mParams.putAll(queryStringDecoder.parameters());
        } else if (HttpMethod.POST == mMethod || HttpMethod.PUT == mMethod) {
            // body
            ByteBuf content = pRequest.content();
            mBody = content != null ? content.toString(StandardCharsets.UTF_8) : "";
            // POST
            HttpPostRequestDecoder postRequestDecoder = null;
            try {
                postRequestDecoder = new HttpPostRequestDecoder(pRequest);
                List<InterfaceHttpData> postList = postRequestDecoder.getBodyHttpDatas();

                for (InterfaceHttpData data : postList) {
                    if (InterfaceHttpData.HttpDataType.Attribute == data.getHttpDataType()) {
                        String    name      = data.getName();
                        String    value;
                        Attribute attribute = (Attribute) data;
                        attribute.setCharset(CharsetUtil.UTF_8);
                        value = attribute.getValue();
                        if (!mParams.containsKey(name)) {
                            List<String> params = new ArrayList<>();
                            params.add(value);
                            mParams.put(name, params);
                        } else {
                            mParams.get(name).add(value);
                        }
                        if (postRequestDecoder.isMultipart()) {
                            data.retain();
                        }
                    } else if (InterfaceHttpData.HttpDataType.FileUpload == data.getHttpDataType()) {
                        FileUpload upload = (FileUpload) data;
                        String     name   = upload.getName();

                        UploadedFile uploaded = new UploadedFile();
                        uploaded.raw = upload.get();
                        uploaded.charset = upload.getCharset();
                        uploaded.content = upload.getString(uploaded.charset);
                        uploaded.originName = upload.getFilename();
                        uploaded.extension = FileUtil.getExtension(uploaded.originName);

                        if (!mFiles.containsKey(name)) {
                            List<UploadedFile> files = new ArrayList<>();
                            files.add(uploaded);
                            mFiles.put(name, files);
                        } else {
                            mFiles.get(name).add(uploaded);
                        }

                        //LOG.dd("FILE %s inMemory:%s", uploaded.toString(), upload.isInMemory());

                        postRequestDecoder.removeHttpDataFromClean(data);
                    }
                }
            } catch (Exception e) {
                String contentType = "unknown";
                try {
                    contentType = pRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);
                } catch (Exception ignore) {
                }
                LOG.error(e);
            } finally {
                if (postRequestDecoder != null) {
                    postRequestDecoder.destroy();
                }
            }
        }
        String ip = pRequest.headers().get("X-Forwarded-For");
        if (ip == null) {
            InetSocketAddress address = (InetSocketAddress) pContext.channel().remoteAddress();
            ip = address.getAddress().getHostAddress();

            Inet6Address a;
        }
        if (!StringUtil.isEmpty(ip) && ip.contains(",")) {
            ip = Splitter.on(",").trimResults().splitToList(ip).get(0);
        }
        mIP = ip;

        String uri = pRequest.uri();
        if (uri.contains("?")) {
            uri = uri.substring(0, uri.indexOf("?"));
        }
        mURI = uri;

        mCost = System.nanoTime() - start;
    }

    public static StatCommon parseStatCommon(Parameters mParams) {
        StatCommon stat = new StatCommon();
        stat.pid = mParams.getInt(pid, DEFAULT_INFO.pid); // 产品ID
        stat.versionCode = mParams.getString(versionCode, DEFAULT_INFO.versionCode); // 产品版本号
        stat.versionName = mParams.getString(versionName, DEFAULT_INFO.versionName); // 产品版本名称
        stat.gameID = mParams.getInt(gameID, DEFAULT_INFO.gameID); // 游戏ID
        stat.gameVersionCode = mParams.getString(gameVersionCode, DEFAULT_INFO.gameVersionCode); // 游戏版本号
        stat.gameVersionName = mParams.getString(gameVersionName, DEFAULT_INFO.gameVersionName); // 游戏版本名称
        stat.channel = mParams.getString(channel, DEFAULT_INFO.channel); // 渠道
        stat.os = mParams.getString(os, DEFAULT_INFO.os); // 系统，android|ios|H5
        stat.osVersionName = mParams.getString(osVersionName, DEFAULT_INFO.osVersionName); // 系统版本名称，如android中的 4.4.0
        stat.osVersionCode = mParams.getString(osVersionCode, DEFAULT_INFO.osVersionCode); // 系统版本号，如Android的API版本
        stat.mac = mParams.getString(mac, DEFAULT_INFO.mac); // MAC地址
        stat.deviceID = mParams.getString(deviceID, DEFAULT_INFO.deviceID); // 设备ID
        stat.deviceModel = mParams.getString(deviceModel, DEFAULT_INFO.deviceModel); // 设备型号
        stat.ip = mParams.getIP(); // IP

        if (!StringUtil.isEmpty(stat.mac)) {
            stat.mac = stat.mac.replace(":", "").replace("-", "").toUpperCase();
        }
        if (StringUtil.isEmpty(stat.deviceID) || "null".equalsIgnoreCase(stat.deviceID)) {
            stat.deviceID = "unknown";
        }
        return stat;
    }

    /**
     * 获取头部参数
     *
     * @return 头部参数MAP
     */
    public Map<String, String> getHeaderParam() {
        Map<String, String> headerParam = new HashMap<>();
        for (Iterator<Map.Entry<String, String>> it = mHeaders.iteratorAsString(); it.hasNext(); ) {
            Map.Entry<String, String> header = it.next();
            headerParam.put(header.getKey(), header.getValue());
        }
        return headerParam;
    }

    public String printParameters() {
        List<String> pairs = new ArrayList<>();
        for (String key : mParams.keySet()) {
            List<String> values = mParams.get(key);
            for (String value : values) {
                pairs.add(key + "=" + value);
            }
        }
        return Joiner.on("&").join(pairs);
    }

    public String printParametersJSON() {
        Map<String, Object> resultMap = new HashMap<>();
        for (String key : mParams.keySet()) {
            List<String> values = mParams.get(key);
            resultMap.put(key, values.size() > 1 ? values : values.get(0));
        }
        return GsonUtil.toJson(resultMap);
    }
}
