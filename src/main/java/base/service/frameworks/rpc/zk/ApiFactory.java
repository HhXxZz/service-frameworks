package base.service.frameworks.rpc.zk;

import base.service.frameworks.processor.BaseTask;
import base.service.frameworks.processor.annotation.API;
import base.service.frameworks.utils.ClassScanner;
import base.service.frameworks.utils.GsonUtil;

import java.util.*;


public enum  ApiFactory {
	INSTANCE;

	public static final String GET = "GET";
	public static final String POST = "POST";

	private String product;
	private String module;
	private String host;
	private int port;

	//接口MAP  module-><url,class>
	public Map<String, Map<String, Class<?>>> ROUTE_MAP = new HashMap<>();


    public void init(String product, String module, String host, int port,String... pPackage){
    	this.product = product;
		this.module = module;
		this.host = host;
		this.port = port;

		scanApi(pPackage);
    }

	/**
	 * 扫描所有的Api接口
	 * @param pPackage
	 */
	private void scanApi(String... pPackage) {
		if (pPackage != null && pPackage.length > 0) {
			Arrays.stream(pPackage).forEach(packageName -> {
				List<Class<? extends BaseTask>> apis = ClassScanner.in(packageName).withAnnotation(API.class).scanInherited(BaseTask.class);
				apis.forEach(clazz -> {
					API api = clazz.getAnnotation(API.class);
					if(ROUTE_MAP.containsKey(module)){
						ROUTE_MAP.get(module).put(api.uri(),clazz);
					}else{
						Map<String,Class<?>>tempMap = new HashMap<>();
						tempMap.put(api.uri(),clazz);
						ROUTE_MAP.put(module,tempMap);
					}
				});
			});
		}
	}

	public static void main(String[] args) {
		ApiFactory.INSTANCE.init("ex","product","127.0.0.1",9896,"base.service.frameworks.processor");


		System.out.println(GsonUtil.toJson(ApiFactory.INSTANCE.ROUTE_MAP.toString()));
	}

    public String getProduct(){
    	return product;
    }
    
    public String getBusiness(){
    	return module;
    }

    public String getHost(){
    	return host;
    }

    public int getPort(){
    	return port;
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
	public String getUrlData(){
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
		Map<String,Object> data = new HashMap<>();
		data.put("host", host);
		data.put("port", port);
		data.put("services", apiList);
		return GsonUtil.toJson(data);

	}

    
//	public static Map<String, Class<?>> getActionList(Class<?>[] clazzList) {
//		ActionConfig actionConfig = null;
//		Class<?> clazz = null;
//		Map<String, Class<?>> actionList = new HashMap<String, Class<?>>();
//		for (int i = 0; i < clazzList.length; i++) {
//			clazz = clazzList[i];
//			actionConfig = clazz.getAnnotation(ActionConfig.class);
//			actionList.put(actionConfig.name(), clazz);
//		}
//		return actionList;
//	}





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
