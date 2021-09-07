package base.service.frameworks.rpc.client;


import base.service.frameworks.rpc.callback.CallFuture;
import base.service.frameworks.rpc.common.MessageRequest;
import base.service.frameworks.rpc.common.MessageResponse;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hxz on 2021/7/2 10:14.
 */

public class  ClientPool {

    private static final Logger logger = LoggerFactory.getLogger(ClientPool.class);

    private static final int MAX_IDLE = 20;
    private static final int MAX_TOTAL = 100;
    private static final int MIN_IDLE = 5;

    private final GenericObjectPool<ClientChannel> objectPool;
    private final int readTimeout;
    private String id;

    public ClientPool(String host, int port,int readTimeout) {
        this.id = UUID.randomUUID().toString();
        this.readTimeout = readTimeout;

        ClientFactory client = new ClientFactory(host, port);
        //设置对象池的相关参数
        GenericObjectPoolConfig<ClientChannel> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(MAX_IDLE);
        poolConfig.setMaxTotal(MAX_TOTAL);
        poolConfig.setMinIdle(MIN_IDLE);
        //新建一个对象池,传入对象工厂和配置
        objectPool = new GenericObjectPool<>(client, poolConfig);

        //first init one connection
        ClientChannel channel = getObject();
        returnResourceObject(channel);
    }

    /**
     * 同步返回CallFuture
     * @param request
     * @return
     */
    public CallFuture<MessageResponse> asyncTransport(MessageRequest request) {
        ClientChannel channel = getObject();
        try {
            if(channel == null){
                return null;
            }
            return channel.asyncTransport(request, readTimeout);
        } catch (Exception e) {
            logger.error("asyncTransport failed, " + e.getMessage(), e);
            returnBrokenResourceObject(channel);
        } finally {
            returnResourceObject(channel);
        }
        return null;
    }


    /**
     * 异步返回 结果
     * @param request
     * @return
     */
    public MessageResponse syncTransport(MessageRequest request) {
        try {
            CallFuture<MessageResponse> future = asyncTransport(request);
            if (future != null) {
                return future.get();
            }
            return null;
        } catch (Exception e) {
            logger.error("syncTransport failed, " + e.getMessage(), e);
        }
        return null;
    }

    public ClientChannel getObject(){
        try {
            return objectPool.borrowObject();
        }catch (Exception e){
            logger.error("Could not get a resource from the pool", e);
        }
        return null;
    }


    /**
     * 返回对象到池中
     *
     * @param resource
     */
    public void returnResourceObject(ClientChannel resource) {
        try {
            objectPool.returnObject(resource);
        } catch (Exception e) {
            logger.error("Could not return the resource to the pool", e);
        }
    }


    /**
     * 返回一个调用失败的对象到池中
     *
     * @param resource
     */
    public void returnBrokenResourceObject(final ClientChannel resource) {
        try {
            objectPool.invalidateObject(resource);
        } catch (Exception e) {
            logger.error("Could not return the resource to the pool", e);
        }
    }


    public void close(){
        try {
            objectPool.close();
        } catch (Exception e) {
            logger.error("close error", e);
        }
    }

    public boolean isClosed(){
        try {
            return objectPool.isClosed();
        } catch (Exception e) {
            logger.error("get isClose error", e);
        }
        return false;
    }


    public int getNumActive(){
        return objectPool.getNumActive();
    }

    public int getNumIdle(){
        return objectPool.getNumIdle();
    }

    @Override
    public String toString() {
        return "ClientPool{" +
                this.id+
                '}';
    }
}
