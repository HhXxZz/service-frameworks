package org.hxz.service.frameworks.authorize;

import io.netty.handler.codec.http.HttpHeaders;
import okhttp3.Headers;
import org.hxz.service.frameworks.misc.Parameters;
import org.hxz.service.frameworks.utils.DateUtil;
import org.hxz.service.frameworks.utils.EncryptUtil;
import org.hxz.service.frameworks.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by someone on 2017-02-27.
 *
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Authentication {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(Authentication.class);

    public static final String AUTHORIZE_OK                = "OK";
    public static final String UNAUTHORIZED_INVALID_BODY   = "Invalid body-Sign";
    public static final String UNAUTHORIZED_INVALID_DIGEST = "Invalid passwordDigest";

    public static final String Authorization = "Authorization";
    public static final String X_WSSE        = "X-WSSE";
    public static final String Request_Id    = "Request-Id";
    public static final String Body_Sign     = "Body-Sign";

    public static final String INTERNAL_PRINCIPAL = "84b2fbd41ee14e799a6e7e99d89af6c0";
    public static final String INTERNAL_CREDENTIALS = "5a54b46911044b1691a2fc0e0351f5e6";

    public static final String REALM_COMMON_USER     = "common.user";
    public static final String REALM_TYPE_UID     = "uid";

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
     * 获取 header["Authorization"] 的值
     *
     * @param pRealm 授权领域，common.market|common.user|common.payment
     * @param pType 授权类型，取决密钥获取途径，uid|gameID|appKey
     * @return 返回 header["Authorization"]
     */
    @SuppressWarnings("SameParameterValue")
    public static String buildAuthorization(String pRealm, String pType){
        return String.format("WSSE realm=\"%s\", profile=\"UsernameToken\", type=\"%s\"", pRealm, pType);
    }

    /**
     * <br/>校验是否包含正确授权信息
     * <br/>1. 校验 WSSE信息 in Header["Authorization"] & Header["X-WSSE"]
     * <br/>2. 如果存在HTTP Body校验 Header["Body-Sign"]
     * <br/>3. 在不需要WSSE校验的服务中不需要使用此方法
     *
     * @param pHeaders 针对netty的Headers，详见{@link HttpHeaders}
     * @param pBody HTTP Body内容，在存在header["body-sign"]时进行校验
     * @param pToken 认证Token
     * @return OK 表示pHeaders中包含正确的WSSE授权信息，其他返回为错误信息
     */
    @SuppressWarnings("Duplicates")
    public static String isAuthorized(HttpHeaders pHeaders, String pBody, IAuthenticationToken pToken){
        if(pToken == null){
            throw new RuntimeException("IAuthenticationToken must not be null");
        }
        //LOG.dd("Netty authenticate");
        String authorization = pHeaders.get(Authorization);
        String wsse = pHeaders.get(X_WSSE);
        String requestID = pHeaders.get(Request_Id);
        String bodySign = pHeaders.get(Body_Sign);
        return authenticate(authorization, wsse, requestID, bodySign, pBody, pToken);
    }

    /**
     * <br/>校验是否包含正确授权信息
     * <br/>1. 校验 WSSE信息
     * <br/>2. 如果存在HTTP Body校验 Header["Body-Sign"]
     * <br/>3. 在不需要WSSE校验的服务中不需要使用此方法
     *
     * @param pHeaders 针对OkHttp3的Headers，详见{@link Headers}
     * @param pBody HTTP Body内容，在存在header["body-sign"]时进行校验
     * @param pToken 认证Token
     * @return OK 表示pHeaders中包含正确的WSSE授权信息，其他返回为错误信息
     */
    @SuppressWarnings("Duplicates")
    public static String isAuthorized(Headers pHeaders, String pBody, IAuthenticationToken pToken){
        if(pToken == null){
            throw new RuntimeException("IAuthenticationToken must not be null");
        }
        //LOG.dd("OkHttp3 authenticate");
        String authorization = pHeaders.get(Authorization);
        String wsse = pHeaders.get(X_WSSE);
        String requestID = pHeaders.get(Request_Id);
        String bodySign = pHeaders.get(Body_Sign);
        return authenticate(authorization, wsse, requestID, bodySign, pBody, pToken);
    }

    /**
     * <br/>校验是否包含正确授权信息
     * <br/>1. 校验 WSSE信息
     * <br/>2. 如果存在HTTP Body校验 Header["Body-Sign"]
     * <br/>3. 在不需要WSSE校验的服务中不需要使用此方法
     *
     * @param pParams 本框架解析的Parameters对象
     * @param pToken 认证Token
     * @return OK 表示pHeaders中包含正确的WSSE授权信息，其他返回为错误信息
     */
    public static String isAuthorized(Parameters pParams, IAuthenticationToken pToken){
        if(pToken == null){
            throw new RuntimeException("IAuthenticationToken must not be null");
        }

        Map<String, String> headers = null;//pParams.getHeaderParam();
        String              body    = pParams.getBody();

        String authorization = headers.get(Authorization);
        String wsse          = headers.get(X_WSSE);
        String requestID     = headers.get(Request_Id);
        String bodySign      = headers.get(Body_Sign);
        return authenticate(authorization, wsse, requestID, bodySign, body, pToken);
    }

    /**
     * <br/>校验BodySign
     * <br/>此方法一般用于请求后的response校验
     * <br/>认证信息与请求时相同
     *
     * @param pHeaders 针对netty的Headers，详见{@link HttpHeaders}
     * @param pBody HTTP Body内容
     * @param pPrincipal 认证身份(uid|gameID|appkey)
     * @param pCredentials 认证证书(token|password|appSecret)
     * @return OK 表示正确的BodySign，其他为错误信息
     */
    public static String checkBodySign(HttpHeaders pHeaders, String pBody, String pPrincipal, String pCredentials){
        String requestID = pHeaders.get(Request_Id);
        String bodySign = pHeaders.get(Body_Sign);
        return checkBodySign(requestID, bodySign, pBody, pPrincipal, pCredentials);
    }

    /**
     * <br/>校验BodySign
     * <br/>此方法一般用于请求后的response校验
     * <br/>认证信息与请求时相同
     *
     * @param pHeaders 针对OkHttp3的Headers，详见{@link Headers}
     * @param pBody HTTP Body内容
     * @param pPrincipal 认证身份(uid|gameID|appkey)
     * @param pCredentials 认证证书(token|password|appSecret)
     * @return OK 表示正确的BodySign，其他为错误信息
     */
    public static String checkBodySign(Headers pHeaders, String pBody, String pPrincipal, String pCredentials){
        String requestID = pHeaders.get(Request_Id);
        String bodySign = pHeaders.get(Body_Sign);
        return checkBodySign(requestID, bodySign, pBody, pPrincipal, pCredentials);
    }

    /**
     * <br/>请求认证处理
     * <br/>1. 判断Header["Authorization"] & Header["X-WSSE"]有效性
     * <br/>2. 判断认证信息合法性
     * <br/>3. 认证成功时，pToken的 setAuthenticationInfo 方法将被调用，用于计算Response时的body-sign
     * @param pAuthorization Header["Authorization"] 内容
     * @param pWSSE Header["X-WSSE"] 内容
     * @return OK if success
     */
    private static String authenticate(String pAuthorization, String pWSSE, String pRequestID, String pBodySign, String pBody, IAuthenticationToken pToken){
        if(!StringUtil.isEmpty(pBody)){
            if(StringUtil.isEmpty(pBodySign)){
                LOG.error("Miss Body-Sign when body is not empty");
                return "Miss Body-Sign when body is not empty";
            }
            if(StringUtil.isEmpty(pRequestID)){
                LOG.error("Miss Request-Id when body is not empty");
                return "Miss Request-Id when body is not empty";
            }
        }

        // Authorization : WSSE realm="common.market", profile="UsernameToken", type="uid"
        if (pAuthorization != null && pAuthorization.startsWith("WSSE")) {
            String realm = StringUtil.getFirstMatchGroup("realm=\"(.*?)\"", pAuthorization).toLowerCase();
            String type  = StringUtil.getFirstMatchGroup("type=\"(.*?)\"", pAuthorization).toLowerCase();
            if (pToken.isRealmAndTypeSupported(realm, type)) {
                // X-WSSE: UsernameToken Username="<AppKey>", PasswordDigest="<PasswordDigest>", Nonce="<Nonce>", Created="<NonceGenerationTime>"
                if(pWSSE != null && pWSSE.startsWith("UsernameToken")){
                    String username = StringUtil.getFirstMatchGroup("(?:U|u)sername=\"(.*?)\"", pWSSE);
                    String passwordDigest = StringUtil.getFirstMatchGroup("(?:P|p)assword(?:D|d)igest=\"(.*?)\"", pWSSE);
                    String nonce = StringUtil.getFirstMatchGroup("(?:N|n)once=\"(.*?)\"", pWSSE);
                    String created = StringUtil.getFirstMatchGroup("(?:C|c)reated=\"(.*?)\"", pWSSE);

                    long createdTS = DateUtil.getTimestampISO8601(created);
                    if(createdTS < System.currentTimeMillis() - 600000){ // 请求时间超过10分钟打印警告信息
                        LOG.warn("%s(%s) time-lag more then 10 minutes(username:%s, created:%s)", realm, type, username, created);
                    }
                    String credentials = pToken.getCredentials(realm, type, username);
                    if(StringUtil.isEmpty(credentials)){
                        // 身份证明为空
                        LOG.error("Unauthorized realm[%s] type[%s] principal[%s]", realm, type, username);
                        return "Unauthorized principal";
                    }

                    String localPasswordDigest = buildPasswordDigest(nonce, created, credentials);
                    if(!passwordDigest.equals(localPasswordDigest)){
                        // 校验失败，尝试刷新一次身份证明
                        if(pToken.refreshCredentials(realm, type, username)){
                            // 刷新成功，身份证明有变更
                            credentials = pToken.getCredentials(realm, type, username);
                            if(!StringUtil.isEmpty(credentials)) {
                                localPasswordDigest = buildPasswordDigest(nonce, created, credentials);
                                if (!passwordDigest.equals(localPasswordDigest)) {
                                    // 二次确认依旧失败，返回错误结果
                                    LOG.error("Invalid passwordDigest Req[%s] Loc[%s]", passwordDigest, localPasswordDigest);
                                    return UNAUTHORIZED_INVALID_DIGEST;
                                }
                            }
                        }else{
                            // 刷新失败，通常原因包括不存在此身份证明或身份证明未变更
                            // 建议使用者在刷新方法的实现中增加防攻击逻辑
                            LOG.error("Invalid passwordDigest Req[%s] Loc[%s]", passwordDigest, localPasswordDigest);
                            return UNAUTHORIZED_INVALID_DIGEST;
                        }
                    }

                    // 身份验证通过，说明身份证明有效，可直接进行body判断
                    if(!StringUtil.isEmpty(pBody)){
                        // body不为空，校验bodySign
                        String result = checkBodySign(pRequestID, pBodySign, pBody, username, credentials);
                        if(AUTHORIZE_OK.equals(result)){
                            pToken.setAuthenticationInfo(username, credentials);
                        }
                        return result;
                    }else{
                        pToken.setAuthenticationInfo(username, credentials);
                        return AUTHORIZE_OK;
                    }

                }else{
                    LOG.error("Miss X-WSSE or it's value is invalid : %s", pWSSE);
                    return "Miss X-WSSE or it's value is invalid";
                }
            }else{
                LOG.error("Authorization realm(type) is not supported : %s(%s)", realm, type);
                return "Authorization realm is not supported";
            }
        }else{
            LOG.error("Miss Authorization or it's value is invalid : %s", pAuthorization);
            return "Miss Authorization or it's value is invalid";
        }
    }

    /**
     * 校验BodySign
     *
     * @param pRequestID Header["Request-Id"] 值
     * @param pBodySign Header["Body-Sign"] 值
     * @param pBody Http Body
     * @param pPrincipal 认证身份(uid|gameID|appkey)
     * @param pCredentials 认证证书(token|password|appSecret)
     * @return OK if success
     */
    private static String checkBodySign(String pRequestID, String pBodySign, String pBody, String pPrincipal, String pCredentials){
        if(!StringUtil.isEmpty(pBody)){
            if(StringUtil.isEmpty(pBodySign)){
                LOG.error("Miss Body-Sign when body is not empty");
                return "Miss Body-Sign when body is not empty";
            }
        }else{
            // body为空则不校验
            return AUTHORIZE_OK;
        }
        // Body-Sign: sign_type="HMAC-SHA256", signature=""
        String requestBodySign = StringUtil.getFirstMatchGroup("(?:S|s)ignature=\"(.*?)\"", pBodySign);
        String localBodySign = buildBodySignature(pPrincipal, pCredentials, pRequestID, pBody);
        if(requestBodySign.equals(localBodySign)){
            return AUTHORIZE_OK;
        }else{
            LOG.error("Invalid body-Sign [%s : %s] Req[%s] Loc[%s] ReqID[%s] Body[%s]", pPrincipal, pCredentials, requestBodySign, localBodySign, pRequestID, pBody);
            return "Invalid body-Sign";
        }
    }

    /**
     * <br/>获取 header["X-WSSE"] 的值
     * <br/>根据业务，使用不同的内容填充 pPrincipal 和 pCredentials
     * <br/>举例:
     * <br/> - 用户系统在使用 Authentication 授权访问时，pPrincipal=uid，pCredentials=token
     * <br/> - 联运SDK在使用 Authentication 授权访问时，pPrincipal=gameID，pCredentials=appSecret
     *
     * @param pPrincipal 认证身份
     * @param pCredentials 认证证书
     * @return header["X-WSSE"]的值
     */
    public static String buildX_WSSE(String pPrincipal, String pCredentials){
        return buildX_WSSE(
                pPrincipal,
                pCredentials,
                StringUtil.generateUUID(),
                DateUtil.getDateTimeStringISO8601());
    }

    /**
     * <br/>获取 header["X-WSSE"] 的值
     * <br/>根据业务，使用不同的内容填充 pUsername 和 pCredentials
     * <br/>举例:
     * <br/> - 用户系统在使用 Authentication 授权访问时，pPrincipal=uid，pCredentials=token
     * <br/> - 联运SDK在使用 Authentication 授权访问时，pPrincipal=gameID，pCredentials=appSecret
     *
     * @param pPrincipal 认证身份
     * @param pCredentials 认证证书
     * @param pNonce 随机字符串
     * @param pCreated 时间字符串，符合iso8601(yyyy-MM-ddTHH:mm:ssZ)
     * @return header["X-WSSE"]的值
     */
    public static String buildX_WSSE(String pPrincipal, String pCredentials, String pNonce, String pCreated){
        return String.format("UsernameToken Username=\"%s\", PasswordDigest=\"%s\", Nonce=\"%s\", Created=\"%s\"",
                pPrincipal,
                buildPasswordDigest(pNonce, pCreated, pCredentials),
                pNonce,
                pCreated);
    }

    /**
     * <br/>获取 header["Body-Sign"] 的值
     * <br/>1. Body-Sign 会根据 HTTP Body 计算签名，接受方和请求方都应当校验此签名
     * <br/>2. 签名算法：Base64(HMAC_SHA256(body, key)
     * <br/>3. key的拼接根据 request 和 response 不同
     * <br/> - request  : key = pPrincipal&pCredentials&pRequestID
     * <br/> - response : key = pPrincipal&pCredentials
     * <br/>4. body为空时，按照空字符串处理
     *
     * @param pPrincipal 认证身份
     * @param pCredentials 认证证书
     * @param pRequestID 请求ID
     * @param pBody HTTP body
     * @return header["Body-Sign"]的值
     */
    public static String buildBodySign(String pPrincipal, String pCredentials, String pRequestID, String pBody){
        return String.format("sign_type=\"HMAC-SHA256\", signature=\"%s\"", buildBodySignature(pPrincipal, pCredentials, pRequestID, pBody));
    }

    private static String buildPasswordDigest(String pNonce, String pCreated, String pCredentials){
        return EncryptUtil.encode_base64_sha_256(pNonce+pCreated+pCredentials);
    }

    private static String buildBodySignature(String pPrincipal, String pCredentials, String pRequestID, String pBody){
        StringBuilder key = new StringBuilder();
        key.append(pPrincipal).append("&").append(pCredentials);
        if(!StringUtil.isEmpty(pRequestID)){
            key.append("&").append(pRequestID);
        }
        return EncryptUtil.encode_base64_hmac_sha_256(pBody, key.toString());
    }

    public static void main(String[] args){
        System.out.println(StringUtil.generateUUID());
        System.out.println(StringUtil.generateUUID());
        String    requestID  = StringUtil.generateUUID();
        System.out.println(Authentication.Authorization + " " + Authentication.buildAuthorization(InternalToken.REALM, InternalToken.REALM_TYPE_INTERNAL));
        System.out.println(Authentication.X_WSSE + " " + Authentication.buildX_WSSE(Authentication.INTERNAL_PRINCIPAL, Authentication.INTERNAL_CREDENTIALS));
        System.out.println(Authentication.Request_Id + " " + requestID);
        System.out.println(Authentication.Body_Sign + " " + Authentication.buildBodySign(Authentication.INTERNAL_PRINCIPAL, Authentication.INTERNAL_CREDENTIALS, requestID, "123"));

    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
