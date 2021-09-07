package rpc;

import base.service.frameworks.rpc.server.BaseRpcServer;
import base.service.frameworks.rpc.zk.ApiFactory;
import base.service.frameworks.rpc.zk.ServiceRegistry;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hxz on 2021/6/30 15:32.
 */

public class Server1 {
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 9898;
        BaseRpcServer rpcServer = new BaseRpcServer(host,port);
        rpcServer.start();



    }

}
