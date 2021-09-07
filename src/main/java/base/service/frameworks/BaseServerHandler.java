package base.service.frameworks;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by someone on 2016-11-17 16:22.
 * <br/>
 * <br/>请求处理
 * <br/>子类需实现 {@link BaseServerHandler#process(ChannelHandlerContext, FullHttpRequest)}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(BaseServerHandler.class);

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================
    public BaseServerHandler(){
        super(true);
    }

    public BaseServerHandler(boolean pAutoRelease){
        super(true);
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void channelReadComplete(ChannelHandlerContext pContext) {
        pContext.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext pContext, FullHttpRequest pRequest) {
        if (HttpUtil.is100ContinueExpected(pRequest)) {
            send100Continue(pContext);
        }
        process(pContext, pRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext pContext, Throwable pCause) {
        String message = pCause.getMessage();
        if(!message.contains("Connection reset by peer") && !message.contains("远程主机强迫关闭了一个现有的连接")){
            LOG.error(pCause.getMessage(), pCause);
        }
        LOG.error("exceptionCaught.error",pCause);
        pContext.close();
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
    protected abstract void process(ChannelHandlerContext pContext, FullHttpRequest pRequest);
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
