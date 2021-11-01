package org.hxz.service.frameworks.base;

import org.hxz.service.frameworks.misc.Code;
import org.hxz.service.frameworks.misc.Parameters;
import org.hxz.service.frameworks.utils.ResponseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by someone on 2017/1/12.
 * <br/>
 * <br/>请求分发器
 * <br/>
 * <br/>子类仅需实现 {@link BaseRequestDispatcher#handled(ChannelHandlerContext, FullHttpRequest, Parameters)}
 * <br/>当子类无法处理请求时，会返回业务不支持
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseRequestDispatcher {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(BaseRequestDispatcher.class);

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
    public void dispatch(FullHttpRequest pRequest, ChannelHandlerContext pContext, Parameters pParams){
        if(!handled(pContext, pRequest, pParams)){
            ResponseUtil.writeErrorResponse(pRequest, pContext, pParams.getString(Parameters.callback, ""), Code.BaseCode.ERR_UNSUPPORTED_SERVICE);
        }
    }

    /**
     * <br/>处理请求，子类实现
     * <br/>根据处理结果，决定是否返回业务不支持的错误内容
     *
     * @param pContext Context
     * @param pRequest 请求对象
     * @param pParams 参数解析对象
     * @return true表示已处理，子类已经完成请求并返回了结果；false表示子类实现无法处理，需要返回不支持的错误信息
     */
    protected abstract boolean handled(ChannelHandlerContext pContext, FullHttpRequest pRequest, Parameters pParams);

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
