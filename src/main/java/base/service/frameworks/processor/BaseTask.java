package base.service.frameworks.processor;

import base.service.frameworks.misc.StaticFileSetting;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import base.service.frameworks.misc.Code;
import base.service.frameworks.misc.Parameters;
import base.service.frameworks.utils.ResponseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by SPM on 2017/3/6.
 * <p>
 * 任务基类
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class BaseTask implements Runnable {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(BaseTask.class);

    // ===========================================================
    // Fields
    // ===========================================================
    protected HttpRequest           mRequest;
    protected ChannelHandlerContext mContext;
    protected Parameters            mParams;
    protected String                mCallback;
    protected boolean               mEnableToken = false; // 所有任务应当默认校验token
    protected boolean               mEnableMember = false;
    protected boolean               mEnableAuthentication = false;
    protected boolean               mEnableEncryption = false;


    // ===========================================================
    // Constructors
    // ===========================================================
    public BaseTask(HttpRequest pRequest,
                    ChannelHandlerContext pContext,
                    Parameters pParams) {
        this.mRequest = pRequest;
        this.mContext = pContext;
        this.mParams = pParams;
        this.mCallback = mParams.getString(Parameters.callback, "");
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================
    public void enableToken(boolean pEnableToken) {
        this.mEnableToken = pEnableToken;
    }

    public void enableMember(boolean pEnableMember) {
        this.mEnableMember = pEnableMember;
    }

    public void enableAuthentication(boolean pEnableAuthentication) {
        this.mEnableAuthentication = pEnableAuthentication;
    }

    public void enableEncryption(boolean pEnableEncryption) {
        this.mEnableEncryption = pEnableEncryption;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void run() {
        try {
            if(this.mEnableToken){
                Code code = checkToken();
                if(code != Code.BaseCode.SUCCESS){
                    ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, code);
                    return;
                }
            }
            if(this.mEnableAuthentication){
                Code code = checkAuthentication();
                if(code != Code.BaseCode.SUCCESS){
                    ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, code);
                    return;
                }
            }
            Code code = process();
            if (code.getCode() != Code.BaseCode.SUCCESS.getCode()) {
                ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, code);
            }
        } catch (Exception e) {
            LOG.error("unhandled exception : {}", e.getMessage(),e);
            ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, Code.BaseCode.ERR_SERVICE_BUSY);
        } finally {
            release();
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================
    /**
     * 返回成功内容 {"code":0, "msg":"success", data:{}}，支持jsonp
     *
     * @param pResult data内容
     */
    protected void responseSuccess(Object pResult) {
        responseSuccess(pResult, null);
    }

    protected void responseSuccess(Object pResult, HttpResponseStatus pStatus) {
        responseSuccess(pResult, false, pStatus);
    }

    protected void responseSuccess(Object pResult, boolean pEnableExpose) {
        responseSuccess(pResult, pEnableExpose, null);
    }

    protected void responseSuccess(Object pResult, boolean pEnableExpose, HttpResponseStatus pStatus) {
        ResponseUtil.writeSuccessResponse(mRequest, mContext, mCallback, pResult, pEnableExpose, pStatus);
    }

    /**
     * 返回错误内容 {"code":1, "msg":"fail"}，支持jsonp
     *
     * @param pErrorCode    错误码
     * @param pErrorMessage 错误信息
     */
    protected void responseError(int pErrorCode, String pErrorMessage) {
        responseError(pErrorCode, pErrorMessage, null);
    }

    protected void responseError(int pErrorCode, String pErrorMessage, HttpResponseStatus pStatus) {
        ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, pErrorCode, pErrorMessage, pStatus);
    }

    /**
     * 返回错误内容 {"code":1, "msg":"fail"}，支持jsonp
     *
     * @param pCode 错误码
     */
    protected void responseError(Code pCode) {
        responseError(pCode, null);
    }

    protected void responseError(Code pCode, HttpResponseStatus pStatus) {
        ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, pCode, pStatus);
    }

    /**
     * 返回错误内容 {"code":1, "msg":"fail"}，支持jsonp
     *
     * @param pCode 错误码
     */
    protected void responseError(Code pCode, Object pResult) {
        responseError(pCode, pResult, null);
    }

    protected void responseError(Code pCode, Object pResult, HttpResponseStatus pStatus) {
        ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, pCode, pResult, pStatus);
    }

    /**
     * 返回错误内容 {"code":1, "msg":"fail"}，支持jsonp
     *
     * @param pCode 错误码
     */
    protected void responseError(Code pCode, Object pResult, boolean pEnableExpose) {
        responseError(pCode, pResult, pEnableExpose, null);
    }

    protected void responseError(Code pCode, Object pResult, boolean pEnableExpose, HttpResponseStatus pStatus) {
        ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, pCode, pResult, pEnableExpose, pStatus);
    }

    /**
     * 返回错误内容 {"code":1, "msg":"fail"}，支持jsonp
     *
     * @param pMessage 错误信息
     */
    protected void responseCustomError(String pMessage) {
        responseCustomError(pMessage, null);
    }

    protected void responseCustomError(String pMessage, HttpResponseStatus pStatus) {
        ResponseUtil.writeErrorResponse(mRequest, mContext, mCallback, Code.BaseCode.ERR_CUSTOM_ERROR.getCode(), pMessage, pStatus);
    }

    /**
     * 返回简单成功内容 {"code":0, "msg":"success"}，支持jsonp
     */
    protected void responseSuccessSimple() {
        responseSuccessSimple(null);
    }

    protected void responseSuccessSimple(HttpResponseStatus pStatus) {
        ResponseUtil.writeSimpleSuccessResponse(mRequest, mContext, mCallback, pStatus);
    }

    /**
     * 返回网页
     *
     * @param pContent 网页
     */
    protected void responseHTML(String pContent) {
        responseHTML(pContent, null);
    }

    protected void responseHTML(String pContent, HttpResponseStatus pStatus) {
        ResponseUtil.writeHTMLResponse(mRequest, mContext, pContent, pStatus);
    }

    /**
     * 返回错误网页，一般用于扫码后的处理失败
     *
     * @param pCode 错误码
     */
    protected void responseErrorHTML(Code pCode) {
        responseErrorHTML(pCode, null);
    }

    protected void responseErrorHTML(Code pCode, HttpResponseStatus pStatus) {
        ResponseUtil.writeErrorHTMLResponse(mRequest, mContext, pCode, pStatus);
    }

    /**
     * 返回内容
     *
     * @param pContent 内容
     */
    protected void response(String pContent) {
        response(pContent, null);
    }

    protected void response(String pContent, HttpResponseStatus pStatus) {
        ResponseUtil.writeResponse(mRequest, mContext, pContent, pStatus);
    }

    /**
     * 返回内容
     *
     * @param pContent 内容
     */
    protected void responseObject(Object pContent) {
        responseObject(pContent, null);
    }

    protected void responseObject(Object pContent, HttpResponseStatus pStatus) {
        ResponseUtil.writeResponseObject(mRequest, mContext, mCallback, pContent, pStatus);
    }

    /**
     * 返回内容
     *
     * @param pContent 内容
     */
    protected void responseObject(Object pContent, boolean pEnableExpose) {
        responseObject(pContent, pEnableExpose, null);
    }

    protected void responseObject(Object pContent, boolean pEnableExpose, HttpResponseStatus pStatus) {
        ResponseUtil.writeResponseObject(mRequest, mContext, mCallback, pContent, pEnableExpose, pStatus);
    }

    /**
     * 返回内容，并自定义一些header
     *
     * @param pHeaders HTTP Header
     * @param pContent 内容
     */
    protected void responseWithHeaders(Map<AsciiString, String> pHeaders, String pContent) {
        responseWithHeaders(pHeaders, pContent, null);
    }

    protected void responseWithHeaders(Map<AsciiString, String> pHeaders, String pContent, HttpResponseStatus pStatus) {
        ResponseUtil.writeResponse(mRequest, mContext, pHeaders, pContent);
    }

    /**
     * 返回302重定向
     *
     * @param pURL 定向链接
     */
    protected void redirect(String pURL) {
        ResponseUtil.redirect(mRequest, mContext, pURL);
    }

    protected void release() {
        this.mParams.clear();
        this.mParams = null;
        this.mContext = null;
        this.mRequest = null;
    }

    protected Code process() {
        // 此方法需由子类override实现
        return Code.BaseCode.ERR_TASK_NOT_IMPLEMENTS;
    }

    protected Code checkToken(){
        // 此方法需由子类override实现
        return Code.BaseCode.ERR_TOKEN_VALIDATION_NOT_IMPLEMENTS;
    }

    protected Code checkAuthentication(){
        // 此方法需由子类override实现
        return Code.BaseCode.ERR_AUTHENTICATION_NOT_IMPLEMENTS;
    }



    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
