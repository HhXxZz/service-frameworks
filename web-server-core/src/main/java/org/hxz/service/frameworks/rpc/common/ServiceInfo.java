package org.hxz.service.frameworks.rpc.common;

import org.hxz.service.frameworks.base.ApiFactory;

import java.util.List;

/**
 * Created by hxz on 2021/9/24 10:35.
 */

public class ServiceInfo {

    private String instanceId; //nacos 实例id
    private String serverAddress;  //zk  node的地址
    private String product; //项目名称
    private String module;  //项目模块
    //private String serviceName;//节点服务名称
    //private String group = "example";
    private String serviceName; //项目名称
    private String host;
    private int port;
    private int weight;
    private List<ApiFactory.ApiInfo> apiList;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<ApiFactory.ApiInfo> getApiList() {
        return apiList;
    }

    public void setApiList(List<ApiFactory.ApiInfo> apiList) {
        this.apiList = apiList;
    }
}
