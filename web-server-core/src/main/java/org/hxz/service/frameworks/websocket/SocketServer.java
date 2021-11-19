package org.hxz.service.frameworks.websocket;

import org.hxz.service.frameworks.misc.Config;
import org.hxz.service.frameworks.socket.ws.CommonSocketIOServer;
import com.corundumstudio.socketio.AckMode;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hxz on 2021/6/22 10:48.
 * socket单例服务
 * 集群解决方案 netty-socket多节点+redis发布订阅通讯
 *
 *
 */

public enum  SocketServer {
    INSTANCE;

    public static final String EVENT_GROUP_MESSAGE = "EVENT_GROUP_MESSAGE";

    private static final Logger logger = LogManager.getLogger(SocketServer.class);
    private final AtomicBoolean mInitialed = new AtomicBoolean(false);


    private static final String HOST = Config.getProperty("PUSH.Host", "127.0.0.1");
    private static final int PORT = Config.getIntProperty("PUSH.Port", 9092);
    // 群聊房间
    public static final  String CHAT_GROUP_ROOM = "CHAT-GROUP-ROOM:%d";
    private CommonSocketIOServer mServer;
    // 用户UserTokenKey存储map
    private Map<Long, SocketIOClient> mUserClientMap;

    public void init(){
        if(mInitialed.compareAndSet(false,true)){
            logger.info("init");
            mServer = CommonSocketIOServer.create("IM-SERVICE", HOST, PORT, new SocketHandler(), "/im", null, AckMode.MANUAL);

            // 群聊相关
            mServer.on(EVENT_GROUP_MESSAGE, String.class, this::onGroupMessageEvent);

            mServer.start();
        }
    }


    private void onGroupMessageEvent(SocketIOClient pClient, String pData, AckRequest pAckRequest) {
        logger.info("onGroupMessageEvent:"+pData);
       // pAckRequest.sendAckData("aa");
       // pClient.getBroadcastOperations().sendEvent("EVENT_GROUP_MESSAGE", data);
        pClient.sendEvent(EVENT_GROUP_MESSAGE,"收拾收拾收拾收拾收拾收拾书");
    }


    public void release(){
        if(mInitialed.compareAndSet(true,false)){
            mServer.close();
        }
    }

}
