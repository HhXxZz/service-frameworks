package base.service.frameworks.test;

import base.service.frameworks.rpc.server.BaseRpcServer;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hxz on 2021/6/30 15:32.
 */

public class Server3 {
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 9888;
        BaseRpcServer rpcServer = new BaseRpcServer(host,port);
        rpcServer.start();



    }

}
