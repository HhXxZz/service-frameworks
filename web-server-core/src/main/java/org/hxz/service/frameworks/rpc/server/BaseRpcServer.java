package org.hxz.service.frameworks.rpc.server;

import org.hxz.service.frameworks.misc.Config;
import org.hxz.service.frameworks.rpc.common.MessageRequestDecoder;
import org.hxz.service.frameworks.rpc.common.MessageResponseEncoder;
import org.hxz.service.frameworks.base.ApiFactory;
import org.hxz.service.frameworks.rpc.common.ServiceInfo;
import org.hxz.service.frameworks.rpc.nacos.NacosManager;
import org.hxz.service.frameworks.utils.AppContext;
import org.hxz.service.frameworks.utils.ConnectionUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by hxz on 2021/6/29 14:31.
 */

@Component
@ComponentScan("org.hxz")
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration.class})
public abstract class BaseRpcServer {
    private static final Logger logger = LogManager.getLogger(BaseRpcServer.class);

    public void start() {
        Environment environment = AppContext.getBean(Environment.class);

        String nacosAddress = environment.getProperty("nacos.address","127.0.0.1:8848");
        String nacosProduct = environment.getProperty("nacos.product","product");
        String nacosModule = environment.getProperty("nacos.module","module");
        String nacosService = environment.getProperty("nacos.service","service");
        int weight = environment.getProperty("server.weight",Integer.class,1);

        String host = environment.getProperty("server.host","127.0.0.1");
        int port = environment.getProperty("server.port",Integer.class,8889);;

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServerAddress(nacosAddress);
        serviceInfo.setProduct(nacosProduct);
        serviceInfo.setModule(nacosModule);
        serviceInfo.setHost(host);
        serviceInfo.setPort(port);
        serviceInfo.setWeight(weight);
        serviceInfo.setServiceName(nacosService);
        ApiFactory.INSTANCE.init(serviceInfo,initApiPackage());
        serviceInfo.setApiList(ApiFactory.INSTANCE.getApiList());

        NacosManager.INSTANCE.init(nacosAddress);
        NacosManager.INSTANCE.register(serviceInfo);

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
            //ConnectionUtil.INSTANCE.init();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected abstract String initApiPackage();

}
