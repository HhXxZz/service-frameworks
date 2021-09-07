package base.service.frameworks;

import base.service.frameworks.misc.Config;
import base.service.frameworks.utils.ConnectionUtil;
import base.service.frameworks.utils.HTTPUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.PlatformDependent;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by someone on 2016-11-17 16:22.
 * <br/>
 * <br/>HTTP服务器基类
 * <br/>支持GET及POST方式请求
 * <br/>非键值对方式的参数，需要自行处理body内容
 * <br/>
 * <br/>子类实现
 * <br/>·{@link BaseServer#release()} 退出时需要释放的业务内容
 * <br/>·{@link BaseServer#getServerInitializer()} 返回服务器初始化对象，需继承 {@link BaseServerInitializer}
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseServer {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(BaseServer.class);
    private static final int    PORT = Config.getPort();

    private Channel         mChannel;
    private EventLoopGroup  mBossGroup;
    private EventLoopGroup  mWorkerGroup;
    private boolean         isEpollAvailable;

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
    protected void start() {
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

            try {
                ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
                // ===========================================================
                // Fields
                // ===========================================================
                ServerBootstrap mBootstrap = new ServerBootstrap();
                mBootstrap.group(mBossGroup, mWorkerGroup)
                        .channel(isEpollAvailable ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        //.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        //.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 32 * 1024))
                        .childHandler(getServerInitializer());
                mChannel = mBootstrap.bind(PORT).sync().channel();
                LOG.info("Server start in http://127.0.0.1:{}", PORT);
                // 自动加载进行初始化
                Request.Builder builder= new Request.Builder();
                builder.url("http://127.0.0.1:"+PORT);
                String resp = HTTPUtil.INSTANCE.execute(builder.build());

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

    protected abstract BaseServerInitializer getServerInitializer();
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
