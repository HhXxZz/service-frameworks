package base.service.frameworks.rpc.callback;

import base.service.frameworks.rpc.common.MessageResponse;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端回调池，用于保存调用发送请求出去的{@link CallbackContext}上下文，用于nio异步通信收到服务端响应后回调成功或者失败
 */
public class CallbackPool {

    /**
     * Map默认键数量，全局唯一静态{@link CallbackContext}的<code>ConcurrentHashMap</code>初始化参数
     */
    private static final int INITIAL_CAPACITY = 128 * 4 / 3;

    /**
     * Map的扩容装载因子，全局唯一静态{@link CallbackContext}的<code>ConcurrentHashMap</code>初始化参数
     */
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * Map的并发度，也就是segament数量，读不锁写锁，全局唯一静态{@link CallbackContext}的<code>ConcurrentHashMap</code>初始化参数
     */
    private static final int CONCURRENCY_LEVEL = 16;

    /**
     * 保存{@link CallbackContext}的Map，键为调用的唯一标示requestId</tt>
     */
    private static final ConcurrentHashMap<String, CallbackContext> CALLBACK_MAP = new ConcurrentHashMap<>(
            INITIAL_CAPACITY, LOAD_FACTOR, CONCURRENCY_LEVEL);

    /**
     * 根据requestId标示获取上下文
     * 
     * @param requestId
     * @return
     */
    public static CallbackContext getContext(String requestId) {
        return CALLBACK_MAP.get(requestId);
    }

    /**
     * 根据id标示获取上下文内的回调
     * 
     * @param requestId
     * @return
     */
    public static Callback<MessageResponse> get(String requestId) {
        CallbackContext callbackContext = CALLBACK_MAP.get(requestId);
        return callbackContext == null ? null : callbackContext.getCallback();
    }

    /**
     * 放入回调上下文
     * 
     * @param requestId
     *            requestId
     * @param timeout
     *            客户端调用超时
     * @param isShortAliveConn
     *            客户端是否为短连接
     * @param channel
     *            客户端连接的channel
     * @param callback
     *            客户端句柄callback
     */
    public static void put(String requestId, int timeout,
            boolean isShortAliveConn, Channel channel, Callback<MessageResponse> callback) {
        CALLBACK_MAP.putIfAbsent(requestId, new CallbackContext(requestId, System.currentTimeMillis(),
                timeout, isShortAliveConn, channel, callback));
    }

    /**
     * 移除回调上下文
     * 
     * @param requestId
     */
    public static void remove(String requestId) {
        CALLBACK_MAP.remove(requestId);
    }

    /**
     * 清理Map
     */
    public static void clear() {
        CALLBACK_MAP.clear();
    }

    /**
     * 获取保存回调上下文的Map
     * 
     * @return
     */
    public static ConcurrentHashMap<String, CallbackContext> getCALLBACK_MAP() {
        return CALLBACK_MAP;
    }

}
