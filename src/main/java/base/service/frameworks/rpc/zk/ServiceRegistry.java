package base.service.frameworks.rpc.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private static final int ZK_SESSION_TIMEOUT = 5000;
    
    private final CountDownLatch latch = new CountDownLatch(1);

    private final String registryAddress;

    private String product;
    private String module;
    private String host;
    private int port;

    public ServiceRegistry(String host,int port,String product,String module,String registryAddress) {
        this.registryAddress = registryAddress;
        this.product = product;
        this.module = module;
        this.host = host;
        this.port = port;
    }

    public void register() {
        ZooKeeper zk = connectServer();
        if (zk != null) {
            addRootNode(zk);
            createChildNode(zk);
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, ZK_SESSION_TIMEOUT, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            logger.error("", e);
        }
        return zk;
    }

    private void addRootNode(ZooKeeper zk){
        try {
        	String rootPath = "/" + product;
            Stat s = zk.exists(rootPath, false);
            if (s == null) {
                zk.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            rootPath = rootPath + "/" + module;
            s = zk.exists(rootPath, false);
            if (s == null) {
                zk.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException e) {
            logger.error(e.toString());
        }
    }

    private void createChildNode(ZooKeeper zk) {
        try {
        	String nodePath = "/" + product + "/" + module + "/" + host+":"+ port;
            byte[] bytes = ApiFactory.INSTANCE.getUrlData().getBytes();
            zk.create(nodePath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException | InterruptedException e) {
            logger.error("", e);
        }
    }
}