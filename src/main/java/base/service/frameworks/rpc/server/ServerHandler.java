package base.service.frameworks.rpc.server;

import base.service.frameworks.base.ApiFactory;
import base.service.frameworks.misc.Parameters;
import base.service.frameworks.rpc.common.MessageRequest;
import base.service.frameworks.rpc.common.MessageResponse;
import base.service.frameworks.utils.GsonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.regex.Pattern;

public class ServerHandler extends SimpleChannelInboundHandler<MessageRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

//    private final ServiceConfig serviceConfig;
//
//    public XServerHandler(ServiceConfig serviceConfig) {
//        this.serviceConfig = serviceConfig;
//    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,final MessageRequest request) {
        logger.info("Receive request " + request.toString());
        MessageResponse response = handleRequest(request);
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
            logger.info("Send response for request " + request.getRequestId());
        });
    }

    private MessageResponse handleRequest(MessageRequest request) {
        MessageResponse response = new MessageResponse();
        response.setRequestId(request.getRequestId());
    	int errCode = 0;
    	Class<?> clazz = ApiFactory.INSTANCE.getLogicClass(request.getServiceName(), request.getApi());
		if(clazz != null){
			try {
				Constructor<?> constructor = clazz.getConstructor(Parameters.class);
                System.out.println(request.getParams().toString());
				Object logicObject = constructor.newInstance(request.getParams());
				response.setData(logicObject.toString());
                errCode = 0;
			} catch (Exception e) {
				logger.error("handleRequest",e);
                errCode = 2;
			}
		}
		response.setErrMsg("");
		response.setErrCode(errCode);
		logger.info("Send response " + response.toString());
		return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Server Closed:"+ctx.channel().remoteAddress().toString(), cause);
        //ctx.close();
    }
}
