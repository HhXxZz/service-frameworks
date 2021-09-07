package base.service.frameworks.processor;


import base.service.frameworks.misc.Code;
import base.service.frameworks.misc.Parameters;
import base.service.frameworks.processor.annotation.API;
import base.service.frameworks.rpc.zk.ApiFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;


/**
 * Created by dada on 2021/5/19 16:24.
 */
@SuppressWarnings("unused")
@API(uri = "/api/relation/info", name = "好友名片信息", enableMember = true,method = ApiFactory.POST)
public class InfoTask extends BaseTask {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================
    public InfoTask(HttpRequest pRequest, ChannelHandlerContext pContext, Parameters pParams) {
        super(pRequest, pContext, pParams);
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================

    @Override
    protected Code process() {
        return Code.BaseCode.SUCCESS;
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
