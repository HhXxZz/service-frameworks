package base.service.frameworks.rpc.server;

import base.service.frameworks.rpc.client.ClientPool;
import base.service.frameworks.rpc.common.ServiceInfo;
import base.service.frameworks.rpc.nacos.NacosManager;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务管理
 * nacos发现服务自动配置pool连接池
 * nacos实现负载均衡、权重降级
 *
 */

public enum  ServiceManager {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private static final int READ_TIME_OUT = 5000;

    //服务连接池  instanceId -> clientPool
    private final Map<String, ClientPool>clientPoolMap = new ConcurrentHashMap<>();


    /**
     * zk自动更新service，并且连接
     */
    public void updateConnectedServer(ServiceInfo serviceInfo) {
        update(serviceInfo);
    }


    /**
     * 根据zk更新clientPool的实例
     */
    private void update(ServiceInfo serviceInfo) {
        String host = serviceInfo.getHost();
        int port = serviceInfo.getPort();
        String instanceId = serviceInfo.getInstanceId();
        ClientPool pool;
        if(!clientPoolMap.containsKey(instanceId)){
            pool = new ClientPool(host,port,READ_TIME_OUT);
            clientPoolMap.put(instanceId,pool);
        }else{
            pool = clientPoolMap.get(instanceId);
        }

        if(pool.isInvalid()){
            clientPoolMap.remove(instanceId);
        }
    }

    public void clear(){
        clientPoolMap.clear();
    }


    public ClientPool chooseClient(String serviceName) {
        Instance instance = NacosManager.INSTANCE.getInstance(serviceName);
        if(instance == null){
            logger.error("获取instance失败");
            return null;
        }
        ServiceInfo serviceInfo = NacosManager.INSTANCE.getServiceInfo(instance);
        if(serviceInfo == null){
            logger.error("获取instance serviceInfo失败");
            return null;
        }
        String instanceId = instance.getInstanceId();
        if(instanceId == null){
            logger.error("获取instanceId失败");
            return null;
        }
        ClientPool clientPool = clientPoolMap.get(instanceId);
        if(clientPool.isInvalid()){
            NacosManager.INSTANCE.deregisterInstance(serviceInfo);
            logger.error("连接实例已失效");
            return null;
        }
        return clientPool;
    }


}
