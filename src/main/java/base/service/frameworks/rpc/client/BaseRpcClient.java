package base.service.frameworks.rpc.client;

import base.service.frameworks.misc.Config;
import base.service.frameworks.rpc.common.*;
import base.service.frameworks.utils.ConnectionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Created by hxz on 2021/6/29 14:31.
 */

public final class BaseRpcClient {
    private static final Logger logger = LogManager.getLogger(BaseRpcClient.class);

    private final EventLoopGroup workerGroup;
    private Bootstrap bootstrap;

    private final String host;
    private final int port;

    public BaseRpcClient(String host,int port){
        this.host = host;
        this.port = port;
        workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    .addLast(new MessageRequestEncoder())
                                    .addLast(new MessageResponseDecoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(1024*1024, 0, 4, 0, 0))
                                    .addLast(new ClientHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 128)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 128)
            ;
            //logger.info("BaseRpcClient.init");
        } catch (Exception e) {
            logger.error("BaseRpcClient start failed!", e);
        }
    }

    public ChannelFuture connect() {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    logger.info("Connection " + channelFuture.channel() + " is well established");
                } else {
                    logger.info(String.format("Connection get failed on %s due to %s",
                            channelFuture.cause().getMessage(), channelFuture.cause()));
                }
            });
            return future;
        }catch (Exception e){
            logger.error("BaseRpcClient connect error!", e);
        }
        return null;
    }

    private void shutdown() {
        logger.info("BaseRpcClient will shutdown.");
        try {
            workerGroup.shutdownGracefully().sync();
        } catch (Exception ignore) {
            logger.warn("mWorkerGroup shutdown fail");
        }
        // 释放数据库
        if(Config.isDAOEnabled()){
            ConnectionUtil.INSTANCE.release();
        }
    }

    public String getInfo() {
        return host + ":" + port ;
    }


}
