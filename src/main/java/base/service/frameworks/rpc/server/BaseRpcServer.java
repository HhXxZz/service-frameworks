package base.service.frameworks.rpc.server;

import base.service.frameworks.misc.Config;
import base.service.frameworks.redis.RedisClient;
import base.service.frameworks.rpc.common.MessageRequestDecoder;
import base.service.frameworks.rpc.common.MessageResponseEncoder;
import base.service.frameworks.base.ApiFactory;
import base.service.frameworks.rpc.common.ServiceInfo;
import base.service.frameworks.rpc.nacos.NacosManager;
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

public class BaseRpcServer {
    private static final Logger logger = LogManager.getLogger(BaseRpcServer.class);

    private String host;
    private int port;
    private ServiceInfo serviceInfo;

    public BaseRpcServer(String host,int port){
        //String address = Config.getProperty("zk.address","127.0.0.1:2181,127.0.0.1:3181,127.0.0.1:4181");
        String address = Config.getProperty("zk.address","127.0.0.1:8848");
        String product = Config.getProperty("zk.product","service");
        String business = Config.getProperty("zk.business","example");
//        this.host = Config.getProperty("server.host","127.0.0.1");
//        this.port = Config.getIntProperty("server.port",9898);
        this.host = host;
        this.port = port;

        serviceInfo = new ServiceInfo();
        serviceInfo.setServerAddress(address);
        serviceInfo.setHost(host);
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(product);

//        serviceInfo.setProduct(product);
//        serviceInfo.setModule(business);

        ApiFactory.INSTANCE.init(serviceInfo,"base.service.frameworks.processor");
        serviceInfo.setApiList(ApiFactory.INSTANCE.getApiList());

//        ServiceRegistry serviceRegistry = new ServiceRegistry(serviceInfo);
//        serviceRegistry.register();

        NacosManager.INSTANCE.init(address);
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

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
