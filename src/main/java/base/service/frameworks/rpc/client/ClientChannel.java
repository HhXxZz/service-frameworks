package base.service.frameworks.rpc.client;

import base.service.frameworks.rpc.callback.CallFuture;
import base.service.frameworks.rpc.callback.CallbackPool;
import base.service.frameworks.rpc.common.MessageRequest;
import base.service.frameworks.rpc.common.MessageResponse;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端连接池内的对象封装
 */
public class ClientChannel {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannel.class);

    /**
     * netty连接channel的future引用
     */
    private ChannelFuture channelFuture;

    /**
     * 异步调用
     * 
     * @param request
     * @param readTimeout
     *            客户端调用超时时间
     * @return
     * @throws Exception
     */
    public CallFuture<MessageResponse> asyncTransport(MessageRequest request, int readTimeout) {
        if (channelFuture != null) {
            try {
                CallFuture<MessageResponse> future = CallFuture.newInstance();
                CallbackPool.put(request.getRequestId(), readTimeout, false, null, future);
                channelFuture.channel().writeAndFlush(request);
                return future;
            } catch (Exception e) {
            	logger.error("Failed to transport to " + channelFuture.channel() + " due to "+ e.getMessage());
            }
        } else {
        	logger.error("Socket channel is not well established, so failed to transport");
        }
        return null;
    }

    /**
     * 同步调用
     * 
     * @param request
     * @param readTimeout
     *            客户端调用超时时间
     * @return
     * @throws Exception
     */
    public MessageResponse syncTransport(MessageRequest request, int readTimeout) throws Exception {
        return asyncTransport(request, readTimeout).get();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

}
