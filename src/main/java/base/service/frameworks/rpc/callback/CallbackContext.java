package base.service.frameworks.rpc.callback;

import base.service.frameworks.rpc.common.MessageResponse;
import io.netty.channel.Channel;

/**
 * 回调上下文，用于定位回调，计算超时、关闭channel等辅助功能
 */
public class CallbackContext {

    /**
     * 用于标示某个回调的id
     */
    private final String requestId;

    /**
     * 调用起始时间
     */
    private final long startTime;

    /**
     * 客户端调用是否为短连接
     */
    private boolean isShortAliveConn;

    /**
     * 客户端调用用的channel，如果是长连接可以为空，只有配合{@link #isShortAliveConn}为true时候，才会再回调中关闭
     */
    private Channel channel;

    /**
     * 调用结束时间
     */
    private final int timeout;

    /**
     * 回调
     */
    private final Callback<MessageResponse> callback;

    /**
     * Creates a new instance of CallbackContext.
     * 
     * @param requestId
     * @param startTime
     * @param timeout
     * @param isShortAliveConn
     * @param channel
     * @param callback
     */
    public CallbackContext(String requestId, long startTime, int timeout, boolean isShortAliveConn,
            Channel channel, Callback<MessageResponse> callback) {
        super();
        this.requestId = requestId;
        this.startTime = startTime;
        this.timeout = timeout;
        this.isShortAliveConn = isShortAliveConn;
        this.channel = channel;
        this.callback = callback;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getRequestId() {
        return requestId;
    }

    public Callback<MessageResponse> getCallback() {
        return callback;
    }

    public boolean isShortAliveConn() {
        return isShortAliveConn;
    }

    public void setShortAliveConn(boolean isShortAliveConn) {
        this.isShortAliveConn = isShortAliveConn;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
