package base.service.frameworks.rpc.server;

import base.service.frameworks.rpc.common.MessageRequest;
import base.service.frameworks.rpc.common.MessageResponse;
import base.service.frameworks.utils.GsonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //logger.info("Send response for request " + request.getRequestid());
            }
        });
    }

    private MessageResponse handleRequest(MessageRequest request) {
        MessageResponse response = new MessageResponse();
        response.setRequestId(request.getRequestId());
    	int errCode;
//    	Class<?> clazz = serviceConfig.getLogicClass(request.getModule(), request.getAction());
//		if(clazz != null){
			try {
//				Constructor<?> constructor = clazz.getConstructor(Map.class);
//				Object logicObject = constructor.newInstance(request.getParameters());

				response.setData("{\"data\",\"success\"}");
                errCode = 0;
			} catch (Exception e) {
				logger.error("handleRequest",e);
                errCode = 2;
			}
//		}
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
