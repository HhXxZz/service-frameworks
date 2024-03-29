package org.hxz.service.frameworks.base;

import org.hxz.service.frameworks.misc.Code;
import org.hxz.service.frameworks.misc.Parameters;
import org.hxz.service.frameworks.processor.BaseTask;
import org.hxz.service.frameworks.rpc.client.ClientPool;
import org.hxz.service.frameworks.rpc.common.MessageRequest;
import org.hxz.service.frameworks.rpc.common.MessageResponse;
import org.hxz.service.frameworks.rpc.server.ServiceManager;
import org.hxz.service.frameworks.utils.ResponseUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

import static org.hxz.service.frameworks.utils.ResponseUtil.Content_Type_Application_Json;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by someone on 2016-11-17 16:22.
 * <br/>
 * <br/>请求处理
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class BaseServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LogManager.getLogger(BaseServerHandler.class);

//    public BaseServerHandler(){
//        super(true);
//    }
//
//    public BaseServerHandler(boolean pAutoRelease){
//        super(true);
//    }




//    @Override
//    public void channelReadComplete(ChannelHandlerContext pContext) {
//        pContext.flush();
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            String requestPath = decoder.path();
            logger.info("requestPath=" + requestPath);
            if (requestPath == null) {
                logger.error("requestPath null error");
                ResponseUtil.writeErrorResponse(request, ctx, "", 0, "error");
                return;
            }
            String[] requestPaths = requestPath.split("/");
            String serviceName = requestPaths[1];
            String api = requestPath.substring(requestPath.indexOf("/",1));


            ClientPool clientPool = ServiceManager.INSTANCE.chooseClient(serviceName);
            if (clientPool == null) {
                logger.error("ClientPool is null error");
                ResponseUtil.writeErrorResponse(request, ctx, "", 0, "error");
                return;
            }

            Parameters parameters = new Parameters(ctx, request);
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setRequestId(UUID.randomUUID().toString());
            messageRequest.setServiceName(serviceName);
            messageRequest.setApi(api);
            messageRequest.setParams(parameters);
            MessageResponse responseStr = clientPool.syncTransport(messageRequest);
            //MessageResponse responseStr = new MessageResponse();
            //responseStr.setData("aaaa");

            writeJsonResponse(request,ctx,responseStr.getData());

        }catch (Exception e){
            logger.error("handler.error",e);
            writeJsonResponse(request,ctx,BaseTask.errorResponse(Code.BaseCode.ERR_DEFAULT));
        }
    }


    private void writeJsonResponse(FullHttpRequest request,ChannelHandlerContext ctx,String responseStr){
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, Content_Type_Application_Json);
        if (HttpUtil.isKeepAlive(request)) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable pCause) {
        String message = pCause.getMessage();
        if(!message.contains("Connection reset by peer") && !message.contains("远程主机强迫关闭了一个现有的连接")){
            logger.error(pCause.getMessage(), pCause);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private static void send100Continue(ChannelHandlerContext pContext) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        pContext.writeAndFlush(response, pContext.voidPromise());
    }

    /**
     * 业务处理
     */
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
