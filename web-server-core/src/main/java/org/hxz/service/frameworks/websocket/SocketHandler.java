package org.hxz.service.frameworks.websocket;

import org.apache.commons.lang3.StringUtils;
import org.hxz.service.frameworks.socket.ws.CommonSocketIOServer;
import org.hxz.service.frameworks.utils.GsonUtil;
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
        logger.info("handleNewClient:");

        String uid = pClient.getHandshakeData().getSingleUrlParam("uid");
        String token = pClient.getHandshakeData().getSingleUrlParam("token");
        pClient.set("uid", uid);
        pClient.set("token", token);
    }

    @Override
    public boolean authorizationEnabled() {
        logger.info("authorizationEnabled:");
        return true;
    }

    @Override
    public String parsePrincipal(SocketIOClient pClient) {
        logger.info("parsePrincipal:");
        return pClient.getHandshakeData().getSingleUrlParam("uid");
    }

    @Override
    public String parseCredentials(SocketIOClient pClient) {
        //logger.info("parseCredentials:");
        return pClient.getHandshakeData().getSingleUrlParam("token");
    }

    @Override
    public void releaseClient(SocketIOClient pClient) {
        logger.info("releaseClient:");
    }

    @Override
    public boolean isAuthorized(HandshakeData data) {
        //logger.info("isAuthorized:"+GsonUtil.toJson(data));

        String uid = data.getSingleUrlParam("uid");
        String token = data.getSingleUrlParam("token");
        if(StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(token)){
            return true;
        }
        return false;
    }
}
