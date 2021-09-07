package base.service.frameworks.rpc.server;

import base.service.frameworks.rpc.client.ClientPool;
import base.service.frameworks.rpc.zk.ApiFactory;
import base.service.frameworks.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务管理
 * zk发现服务自动配置pool连接池
 * 实现负载均衡、权重降级
 *
 */

public enum  ServiceManager {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private static final int READ_TIME_OUT = 5000;

    //服务连接池  host:port->clientPool
    private final Map<String, ClientPool>clientPoolMap = new ConcurrentHashMap<>();

    //接口服务池 接口->serviceList
    private final Map<String, CopyOnWriteArrayList<ClientPool>>apiServiceListMap = new ConcurrentHashMap<>();

    private final AtomicBoolean updateApiListMap = new AtomicBoolean(false);
    //private final AtomicBoolean handlerDone = new AtomicBoolean(true);

    //计时增加
    private final AtomicInteger roundRobin = new AtomicInteger(0);

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition connected = lock.newCondition();
    private static final int CONNECT_TIME_OUT = 6; //连接超时时间


    private void signalAll() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean await() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * zk自动更新service，并且连接
     * @param serviceList
     */
    public void updateConnectedServer(List<String> serviceList) {
        if(serviceList.isEmpty()){
            clear();
        }else{
            for(String zkConnectStr:serviceList){
                update(zkConnectStr);
            }
            signalAll();
        }
    }


    /**
     * 根据zk更新clientPool的实例
     * @param zkConnectStr
     */
    private void update(String zkConnectStr) {
        ZkConnectInfo zkConnectInfo = GsonUtil.fromJson(zkConnectStr,ZkConnectInfo.class);
        String host = zkConnectInfo.getHost();
        int port = zkConnectInfo.getPort();

        String serviceKey = host+":"+port;
        ClientPool pool;
        if(!clientPoolMap.containsKey(serviceKey)){
            pool = new ClientPool(host,port,READ_TIME_OUT);
            clientPoolMap.put(serviceKey,pool);
        }else{
            pool = clientPoolMap.get(serviceKey);
        }
        for(String tempServiceKey:clientPoolMap.keySet()){
            //不相等说明当前的client已经被下线
            if(!tempServiceKey.equals(serviceKey)){
                //clientPoolMap.remove(tempServiceKey).close();
            }
        }

        //装配api对应的pool连接节点
        for(ApiFactory.ApiInfo apiInfo:zkConnectInfo.getServices()){
            String apiKey = buildApiKey(apiInfo.getModule(),apiInfo.getUrl(),apiInfo.getVersion());
            if(!apiServiceListMap.containsKey(apiKey)){
                CopyOnWriteArrayList<ClientPool>clientPoolList = new CopyOnWriteArrayList<>();
                clientPoolList.add(pool);
                System.out.println("=======put"+apiKey);
                apiServiceListMap.put(apiKey,clientPoolList);
            }else{
                apiServiceListMap.get(apiKey).addIfAbsent(pool);
            }
        }
        updateApiServiceListMap();
    }


    /**
     * 随机选择一个clientPool
     * @param module
     * @param action
     * @param version
     * @return
     */
    public ClientPool chooseClient(String module, String action, String version) {
        String apiKey = buildApiKey(module,action,version);
        CopyOnWriteArrayList<ClientPool> clientPoolList =apiServiceListMap.get(apiKey);
        if(clientPoolList == null){
            logger.error("don't have any clientPool !");
            return null;
        }
        int size = clientPoolList.size();
        while (size == 0){
            try {
                if(await()) {
                    clientPoolList = apiServiceListMap.get(apiKey);
                    size = clientPoolList.size();
                }
            }catch (InterruptedException e){
                logger.error("Waiting for available node is interrupted! ", e);
                logger.error("Can't connect any servers!");
                return null;
            }
        }

        for(int i = 0;i < size; i++){
            int index = (roundRobin.getAndAdd(1) + size) % size;
            ClientPool clientPool = clientPoolList.get(index);
            logger.info("client.closed = "+clientPool.isClosed() +", idles="+clientPool.getNumIdle() +", actives="+clientPool.getNumActive());
            if(clientPool.isClosed() && clientPool.getNumIdle() > 0){
                updateApiServiceListMap();
                //return null;
            }else {
                logger.info("return pool:size={},index={},obj={}",size,index,GsonUtil.toJson(apiServiceListMap.keySet()));
                apiServiceListMap.forEach((key,list)->{
                    list.forEach(pool->{
                        System.out.println("key:"+key+"\t"+pool.toString()+"\t"+pool.isClosed());
                    });
                });
                return clientPool;
            }
        }
        logger.error("don't have any clientPool !");
        return null;
    }


    /**
     * 判断当前的clientPool是否关闭，关闭则更新apiServiceListMap
     */
    private void updateApiServiceListMap(){
        if(updateApiListMap.compareAndSet(false,true)) {
            apiServiceListMap.forEach((key, list)->{
                apiServiceListMap.get(key).removeIf(ClientPool::isClosed);
                if(apiServiceListMap.get(key).isEmpty()){
                    apiServiceListMap.remove(key);
                }
            });
            clientPoolMap.forEach((key,pool)->{
                if(pool.isClosed()){
                    clientPoolMap.remove(key);
                }
            });
            updateApiListMap.compareAndSet(true,false);
        }
    }

    private String buildApiKey(String module, String action, String version){
        return module+"@"+action+"#"+version;
    }
    private void clear(){
        clientPoolMap.clear();
        apiServiceListMap.clear();
    }

    static class ZkConnectInfo{
        private String host;
        private int port;
        private List<ApiFactory.ApiInfo> services;

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

        public List<ApiFactory.ApiInfo> getServices() {
            return services;
        }

        public void setServices(List<ApiFactory.ApiInfo> services) {
            this.services = services;
        }
    }

}
