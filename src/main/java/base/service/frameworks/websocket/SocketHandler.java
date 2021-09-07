package base.service.frameworks.websocket;

import base.service.frameworks.socket.ws.CommonSocketIOServer;
import base.service.frameworks.utils.GsonUtil;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hxz on 2021/6/22 11:21.
 */

public class SocketHandler implements CommonSocketIOServer.CommonSocketIOHandler {

    private static final Logger logger = LogManager.getLogger(SocketHandler.class);

    @Override
    public void handleNewClient(SocketIOClient pClient) {
        logger.info("handleNewClient:"+ GsonUtil.toJson(pClient.getHandshakeData()));
    }

    @Override
    public boolean authorizationEnabled() {
        logger.info("authorizationEnabled:");
        return false;
    }

    @Override
    public String parsePrincipal(SocketIOClient pClient) {
        logger.info("parsePrincipal:"+ GsonUtil.toJson(pClient.getHandshakeData()));
        return null;
    }

    @Override
    public String parseCredentials(SocketIOClient pClient) {
        logger.info("parseCredentials:"+ GsonUtil.toJson(pClient.getHandshakeData()));
        return null;
    }

    @Override
    public void releaseClient(SocketIOClient pClient) {
        logger.info("releaseClient:"+ GsonUtil.toJson(pClient.getHandshakeData()));
    }

    @Override
    public boolean isAuthorized(HandshakeData data) {
        logger.info("isAuthorized:"+ GsonUtil.toJson(data));
        return false;
    }
}
