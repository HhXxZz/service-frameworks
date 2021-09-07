package base.service.frameworks.socket.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * Created by someone on 2018-10-19.
 *
 * </pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@ChannelHandler.Sharable
public abstract class CommonTCPHandler extends SimpleChannelInboundHandler<ByteBuf> {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(CommonTCPHandler.class);


    // ===========================================================
    // Fields
    // ===========================================================
    private AtomicInteger         mConnectTimes = new AtomicInteger();
    private ChannelHandlerContext mContext;
    private ChannelPromise        mPromise;
    private long                  mMainThread;
    private boolean               enableSynchronizedCommunication;

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    public void enableSynchronizedCommunication(boolean pSynchronized) {
        this.enableSynchronizedCommunication = pSynchronized;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void exceptionCaught(ChannelHandlerContext pContext, Throwable pCause) {
        String message = pCause.getMessage();
        LOG.error(pCause.getMessage(), pCause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext pContext, ByteBuf pData) {
        if(enableSynchronizedCommunication && mPromise != null){
            if(!onReceive(pContext, pData, mPromise)){
                mPromise.setSuccess();
            }
        }else{
            onReceive(pContext, pData, null);
            if(mPromise != null){
                mPromise.setSuccess();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext pContext) {
        this.mContext = pContext;
        this.mMainThread = Thread.currentThread().getId();
        int times = mConnectTimes.incrementAndGet();
        LOG.debug("TCP channel active, the %dth connection", times);
        onClientActive(pContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext pContext) {
        LOG.warn("TCP channel inactive");
        if(this.mPromise != null && !this.mPromise.isDone()){
            this.mPromise.setFailure(new RuntimeException("TCP client channel inactive"));
        }
        onClientInactive(pContext);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext pContext, Object pEvent) {
        //super.userEventTriggered(pContext, pEvent);
        if (pEvent instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) pEvent;
            //LOG.dd("TCP idle state event state[%s]", e.state());
            switch (e.state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    onKeepAlive(pContext);
                    break;
            }
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 接收数据
     * @param pContext ChannelHandlerContext
     * @param pData 收到的数据
     * @param pPromise 此数据前发送请求的promise
     * @return true 外部已经处理了 ChannelPromise
     */
    protected abstract boolean onReceive(ChannelHandlerContext pContext, ByteBuf pData, ChannelPromise pPromise);

    /**
     * 客户端开始工作
     * @param pContext ChannelHandlerContext
     */
    protected abstract void onClientActive(ChannelHandlerContext pContext);

    /**
     * 客户端停止工作
     * @param pContext ChannelHandlerContext
     */
    protected abstract void onClientInactive(ChannelHandlerContext pContext);

    /**
     * 触发心跳
     * @param pContext ChannelHandlerContext
     */
    protected abstract void onKeepAlive(ChannelHandlerContext pContext);

    /**
     * 异步通信<br/>
     * 只有 enableSynchronizedCommunication(false) 关闭同步模式才能使用才方法通信<br/>
     * 默认是异步模式
     *
     * @param pData 报文
     */
    public void send(ByteBuf pData){
        if(enableSynchronizedCommunication){
            throw new RuntimeException("synchronized mode enabled");
        }
        if(this.mContext != null && this.mContext.channel().isActive() && this.mContext.channel().isWritable()){
            this.mContext.writeAndFlush(pData);
        }else{
            LOG.warn("nothing sent! context[%s] active[%s] writable[%s]",
                    this.mContext != null ? Integer.toHexString(this.mContext.hashCode()):"null",
                    this.mContext != null ? this.mContext.channel().isActive():"false",
                    this.mContext != null ? this.mContext.channel().isWritable():"false");
        }
    }

    /**
     * 同步通信，即请求报文后必须收到返回才能进行下一次请求<br/>
     * 此方法必须在线程中调用<br/>
     * 只有 enableSynchronizedCommunication(true) 开启同步模式才能使用才方法通信
     *
     * @param pData 报文
     * @param pListener 完成监听
     */
    public synchronized void sendSynchronized(ByteBuf pData, ChannelFutureListener pListener, long pTimeoutMillis){
        if(!enableSynchronizedCommunication){
            throw new RuntimeException("synchronized mode disabled");
        }
        if(this.mMainThread == Thread.currentThread().getId()){
            throw new RuntimeException("cannot run this method in client's thread");
        }
        if(this.mContext != null && this.mContext.channel().isActive() && this.mContext.channel().isWritable()){
            this.mPromise = this.mContext.writeAndFlush(pData).channel().newPromise();
            try {
                if(pListener != null){
                    this.mPromise.addListener(pListener);
                }
                if(pTimeoutMillis > 0){
                    this.mPromise.await(pTimeoutMillis);
                }else{
                    this.mPromise.await();
                }
            } catch (InterruptedException ignored) {}
        }else{
            LOG.warn("nothing sent! context[%s] active[%s] writable[%s]",
                    this.mContext != null ? Integer.toHexString(this.mContext.hashCode()):"null",
                    this.mContext != null ? this.mContext.channel().isActive():"false",
                    this.mContext != null ? this.mContext.channel().isWritable():"false");
        }
    }
    public synchronized void sendSynchronized(ByteBuf pData){
        sendSynchronized(pData, null, 0);
    }

    public synchronized void sendSynchronized(ByteBuf pData, ChannelFutureListener pListener){
        sendSynchronized(pData, pListener, 0);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
