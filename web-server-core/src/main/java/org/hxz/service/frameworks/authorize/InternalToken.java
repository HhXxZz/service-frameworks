package org.hxz.service.frameworks.authorize;

import org.hxz.service.frameworks.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by someone on 2020/6/12 10:45.
 */
public class InternalToken implements IAuthenticationToken {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(IAuthenticationToken.class);

    public static InternalToken DEFAULT = new InternalToken();

    public static final String REALM               = "base.internal";
    public static final String REALM_TYPE_INTERNAL = "internal";
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
    @Override
    public String getCredentials(String pRealm, String pType, String pPrincipal) {
        if (REALM_TYPE_INTERNAL.equals(pType)) {
            if (Authentication.INTERNAL_PRINCIPAL.equals(pPrincipal)) {
                return Authentication.INTERNAL_CREDENTIALS;
            }
            return StringUtil.generateUUID();
        }
        return "";
    }

    @Override
    public void setAuthenticationInfo(String pPrincipal, String pCredentials) {
        // 不需要重用token
    }

    @Override
    public boolean isRealmAndTypeSupported(String pRealm, String pType) {
        return REALM.equals(pRealm) && REALM_TYPE_INTERNAL.equals(pType);
    }

    @Override
    public boolean refreshCredentials(String pRealm, String pType, String pPrincipal) {
        // 不存在缓存，不需要二次刷新
        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
