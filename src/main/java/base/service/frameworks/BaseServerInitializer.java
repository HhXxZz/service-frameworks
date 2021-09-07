package base.service.frameworks;

import base.service.frameworks.misc.Config;
import base.service.frameworks.utils.ConnectionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;

import static io.netty.handler.codec.http.HttpMethod.*;

/**
 * Created by someone on 2016-11-17 16:24.
 * <br/>
 * <br/>服务器初始化
 * <br/>
 * <br/>子类需实现
 * <br/>· {@link BaseServerInitializer#init()} 需要初始化的业务内容
 * <br/>· {@link BaseServerInitializer#initCustomChannel(ChannelPipeline)} 需要向pipe添加的自定义Handler
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseServerInitializer extends ChannelInitializer<SocketChannel> {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    protected void initChannel(SocketChannel ch) {
        // 初始化数据库
        if(Config.isDAOEnabled()){
            ConnectionUtil.INSTANCE.init();
        }

        // 初始话任务线程池
        init();

        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024 * 1024 * 200)); // 最大上传200MB内容
        p.addLast(new CorsHandler(
                CorsConfigBuilder
                        .forAnyOrigin()
                        .allowedRequestMethods(OPTIONS, GET, POST, PUT, DELETE)
                        .allowedRequestHeaders("X-Requested-With", "Content-Type", "Content-Length")
                        .allowCredentials()
                        .build()
        ));
        initCustomChannel(p);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 需要初始化的业务内容
     */
    protected abstract void init();

    /**
     * 需要添加的自定义Handler
     * @param pPipe 需要添加的 {@link ChannelHandler}
     */
    protected abstract void initCustomChannel(ChannelPipeline pPipe);

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
