package org.hxz.service.frameworks.rpc.server;

import org.hxz.service.frameworks.misc.Config;
import org.hxz.service.frameworks.redis.RedisClient;
import org.hxz.service.frameworks.rpc.common.MessageRequestDecoder;
import org.hxz.service.frameworks.rpc.common.MessageResponseEncoder;
import org.hxz.service.frameworks.base.ApiFactory;
import org.hxz.service.frameworks.rpc.common.ServiceInfo;
import org.hxz.service.frameworks.rpc.nacos.NacosManager;
import org.hxz.service.frameworks.utils.ConnectionUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hxz on 2021/6/29 14:31.
 */

public abstract class BaseRpcServer {
    private static final Logger logger = LogManager.getLogger(BaseRpcServer.class);

    private final String host;
    private final int port;

    public BaseRpcServer(){
        String address = Config.getProperty("nacos.address","127.0.0.1:8848");
        String serviceName = Config.getProperty("nacos.serviceName","service");
        host = Config.getProperty("server.host","127.0.0.1");;
        port = Config.getIntProperty("server.port",9898);;

        NacosManager.INSTANCE.init(address);

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServerAddress(address);
        serviceInfo.setHost(host);
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(serviceName);
        ApiFactory.INSTANCE.init(serviceInfo,initApiPackage());
        serviceInfo.setApiList(ApiFactory.INSTANCE.getApiList());
        NacosManager.INSTANCE.register(serviceInfo);
        RedisClient.INSTANCE.init();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    .addLast(new MessageRequestDecoder())
                                    .addLast(new MessageResponseEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(1024*1024, 0, 4, 0, 0))
                                    .addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 128)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;

            ChannelFuture future = bootstrap.bind(host,port).sync();
            if(future.isSuccess()){
                logger.info("Server started on port {}", port);
            }
            ConnectionUtil.INSTANCE.init();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected abstract String initApiPackage();

}
