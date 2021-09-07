package base.service.frameworks.rpc.client;

import base.service.frameworks.rpc.callback.Callback;
import base.service.frameworks.rpc.callback.CallbackContext;
import base.service.frameworks.rpc.callback.CallbackPool;
import base.service.frameworks.rpc.common.MessageResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHandler extends SimpleChannelInboundHandler<MessageResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageResponse response) throws Exception {
        String requestId = response.getRequestId();
        try {
            logger.info("requestId:" + requestId);
            CallbackContext context = CallbackPool.getContext(requestId);
            if (context == null) {
                logger.warn("Receive msg from server but no context found, requestId=" + requestId);
                return;
            }
            Callback<MessageResponse> cb = context.getCallback();
            cb.handleResult(response);

            if (context.isShortAliveConn()) {
                Channel channel = context.getChannel();
                if (channel != null) {
                    logger.info("Close " + channel + ", requestId=" + requestId);
                    channel.close();
                }
            }
        } finally {
            CallbackPool.remove(requestId);
            //ContextHolder.clean();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("ServerClosed:"+ctx.channel().remoteAddress().toString(),cause);
        ctx.close();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }



}
