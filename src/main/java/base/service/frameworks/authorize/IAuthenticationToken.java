package base.service.frameworks.authorize;

/**
 * Created by someone on 2017-03-02.
 *
 * <br/>认证Token接口，用户获取认证所需的证书(密码、密钥)
 */
@SuppressWarnings({"unused", "UnnecessaryInterfaceModifier"})
public interface IAuthenticationToken {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * <br/>根据类型和身份获取身份证明
     *
     * @param pRealm 认证域，从Header["Authorization"]获取
     * @param pType 认证类型(uid|gameid|appkey),全小写,从Header["Authorization"]获取
     * @param pPrincipal 身份内容，从Header["X-WSSE"]
     * @return 该身份的认证证书(密码,密钥等)
     */
    public String getCredentials(String pRealm, String pType, String pPrincipal);

    /**
     * <br/>设置验证过的信息到实现类中
     * <br/>类似回调，它将在 {@link Authentication#isAuthorized} 返回OK时被调用
     * <br/>子类可以利用此方法获取验证时使用的 principal 和 credentials，用于response的body-sign构造
     *
     * @param pPrincipal 认证身份
     * @param pCredentials 认证证书
     */
    public void setAuthenticationInfo(String pPrincipal, String pCredentials);

    /**
     * 认证领域及类型是否支持，由调用者决定
     *
     * @param pRealm 领域
     * @param pType 类型
     * @return true为支持，false为不支持
     */
    public boolean isRealmAndTypeSupported(String pRealm, String pType);

    /**
     * 刷新身份证明<br/>
     * 考虑到调用方可能使用内存缓存，在第一次认证失败时尝试刷新身份证明进行二次认证
     *
     * @param pRealm 认证域，从Header["Authorization"]获取
     * @param pType 认证类型(uid|gameid|appkey),全小写,从Header["Authorization"]获取
     * @param pPrincipal 身份内容，从Header["X-WSSE"]
     * @return true:身份证明有变更 false:身份证明无变更
     */
    public boolean refreshCredentials(String pRealm, String pType, String pPrincipal);
}
