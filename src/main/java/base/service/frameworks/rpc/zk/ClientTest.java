package base.service.frameworks.rpc.zk;

import base.service.frameworks.rpc.client.ClientPool;
import base.service.frameworks.rpc.common.MessageRequest;
import base.service.frameworks.rpc.server.BaseRpcServer;
import base.service.frameworks.rpc.server.ServiceManager;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hxz on 2021/6/30 15:32.
 */

public class ClientTest {
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        String address = "127.0.0.1:2181,127.0.0.1:3181,127.0.0.1:4181";
        String product = "service";
        String business = "example";
        String host = "127.0.0.1";
        int port = 9896;

        //ApiFactory.INSTANCE.init(product,business,host,port,"base.service.frameworks.processor");

//        ServiceRegistry serviceRegistry = new ServiceRegistry(host,port,product,business,address);
//        BaseRpcServer rpcServer = new BaseRpcServer();
//        rpcServer.start(host,port);

        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(address,product,business);
        ClientPool clientPool = ServiceManager.INSTANCE.chooseClient(business,"/api/relation/info","1");
        MessageRequest request = new MessageRequest();
        request.setRequestId(UUID.randomUUID().toString());
        clientPool.syncTransport(request);

    }

}
