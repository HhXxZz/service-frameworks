package base.service.frameworks.rpc.zk;

import base.service.frameworks.rpc.server.ServiceManager;
import base.service.frameworks.utils.GsonUtil;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zk服务发现，根据zk的watch机制，当服务自动上线时能够识别
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private static final int ZK_SESSION_TIMEOUT = 5000;
    
    private final CountDownLatch latch = new CountDownLatch(1);

    private final String registryAddress;
    private final ZooKeeper zookeeper;
    
    private final String product;
    private final String business;

    public ServiceDiscovery(String registryAddress, String product, String business) {
        this.registryAddress = registryAddress;
        this.product = product;
        this.business = business;
        zookeeper = connectServer();
        if (zookeeper != null) {
            watchNode(zookeeper);
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
            logger.error("zookeeper connect server failed", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
        	String rootPath = "/" + this.product + "/" + this.business;
            List<String> serverList = zk.getChildren(rootPath, event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNode(zk);
                }
            });
            List<String> dataList = new ArrayList<>();
        	for (String server : serverList) {
                byte[] bytes = zk.getData(rootPath + "/" + server, false, null);
                dataList.add(new String(bytes));
        	}
            logger.info("Service discovery triggered updating connected server node, node data: {}", dataList);

        	ServiceManager.INSTANCE.updateConnectedServer(dataList);
        } catch (KeeperException | InterruptedException e) {
            logger.error("Service discovery failed", e);
        }
    }


    public void stop(){
        if(zookeeper!=null){
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                logger.error("zookeeper stop failed", e);
            }
        }
    }
}
