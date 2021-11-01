package org.hxz.service.frameworks.base;

import org.hxz.service.frameworks.processor.BaseTask;
import org.hxz.service.frameworks.processor.annotation.API;
import org.hxz.service.frameworks.rpc.common.ServiceInfo;
import org.hxz.service.frameworks.utils.ClassScanner;

import java.util.*;


public enum  ApiFactory {
	INSTANCE;

	public static final String GET = "GET";
	public static final String POST = "POST";

	private ServiceInfo serviceInfo;

	//接口MAP  module-><url,class>
	public Map<String, Map<String, Class<?>>> ROUTE_MAP = new HashMap<>();

    public void init(ServiceInfo serviceInfo,String... pPackage){
    	this.serviceInfo = serviceInfo;
		scanApi(pPackage);
    }

	/**
	 * 扫描所有的Api接口
	 * @param pPackage 接口包路径
	 */
	private void scanApi(String... pPackage) {
		if (pPackage != null && pPackage.length > 0) {
			Arrays.stream(pPackage).forEach(packageName -> {
				List<Class<? extends BaseTask>> apis = ClassScanner.in(packageName).withAnnotation(API.class).scanInherited(BaseTask.class);
				apis.forEach(clazz -> {
					API api = clazz.getAnnotation(API.class);
					if(ROUTE_MAP.containsKey(serviceInfo.getServiceName())){
						ROUTE_MAP.get(serviceInfo.getServiceName()).put(api.uri(),clazz);
					}else{
						Map<String,Class<?>>tempMap = new HashMap<>();
						tempMap.put(api.uri(),clazz);
						ROUTE_MAP.put(serviceInfo.getServiceName(),tempMap);
					}
				});
			});
		}
	}

	public static void main(String[] args) {

	}


	public Class<?> getLogicClass(String module, String url) {
		if(ROUTE_MAP.containsKey(module)){
			return ROUTE_MAP.get(module).get(url);
		}
		return null;
	}


	/**
	 * zk服务端注册的节点信息
	 * host:port->{url:class}
	 * @return
	 */
	public List<ApiInfo> getApiList(){
		Map<String, Class<?>> actionList;
		Class<?> clazz;
		API api;
		ApiInfo apiInfo;
		List<ApiInfo> apiList = new ArrayList<>();
		for(String module : ROUTE_MAP.keySet()){
			actionList = ROUTE_MAP.get(module);
			for (String url : actionList.keySet()) {
				clazz = actionList.get(url);
				api = clazz.getAnnotation(API.class);
				apiInfo = new ApiInfo();
				apiInfo.setModule(module);
				apiInfo.setUrl(url);
				apiInfo.setName(api.name());
				apiInfo.setVersion(api.version());
				apiInfo.setMethod(api.method());
				apiInfo.setEnableToken(api.enableToken());
				apiInfo.setEnableMember(api.enableMember());
				apiInfo.setEnableAuthentication(api.enableAuthentication());
				apiInfo.setEnableEncryption(api.enableEncryption());
				apiList.add(apiInfo);
			}
		}
		return apiList;

	}


	public static class ApiInfo {
		private String module;
		private String url;
		private String name;
		private Boolean enableToken;
		private Boolean enableMember;
		private Boolean enableAuthentication;
		private Boolean enableEncryption;
		private String method;
		private String version;


		public ApiInfo(){}

		public String getModule() {
			return module;
		}

		public void setModule(String module) {
			this.module = module;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getEnableToken() {
			return enableToken;
		}

		public void setEnableToken(Boolean enableToken) {
			this.enableToken = enableToken;
		}

		public Boolean getEnableMember() {
			return enableMember;
		}

		public void setEnableMember(Boolean enableMember) {
			this.enableMember = enableMember;
		}

		public Boolean getEnableAuthentication() {
			return enableAuthentication;
		}

		public void setEnableAuthentication(Boolean enableAuthentication) {
			this.enableAuthentication = enableAuthentication;
		}

		public Boolean getEnableEncryption() {
			return enableEncryption;
		}

		public void setEnableEncryption(Boolean enableEncryption) {
			this.enableEncryption = enableEncryption;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}



}
