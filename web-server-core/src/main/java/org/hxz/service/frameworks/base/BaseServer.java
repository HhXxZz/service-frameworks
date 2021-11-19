package org.hxz.service.frameworks.base;

import org.hxz.service.frameworks.misc.Config;
import org.hxz.service.frameworks.utils.ConnectionUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.netty.handler.codec.http.HttpMethod.*;

/**
 * Created by someone on 2016-11-17 16:22.
 * <br/>
 * <br/>HTTP服务器基类
 * <br/>支持GET及POST方式请求
 * <br/>非键值对方式的参数，需要自行处理body内容
 * <br/>
 * <br/>子类实现
 * <br/>·{@link BaseServer#release()} 退出时需要释放的业务内容
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseServer {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(BaseServer.class);
    private static final int    PORT = Config.getIntProperty("server.port",8686);

    private Channel         mChannel;
    private EventLoopGroup  mBossGroup;
    private EventLoopGroup  mWorkerGroup;
    private boolean         isEpollAvailable;
    private DefaultEventExecutorGroup executorGroup;
    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    @SuppressWarnings("AnonymousHasLambdaAlternative")
    public void start() {
        if(Config.isInitialed() && PORT > 0) {
            checkEnvironment();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdown();
                }
            });

            mBossGroup = isEpollAvailable ? new EpollEventLoopGroup() : new NioEventLoopGroup(); // 默认CPU核数 * 2
            mWorkerGroup = isEpollAvailable ? new EpollEventLoopGroup() : new NioEventLoopGroup(); // 默认CPU核数 * 2

            /**
             * 1、如果所有客户端的并发连接数小于业务线程数，那么建议将请求消息封装成任务投递到后端普通业务线程池执行即可，ChannelHandler不需要处理复杂业务逻辑，也不需要再绑定EventExecutorGroup
             *
             * 2、如果所有客户端的并发连接数大于等于业务需要配置的线程数，那么可以为业务ChannelHandler绑定EventExecutorGroup——使用addLast的方法
             */
            executorGroup = new DefaultEventExecutorGroup(8, new DefaultThreadFactory("biz"));

            try {
                ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
                // ===========================================================
                // Fields
                // ===========================================================
                ServerBootstrap mBootstrap = new ServerBootstrap();
                mBootstrap.group(mBossGroup, mWorkerGroup)
                        .channel(isEpollAvailable ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpRequestDecoder());
                                pipeline.addLast(new HttpResponseEncoder());
                                pipeline.addLast(new HttpObjectAggregator(1048576));
                                pipeline.addLast(new ChunkedWriteHandler());
                                pipeline.addLast(new CorsHandler(
                                        CorsConfigBuilder
                                                .forAnyOrigin()
                                                .allowedRequestMethods(OPTIONS, GET, POST, PUT, DELETE)
                                                .allowedRequestHeaders("X-Requested-With", "Content-Type", "Content-Length")
                                                .allowCredentials()
                                                .build()
                                ));
                                pipeline.addLast(executorGroup, new BaseServerHandler());
                                initCustomChannel(pipeline);
                            }
                        });
                mChannel = mBootstrap.bind(PORT).sync().channel();
                LOG.info("Server start in http://127.0.0.1:{}", PORT);
                // 自动加载进行初始化
//                Request.Builder builder= new Request.Builder();
//                builder.url("http://127.0.0.1:"+PORT+"/test/test");
//                String resp = HTTPUtil.INSTANCE.execute(builder.build());
//                System.out.println(resp);
                //初始化数据库
//                if(Config.isDAOEnabled()){
//                    ConnectionUtil.INSTANCE.init();
//                }
                //初始化抽象函数
                init();

                mChannel.closeFuture().sync();
            } catch (InterruptedException e) {
                LOG.error("Server start failed!", e);
            } finally {
                try {
                    mBossGroup.shutdownGracefully().sync();
                } catch (Exception ignore) {
                    LOG.warn("mBossGroup shutdown fail");
                }
                try {
                    mWorkerGroup.shutdownGracefully().sync();
                } catch (Exception ignore) {
                    LOG.warn("mWorkerGroup shutdown fail");
                }
            }
        }else{
            LOG.error("Config unavailable! Exit!");
        }
    }

    private void shutdown() {
        LOG.info("Server will shutdown.");
        try {
            mBossGroup.shutdownGracefully().sync();
        } catch (Exception ignore){
            LOG.warn("mBossGroup shutdown fail");
        }
        try {
            mWorkerGroup.shutdownGracefully().sync();
        } catch (Exception ignore) {
            LOG.warn("mWorkerGroup shutdown fail");
        }

        // 释放任务线程池
        release();
        // 释放数据库
        if(Config.isDAOEnabled()){
            ConnectionUtil.INSTANCE.release();
        }

        try {
            mChannel.closeFuture().sync();
        } catch (InterruptedException ignored) {

        }finally {
            LOG.info("Server shutdown successfully.");
        }
    }

    private void checkEnvironment() {
        if (!PlatformDependent.isWindows()) {
            LOG.debug("Server not on Windows, checking EPOLL status.");

            if (isEpollAvailable = Epoll.isAvailable()) {
                LOG.debug("Epoll is available, use EPOLL.");
            } else {
                LOG.warn("Epoll is unavailable, use NIO. ({})", Epoll.unavailabilityCause().getMessage());
            }
        }else{
            LOG.debug("Server on Windows, use NIO.");
        }
    }

    /**
     * 释放资源，子类继承实现
     */
    protected abstract void release();

    protected abstract void init();

    protected abstract void initCustomChannel(ChannelPipeline pPipe);




    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
