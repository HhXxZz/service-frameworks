package base.service.frameworks.processor;

import base.service.frameworks.utils.GsonUtil;
import base.service.frameworks.utils.StringUtil;
import base.service.frameworks.misc.Code;
import base.service.frameworks.misc.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseTask {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger logger = LogManager.getLogger(BaseTask.class);

    protected Parameters            mParams;
    //protected String                mCallback;
    protected boolean               mEnableToken = false; // 所有任务应当默认校验token
    protected boolean               mEnableMember = false;
    protected boolean               mEnableAuthentication = false;
    protected boolean               mEnableEncryption = false;


    public BaseTask(Parameters pParams) {
        this.mParams = pParams;
        //this.mCallback = mParams.getString(Parameters.callback, "");
    }

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


    @Override
    public String toString(){
        try {
            if(!StringUtil.isEmpty(prepare())){
                return prepare();
            }


            return execute();
        }catch (Exception e){
            logger.error(getClass().getSimpleName(),e);
            return errorResponse(Code.BaseCode.ERR_DEFAULT);
        }finally {
            //TODO
            release();
        }
    }


    protected void release() {
        if(this.mParams != null) {
            this.mParams.clear();
            this.mParams = null;
        }
    }

    public abstract String prepare();

    public abstract String execute();


    /**
     * 返回成功内容 {"code":0, "msg":"success", data:{}}，支持jsonp
     *
     */
    public static String response(int code,String msg, Object obj){
        Response response = new Response();
        response.setCode(code);
        response.setMsg(msg);
        response.setData(obj);
        return GsonUtil.toJson(response);
    }

    public static String successResponse(Object obj){
        Code code = Code.BaseCode.SUCCESS;
        return response(code.getCode(),code.getMessage(),obj);
    }

    public static String errorResponse(Code code){
        return response(code.getCode(),code.getMessage(),null);
    }


    static class Response{
        private int code;
        private String msg;
        private Object data;
        private long time;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
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
