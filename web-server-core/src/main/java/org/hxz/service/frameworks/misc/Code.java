package org.hxz.service.frameworks.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by someone on 2017-01-18.
 */
@SuppressWarnings("unused")
public interface Code {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    Map<Integer, Code> Codes = new HashMap<>();


    // ===========================================================
    // Methods
    // ===========================================================
    static Code getError(int pErrorCode) {
        if (Codes.containsKey(pErrorCode)) {
            return Codes.get(pErrorCode);
        }
        return BaseCode.ERR_DEFAULT;
    }

    int getCode();

    String getMessage();

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    enum BaseCode implements Code {
        SUCCESS(0, "成功"),
        ERR_DEFAULT(200, "未知错误"),
        ERR_TASK_NOT_IMPLEMENTS(201, "处理功能尚未实现"),
        ERR_TOKEN_VALIDATION_NOT_IMPLEMENTS(202, "token校验功能尚未实现"),
        ERR_AUTHENTICATION_NOT_IMPLEMENTS(203, "Authentication功能尚未实现"),
        // 通用
        ERR_UNSUPPORTED_SERVICE(400, "不支持此服务"),
        ERR_UNAUTHORIZED(401, "授权验证失败"),
        ERR_NOT_REGISTER_MEMBER(402, "非注册账号"),
        ERR_INVALID_TOKEN(403, "无效的授权"),
        ERR_MISS_PARAMETERS(404, "缺少必要的参数"),
        ERR_INVALID_PARAMETERS(405, "参数内容不正确"),
        ERR_CUSTOM_ERROR(406, "自定义错误"),
        ERR_SERVICE_BUSY(500, "服务器开小差了~请稍后重试~"),
        // 上传
        ERR_INVALID_UPLOAD_CONFIG(600, "服务器当前不支持文件上传，请联系管理员"),
        ERR_INVALID_UPLOAD_FILE_TYPE(601, "上传文件分类暂不支持"),
        ERR_INVALID_UPLOAD_FILE_EXTENSION(602, "上传文件格式暂不支持"),
        ERR_UPLOAD_FILE_SIZE_EXCEEDED(603, "上传文件大小超出限制"),
        ERR_UPLOAD_FILE_WRITE_TO_DISK_FAILED(604, "上传文件保存失败"),
        ERR_UPLOAD_FILE_RSYNC_FAILED(605, "上传文件发布失败，可能会影响访问，请重试");

        private final int    mCode;
        private final String mMessage;

        // ===========================================================
        // Constructors
        // ===========================================================
        BaseCode(int pCode, String pMessage) {
            this.mCode    = pCode;
            this.mMessage = pMessage;
            Code.Codes.put(pCode, this);
        }

        @Override
        public int getCode() {
            return this.mCode;
        }

        @Override
        public String getMessage() {
            return this.mMessage;
        }
    }
}
