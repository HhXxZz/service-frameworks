package org.hxz.service.frameworks.test;


import org.hxz.service.frameworks.base.BaseServer;
import org.hxz.service.frameworks.rpc.common.ServiceInfo;
import org.hxz.service.frameworks.rpc.nacos.NacosManager;
import io.netty.channel.*;

import java.util.ArrayList;
import java.util.List;


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
