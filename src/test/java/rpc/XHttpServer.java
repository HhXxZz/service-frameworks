package rpc;


import base.service.frameworks.rpc.zk.ServiceDiscovery;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class XHttpServer {

	private int defaultPort = 8686;
	private String defaultHost = "0.0.0.0";
	
	private static Logger logger = LoggerFactory.getLogger(XHttpServer.class);
	
	protected EventLoopGroup bossGroup = null;
	protected EventLoopGroup workerGroup = null;
	protected ServerBootstrap bootstrap = null;
	private DefaultEventExecutorGroup executorGroup = null;
	
	private boolean isInit = false;

	
	public XHttpServer init(Properties properties) {

		//defaultPort = Integer.parseInt(properties.getProperty("service.server.port").trim());


		String address = "127.0.0.1:2181,127.0.0.1:3181,127.0.0.1:4181";
		String product = "service";
		String business = "example";


//        ServiceRegistry serviceRegistry = new ServiceRegistry(host,port,product,business,address);
//        BaseRpcServer rpcServer = new BaseRpcServer();
//        rpcServer.start(host,port);
		ServiceDiscovery serviceDiscovery = new ServiceDiscovery(address,product,business);
		isInit = true;
		return this;
	}
	
	public void start() {
		if(isInit){
			executorGroup = new DefaultEventExecutorGroup(8, new DefaultThreadFactory("bizEventExecutorGroup"));
			bossGroup = new NioEventLoopGroup(1);
	        workerGroup = new NioEventLoopGroup(10);
			try {
				bootstrap = new ServerBootstrap();
				bootstrap.group(bossGroup, workerGroup);
				bootstrap.channel(NioServerSocketChannel.class);
				bootstrap.option(ChannelOption.SO_BACKLOG, 100);
				bootstrap.handler(new LoggingHandler(LogLevel.INFO));
				bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new HttpRequestDecoder());
						pipeline.addLast(new HttpResponseEncoder());
						pipeline.addLast(new HttpObjectAggregator(1048576));
						pipeline.addLast(new ChunkedWriteHandler());
						pipeline.addLast(executorGroup,new XHttpHandler());
					}
				});
				ChannelFuture future = bootstrap.bind(defaultHost,defaultPort).sync();
				future.channel().closeFuture().sync();
				
			}catch (Exception e){
				logger.error("XServer.start.Exception",e);
			} finally {
				stop();
			}
		}else{
			logger.error("XServer.start.failed:no init!!!");
		}
	}
	
	public void stop() {

		if(bossGroup!=null){
			bossGroup.shutdownGracefully();
		}
		if(workerGroup != null){
			workerGroup.shutdownGracefully();
		}
		if(executorGroup != null){
			executorGroup.shutdownGracefully();
		}
		isInit = false;
	}

}
