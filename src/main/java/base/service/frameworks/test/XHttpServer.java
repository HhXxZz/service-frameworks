package base.service.frameworks.test;


import base.service.frameworks.base.BaseServer;
import base.service.frameworks.rpc.common.ServiceInfo;
import base.service.frameworks.rpc.nacos.NacosManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class XHttpServer extends BaseServer {

	private int defaultPort = 8686;
	private String defaultHost = "0.0.0.0";

	@Override
	protected void release() {

	}

	@Override
	protected void init() {
		//defaultPort = Integer.parseInt(properties.getProperty("service.server.port").trim());


		//String address = "127.0.0.1:2181,127.0.0.1:3181,127.0.0.1:4181";
		String address = "127.0.0.1:8848";
		String serviceName = "service";
		String business = "example";


		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setServerAddress(address);
		serviceInfo.setServiceName(serviceName);
//		serviceInfo.setProduct(product);
//		serviceInfo.setModule(business);


		NacosManager.INSTANCE.init(address);

		List<String> serviceNameList = new ArrayList<>();
		serviceNameList.add(serviceName);
		NacosManager.INSTANCE.subscribe(serviceNameList);
	}

	@Override
	protected void initCustomChannel(ChannelPipeline pPipe) {

	}



}
