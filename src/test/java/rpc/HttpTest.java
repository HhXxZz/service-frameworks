package rpc;

import base.service.frameworks.rpc.zk.ServiceDiscovery;

/**
 * Created by hxz on 2021/7/6 17:01.
 */

public class HttpTest {

    public static void main(String[] args) {


        XHttpServer server = new XHttpServer();
        server.init(null);
        server.start();

    }

}
