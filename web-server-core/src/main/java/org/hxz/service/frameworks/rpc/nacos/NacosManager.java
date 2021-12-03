package org.hxz.service.frameworks.rpc.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hxz.service.frameworks.rpc.common.ServiceInfo;
import org.hxz.service.frameworks.rpc.server.ServiceManager;
import org.hxz.service.frameworks.utils.GsonUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hxz on 2021/9/24 15:03.
 */

public enum  NacosManager {
    INSTANCE;


    private static final String KEY_DATA = "data";
    private final AtomicBoolean init = new AtomicBoolean(false);


    private NamingService namingService;
    private ConfigService configService;

    public void init(String serverAddress){
        try {
            if(init.compareAndSet(false,true)) {
                configService = NacosFactory.createConfigService(serverAddress);
                namingService = NamingFactory.createNamingService(serverAddress);
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

//
//    /**
//     * server
//     */
//    public void publishConfig(String dataId,String group,String content){
//        if(init.get()) {
//            try {
//                List<String> serviceNameList = getConfigServiceNameList(dataId,group);
//                if(serviceNameList.contains(content)){
//                    throw new RuntimeException("serviceName exist!");
//                }else{
//                    serviceNameList.add(content);
//                }
//                configService.publishConfig(dataId,group,GsonUtil.toJson(serviceNameList));
//            }catch (NacosException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * server
//     */
//    public void removeConfig(String dataId,String group){
//        if(init.get()) {
//            try {
//                configService.removeConfig(dataId,group);
//            }catch (NacosException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * server
//     */
//    public List<String> getConfigServiceNameList(String dataId,String group){
//        if(init.get()) {
//            try {
//                String content = configService.getConfig(dataId,group,5000);
//                if(StringUtils.isBlank(content)){
//                    return Lists.newArrayList();
//                }
//                return GsonUtil.listFromJson(content,String[].class);
//            }catch (NacosException e) {
//                e.printStackTrace();
//            }
//        }
//        return Lists.newArrayList();
//    }
//
//
//    public String getNewServiceName(String dataId,String group){
//        int index = 0;
//        String serviceName = group + "-%s";
//        if(init.get()) {
//            try {
//                List<String> serviceNameList = getConfigServiceNameList(dataId,group);
//                if (!serviceNameList.isEmpty()) {
//                    index = Integer.parseInt(serviceNameList.get(serviceNameList.size() - 1).split("-")[1]);
//                    index += 1;
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return String.format(serviceName,index);
//    }
//
//    /**
//     * gateway
//     */
//    public void configListener(String dataId,List<String> groupList){
//        if(init.get()) {
//            try {
//                for(String group : groupList) {
//                    configService.addListener(dataId, group, new Listener() {
//                        @Override
//                        public Executor getExecutor() {
//                            return null;
//                        }
//
//                        @Override
//                        public void receiveConfigInfo(String s) {
//                            System.out.println("==configListener"+s);
//                            //动态注册key
//                            List<String> serviceNameList = GsonUtil.listFromJson(s, String[].class);
//                            subscribe(serviceNameList);
//                        }
//                    });
//                }
//            }catch (NacosException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    /**
     * server
     * 注册实例
     */
    public void register(ServiceInfo serviceInfo){
        if(init.get()) {
            try {
                //publishConfig(serviceInfo.getProduct(),serviceInfo.getModule(),serviceInfo.getServiceName());
                Instance instance = new Instance();
                instance.setInstanceId(UUID.randomUUID().toString());
                instance.setIp(serviceInfo.getHost());
                instance.setPort(serviceInfo.getPort());
                instance.setHealthy(true);
                instance.setWeight(serviceInfo.getWeight());
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
                            System.out.println("subscribe:"+serviceName);
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
