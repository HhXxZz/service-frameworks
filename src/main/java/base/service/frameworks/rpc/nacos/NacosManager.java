package base.service.frameworks.rpc.nacos;

import base.service.frameworks.rpc.common.ServiceInfo;
import base.service.frameworks.rpc.server.ServiceManager;
import base.service.frameworks.utils.GsonUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hxz on 2021/9/24 15:03.
 */

public enum  NacosManager {
    INSTANCE;


    private static final String KEY_DATA = "data";
    private final AtomicBoolean init = new AtomicBoolean(false);


    private NamingService namingService;

    public void init(String serverAddress){
        try {
            if(init.compareAndSet(false,true)) {
                namingService = NamingFactory.createNamingService(serverAddress);
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }


    /**
     * 注册实例
     */
    public void register(ServiceInfo serviceInfo){
        if(init.get()) {
            try {
                Instance instance = new Instance();
                instance.setInstanceId(UUID.randomUUID().toString());
                instance.setIp(serviceInfo.getHost());
                instance.setPort(serviceInfo.getPort());
                instance.setHealthy(true);
                instance.setWeight(1);
                Map<String, String> instanceMeta = new HashMap<>();
                instanceMeta.put(KEY_DATA, GsonUtil.toJson(serviceInfo));
                instance.setMetadata(instanceMeta);
                namingService.registerInstance(serviceInfo.getServiceName(), instance);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 注销实例
     * @param serviceInfo 实例配置信息
     */
    public void deregisterInstance(ServiceInfo serviceInfo){
        if(init.get()) {
            try {
                namingService.deregisterInstance(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort());
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 网关根据服务名称监听服务上下线
     * @param serviceNameList 服务名称list
     */
    public void subscribe(List<String> serviceNameList){
        if(init.get()) {
            try {
                for(String serviceName : serviceNameList) {
                    namingService.subscribe(serviceName, event -> {
                        if (event instanceof NamingEvent) {
                            System.out.println("subscribe");
                            List<Instance> instances = ((NamingEvent) event).getInstances();
                            if (instances.isEmpty()) {
                                ServiceManager.INSTANCE.clear();
                            }
                            for (Instance instance : instances) {
                                System.out.println(instance.getMetadata().get(KEY_DATA));
                                ServiceInfo serviceInfo = GsonUtil.fromJson(instance.getMetadata().get(KEY_DATA), ServiceInfo.class);
                                serviceInfo.setInstanceId(instance.getInstanceId());

                                ServiceManager.INSTANCE.updateConnectedServer(serviceInfo);
                            }
                        }
                    });

                    namingService.unsubscribe(serviceName, event -> {
                        if (event instanceof NamingEvent) {
                            System.out.println("unsubscribe");
                            List<Instance> instances = ((NamingEvent) event).getInstances();
                            if (instances.isEmpty()) {
                                ServiceManager.INSTANCE.clear();
                            }
                            for (Instance instance : instances) {
                                System.out.println(instance.getMetadata().get(KEY_DATA));
                                ServiceManager.INSTANCE.updateConnectedServer(GsonUtil.fromJson(instance.getMetadata().get(KEY_DATA), ServiceInfo.class));
                            }
                        }
                    });
                }
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }


    public Instance getInstance(String serviceName) {
        if(init.get()) {
            try {
                return namingService.selectOneHealthyInstance(serviceName);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ServiceInfo getServiceInfo(Instance instance){
        return GsonUtil.fromJson(instance.getMetadata().get(KEY_DATA),ServiceInfo.class);
    }



}
