package org.hxz.service.frameworks.socket.ws;

import org.hxz.service.frameworks.processor.BaseTaskPool;
import org.hxz.service.frameworks.utils.GsonUtil;
import org.hxz.service.frameworks.utils.RegexUtil;
import org.hxz.service.frameworks.utils.StringUtil;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.store.StoreFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.netty.channel.epoll.Epoll;
import io.netty.util.internal.PlatformDependent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * Created by someone on 2019-04-09.
 *
 * </pre>
 */
@SuppressWarnings({"WeakerAccess", "unused", "FieldCanBeLocal"})
public class CommonSocketIOServer {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(CommonSocketIOServer.class);

    public static final String PROPERTY_PRINCIPAL   = "_PRINCIPAL_";
    public static final String PROPERTY_CREDENTIALS = "_CREDENTIALS_";
    public static final String PROPERTY_IP          = "_IP_";

    public static final String EVENT_LOGOUT = "logout";

    // ===========================================================
    // Fields
    // ===========================================================
    private final String                      mServerName;
    private final Configuration               mConfiguration;
    private final SocketIOServer              mServer;
    private final List<String>                mNamespaces;
    private final Map<String, SocketIOClient> mClientSessions;
    private final SocketIOHandlerWrapper      mHandler;
    private final BaseTaskPool mPool;

    // ===========================================================
    // Constructors
    // ===========================================================
    private CommonSocketIOServer(String pServerName, String pHost, int pPort, CommonSocketIOHandler pHandler, String pContextPath, StoreFactory pStoreFactory, AckMode ackMode) {
        this.mServerName     = pServerName;
        this.mHandler        = new SocketIOHandlerWrapper(pHandler);
        this.mConfiguration  = generateConfiguration(pServerName, pHost, pPort, pHandler, pContextPath, pStoreFactory, ackMode);
        this.mServer         = new SocketIOServer(mConfiguration);
        this.mNamespaces     = new ArrayList<>();
        this.mClientSessions = new ConcurrentHashMap<>();
        this.mPool           = new BaseTaskPool("BASE-PUSH");
        attachServerListener();
    }

    public static CommonSocketIOServer create(String pHost, int pPort) {
        return create("No-Name", pHost, pPort, null, null, null, null);
    }

    public static CommonSocketIOServer create(String pServerName, String pHost, int pPort) {
        return create(pServerName, pHost, pPort, null, null, null, null);
    }

    public static CommonSocketIOServer create(String pServerName, String pHost, int pPort, CommonSocketIOHandler pHandler) {
        return create(pServerName, pHost, pPort, pHandler, null, null, null);
    }

    public static CommonSocketIOServer create(String pServerName, String pHost, int pPort, CommonSocketIOHandler pHandler, AckMode ackMode) {
        return create(pServerName, pHost, pPort, pHandler, null, null, ackMode);
    }

    public static CommonSocketIOServer create(String pServerName, String pHost, int pPort, String pContextPath) {
        return create(pServerName, pHost, pPort, null, pContextPath, null, null);
    }

    public static CommonSocketIOServer create(String pServerName, String pHost, int pPort, CommonSocketIOHandler pHandler, String pContextPath) {
        return create(pServerName, pHost, pPort, pHandler, pContextPath, null, null);
    }

    public static CommonSocketIOServer create(String pServerName, String pHost, int pPort, CommonSocketIOHandler pHandler, String pContextPath, StoreFactory pStoreFactory, AckMode ackMode) {
        if (!RegexUtil.isIPAddress(pHost)) {
            throw new RuntimeException("invalid host[" + pHost + "]");
        }
        if (pPort < 1 || pPort > 65535) {
            throw new RuntimeException("invalid port[" + pPort + "] (1-65535)");
        }
        if (pHandler == null) {
            LOG.warn("Create server[{}] with default handler", pServerName);
        }
        return new CommonSocketIOServer(pServerName, pHost, pPort, pHandler, pContextPath, pStoreFactory, ackMode);
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================
    public SocketIOClient getClient(String pPrincipal) {
        return mClientSessions.get(pPrincipal);
    }

    public boolean isOnline(String pPrincipal) {
        return mClientSessions.containsKey(pPrincipal) && mClientSessions.get(pPrincipal).isChannelOpen();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public void start() {
        if (mServer != null) {
            LOG.debug("[{}] started http://{}:{} namespaces{}",
                    mServerName,
                    this.mConfiguration.getHostname(),
                    this.mConfiguration.getPort(),
                    GsonUtil.toJson(this.mNamespaces)
            );
            mPool.init();
            mServer.start();
        }
    }

    public void close() {
        if (mServer != null) {
            LOG.debug("[{}] started http://{}:{} namespaces{}",
                    mServerName,
                    this.mConfiguration.getHostname(),
                    this.mConfiguration.getPort(),
                    GsonUtil.toJson(this.mNamespaces)
            );
            mServer.stop();
            mPool.release();
        }
    }

    public <T> void on(String pEventName, Class<T> eventClass, DataListener<T> listener) {
        if (mServer != null) {
            LOG.debug("[{}] handle event [{}]", mServerName, pEventName);
            mServer.addEventListener(pEventName, eventClass, listener);
        }
    }

    public <T> void off(String pEventName) {
        if (mServer != null) {
            LOG.debug("[{}] ignore event [{}]", mServerName, pEventName);
            mServer.removeAllListeners(pEventName);
        }
    }

    public void createNamespaces(String... pNamespaces) {
        if (mServer != null && pNamespaces != null && pNamespaces.length > 0) {
            for (String namespace : pNamespaces) {
                namespace = namespace.startsWith("/") ? namespace : ("/" + namespace);
                if (!mNamespaces.contains(namespace)) {
                    LOG.debug("[{}] create namespace [{}]", mServerName, namespace);
                    mNamespaces.add(namespace);
                    mServer.addNamespace(namespace);
                }
            }
        }
    }

    public void removeNamespace(String pNamespace) {
        if (mServer != null) {
            pNamespace = pNamespace.startsWith("/") ? pNamespace : ("/" + pNamespace);
            if (mNamespaces.contains(pNamespace)) {
                LOG.debug("[{}] remove namespace [{}]", mServerName, pNamespace);
                mServer.removeNamespace(pNamespace);
                mNamespaces.remove(pNamespace);
            }
        }
    }

    public Collection<SocketIOClient> getClients(String pNamespace, String pRoom) {
        if (!StringUtil.isEmpty(pNamespace)) {
            pNamespace = pNamespace.startsWith("/") ? pNamespace : ("/" + pNamespace);
            if (mNamespaces.contains(pNamespace)) {
                SocketIONamespace namespace = mServer.getNamespace(pNamespace);
                if (namespace != null) {
                    if (!StringUtil.isEmpty(pRoom)) {
                        return namespace.getRoomOperations(pRoom).getClients();
                    }
                } else {
                    mNamespaces.remove(pNamespace);
                }
            }
        }
        if (!StringUtil.isEmpty(pRoom) && mServer.getRoomOperations(pRoom) != null) {
            return mServer.getRoomOperations(pRoom).getClients();
        }
        return null;
    }

    public void broadcast(String pNamespace, String pRoom, String pEvent, Object... pDatas) {
        if (!StringUtil.isEmpty(pNamespace)) {
            pNamespace = pNamespace.startsWith("/") ? pNamespace : ("/" + pNamespace);
            if (mNamespaces.contains(pNamespace)) {
                SocketIONamespace namespace = mServer.getNamespace(pNamespace);
                if (namespace != null) {
                    if (!StringUtil.isEmpty(pRoom)) {
                        namespace.getRoomOperations(pRoom).sendEvent(pEvent, pDatas);
                    } else {
                        namespace.getBroadcastOperations().sendEvent(pEvent, pDatas);
                    }
                    return;
                } else {
                    mNamespaces.remove(pNamespace);
                }
            }
            LOG.warn("[{}] namespace[{}] not found. Cancel broadcasting {}", mServerName, pNamespace, GsonUtil.toJson(pDatas));
            return;
        }
        if (!StringUtil.isEmpty(pRoom) && mServer.getRoomOperations(pRoom) != null) {
            mServer.getRoomOperations(pRoom).sendEvent(pEvent, pDatas);
        } else {
            mServer.getBroadcastOperations().sendEvent(pEvent, pDatas);
        }
    }

    public void send(String pPrincipal, String pEvent, Object... pData) {
        send(pPrincipal, pEvent, null, pData);
    }

    public void send(String pPrincipal, String pEvent, AckCallback<?> ackCallback, Object... pDatas) {
        if (mClientSessions.containsKey(pPrincipal)) {
            if (ackCallback != null) {
                mClientSessions.get(pPrincipal).sendEvent(pEvent, ackCallback, pDatas);
            } else {
                mClientSessions.get(pPrincipal).sendEvent(pEvent, pDatas);
            }
        }
    }

    private static Configuration generateConfiguration(String pServerName, String pHost, int pPort, CommonSocketIOHandler pHandler, String pContextPath, StoreFactory pStoreFactory, AckMode ackMode) {
        Configuration config = new Configuration();
        config.setUseLinuxNativeEpoll(!PlatformDependent.isWindows() && Epoll.isAvailable());
        config.setHostname(pHost);
        config.setPort(pPort);
        config.setOrigin("*");
        config.setTransports(Transport.WEBSOCKET);
        config.setPingTimeout(35000);
        config.setPingInterval(15000);
        if (ackMode != null) {
            config.setAckMode(ackMode);
        }
        if (!StringUtil.isEmpty(pContextPath)) {
            config.setContext(pContextPath);
        }
        if (pStoreFactory != null) {
            LOG.debug("[{}] custom store factory [{}]", pServerName, pStoreFactory.getClass().getSimpleName());
            config.setStoreFactory(pStoreFactory);
        }
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        if (pHandler != null && pHandler.authorizationEnabled()) {
            LOG.debug("[{}] enable authorization", pServerName);
            config.setAuthorizationListener(pHandler);
        }
        return config;
    }

    private void attachServerListener() {
        if (mServer != null) {
            mServer.addConnectListener(this::onConnected);
            mServer.addDisconnectListener(this::onDisconnected);
        }
    }

    protected void onConnected(SocketIOClient pClient) {
        LOG.info("onConnected");
        HandshakeData data = pClient.getHandshakeData();
        String principal   = mHandler.parsePrincipal(pClient);
        String credentials = mHandler.parseCredentials(pClient);
        String sessionID   = pClient.getSessionId().toString();
        String namespace   = pClient.getNamespace().getName();

        if (StringUtil.isEmpty(principal)) {
            // 无法获取到具体身份，断开连接
            LOG.info("Forbidden");
            pClient.disconnect();
            return;
        }

        LOG.debug("[{}] connected principal[{}] session[{}] namespace[{}] query {}",
                mServerName, principal, sessionID, namespace,
                GsonUtil.toJson(pClient.getHandshakeData().getUrlParams()));

        pClient.set(PROPERTY_PRINCIPAL, principal);
        pClient.set(PROPERTY_CREDENTIALS, credentials);
        pClient.set(PROPERTY_IP, getClientRealIP(pClient));

        if (!mClientSessions.containsKey(principal)) {
            // 记录中未包含该客户端
            mClientSessions.put(principal, pClient);
            LOG.debug("[{}] new principal[{}] namespace[{}] session[{}]", mServerName, principal, namespace, sessionID);
        } else {
            SocketIOClient cachedClient    = mClientSessions.get(principal);
            String         cachedSessionID = cachedClient.getSessionId().toString();
            if (cachedClient.isChannelOpen()) {
                if (!cachedSessionID.equals(sessionID)) {
                    LOG.debug("[{}] duplicate principal[{}] namespace[{}] session[{}] cache-session[{}]",
                            mServerName, principal, namespace, sessionID, cachedSessionID);
                    // 如果 credentials 存在，需要做唯一登录控制
                    if (!StringUtil.isEmpty(credentials)
                            && !cachedClient.get(PROPERTY_CREDENTIALS).equals(credentials)) {
                        // 通知旧连接登出，此功能需要前端处理
                        LOG.warn("[{}] emit[logout] to cache-session[{}]",
                                mServerName, cachedSessionID);
                        cachedClient.sendEvent(EVENT_LOGOUT, "{}");
                    }
                    mClientSessions.put(principal, pClient);
                    cachedClient.disconnect();
                }
            } else {
                mClientSessions.put(principal, pClient);
                LOG.debug("[{}] replace principal[{}] namespace[{}] session[{}] cache-session[{}]",
                        mServerName, principal, namespace, sessionID, cachedSessionID);
            }
        }
        mHandler.handleNewClient(pClient);
    }

    private String getClientRealIP(SocketIOClient pClient) {
        String ip = pClient.getHandshakeData().getHttpHeaders().get("X-Forwarded-For");
        if (StringUtil.isEmpty(ip)) {
            ip = pClient.getHandshakeData().getHttpHeaders().get("X-Real-IP");
            if (StringUtil.isEmpty(ip)) {
                ip = pClient.getRemoteAddress().toString();
            }
        }
        if (!StringUtil.isEmpty(ip) && ip.contains(",")) {
            ip = Splitter.on(",").trimResults().splitToList(ip).get(0);
        }
        return ip;
    }

    protected void onDisconnected(SocketIOClient pClient) {
        String sessionID   = pClient.getSessionId().toString();
        String namespace   = pClient.getNamespace().getName();
        String principal   = pClient.get(PROPERTY_PRINCIPAL);
        String credentials = pClient.get(PROPERTY_CREDENTIALS);

        LOG.debug("[{}] disconnected session[{}] namespace[{}] principal[{}]",
                mServerName, sessionID, namespace, principal);
        if (!StringUtil.isEmpty(principal)) {
            mHandler.releaseClient(pClient);
            if (pClient.equals(mClientSessions.get(principal))) {
                // 与缓存客户端相同，需要清理缓存中的内容
                LOG.debug("[{}] remove session[{}] namespace[{}] principal[{}] from cache",
                        mServerName, sessionID, namespace, principal);
                mClientSessions.remove(principal);
            } else {
                LOG.debug("[{}] ignore session[{}] namespace[{}] principal[{}] for GC",
                        mServerName, sessionID, namespace, principal);
            }
        }
    }

    public boolean kick(String pSessionID) {
        if (!StringUtil.isEmpty(pSessionID)) {
            Collection<SocketIOClient> clients = mServer.getAllClients();
            for (SocketIOClient client : clients) {
                if (pSessionID.equals(client.getSessionId().toString())) {
                    LOG.warn("KICK principal[{}] namespace[{}] session[{}]",
                            client.get(PROPERTY_PRINCIPAL),
                            client.getNamespace().getName(),
                            pSessionID);
                    client.disconnect();
                    return true;
                }
            }
        }
        return false;
    }

    public int cleanZombieClient() {
        // 清除未进入 mClientSessions 中的连接
        //    通常相同principal的连接最后一个会进入mClientSessions，之前的连接会被断开，并受到logout事件
        //    特殊情况中，之前的连接没有断开，并存在于server连接池中，此时它们就不再收到定向推送
        Collection<SocketIOClient> clients = mServer.getAllClients();
        int                        count   = 0;
        for (SocketIOClient client : clients) {
            String principal = client.get(PROPERTY_PRINCIPAL);
            String sessionID = client.getSessionId().toString();
            if (!StringUtil.isEmpty(principal)) {
                if (mClientSessions.containsKey(principal)) {
                    SocketIOClient cachedClient    = mClientSessions.get(principal);
                    String         cachedSessionID = cachedClient.getSessionId().toString();
                    if (!cachedSessionID.equals(sessionID)) {
                        LOG.warn("CLEAN ZOMBIE principal[{}] namespace[{}] session[{}]",
                                principal, client.getNamespace().getName(), sessionID);
                        client.disconnect();
                        count++;
                    }
                }
            } else {
                client.disconnect();
                count++;
            }
        }
        // 清理mClientSessions中open状态为false的连接
        return count;
    }

    public String getStatus(boolean pShowDetail) {
        StringBuilder content = new StringBuilder();
        content.append("Socket.IO - ").append(mServerName).append(System.lineSeparator());
        content.append(" ┣ Host ").append(mConfiguration.getHostname()).append(System.lineSeparator());
        content.append(" ┣ Port ").append(mConfiguration.getPort()).append(System.lineSeparator());
        content.append(" ┣ Namespaces ").append(System.lineSeparator());
        for (Iterator<String> it = mNamespaces.iterator(); it.hasNext(); ) {
            String namespace = it.next();
            if (it.hasNext()) {
                content.append(" ┃ ┣ [ ").append(namespace).append(" ] ").append(mServer.getNamespace(namespace).getAllClients().size()).append(System.lineSeparator());
            } else {
                content.append(" ┃ ┗ [ ").append(namespace).append(" ] ").append(mServer.getNamespace(namespace).getAllClients().size()).append(System.lineSeparator());
            }
        }
        content.append(" ┣ Cached-Session ").append(mClientSessions.size()).append(System.lineSeparator());
        if (pShowDetail) {
            for (Iterator<String> it = mClientSessions.keySet().iterator(); it.hasNext(); ) {
                String         principal = it.next();
                SocketIOClient client    = mClientSessions.get(principal);
                if (it.hasNext()) {
                    content.append(" ┃ ┣ ").append(String.format("{} {} {} open[{}] room['{}']",
                            principal,
                            client.getSessionId(),
                            client.get(PROPERTY_IP),
                            client.isChannelOpen(),
                            Joiner.on("' '").join(client.getAllRooms())
                    )).append(System.lineSeparator());
                } else {
                    content.append(" ┃ ┗ ").append(String.format("{} {} {} open[{}] room['{}']",
                            principal,
                            client.getSessionId(),
                            client.get(PROPERTY_IP),
                            client.isChannelOpen(),
                            Joiner.on("' '").join(client.getAllRooms())
                    )).append(System.lineSeparator());
                }
            }
        }
        Collection<SocketIOClient> clients = mServer.getAllClients();
        content.append(" ┗ Clients ").append(clients.size()).append(System.lineSeparator());
        if (pShowDetail) {
            for (Iterator<SocketIOClient> it = clients.iterator(); it.hasNext(); ) {
                SocketIOClient client = it.next();
                if (it.hasNext()) {
                    content.append("    ┣ ").append(String.format("{} {} {} open[{}] room['{}']",
                            client.get(PROPERTY_PRINCIPAL),
                            client.getSessionId(),
                            client.get(PROPERTY_IP),
                            client.isChannelOpen(),
                            Joiner.on("' '").join(client.getAllRooms())
                    )).append(System.lineSeparator());
                } else {
                    content.append("    ┗ ").append(String.format("{} {} {} open[{}] room['{}']",
                            client.get(PROPERTY_PRINCIPAL),
                            client.getSessionId(),
                            client.get(PROPERTY_IP),
                            client.isChannelOpen(),
                            Joiner.on("' '").join(client.getAllRooms())
                    )).append(System.lineSeparator());
                }
            }
        }
        return content.toString();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private static class SocketIOHandlerWrapper implements CommonSocketIOHandler {
        private final CommonSocketIOHandler mHandler;

        SocketIOHandlerWrapper(CommonSocketIOHandler pHandler) {
            this.mHandler = pHandler;
        }

        @Override
        public boolean isAuthorized(HandshakeData pData) {
            if (this.mHandler != null) {
                return this.mHandler.isAuthorized(pData);
            }
            return true;
        }

        @Override
        public void handleNewClient(SocketIOClient pClient) {
            if (this.mHandler != null) {
                this.mHandler.handleNewClient(pClient);
            }
        }

        @Override
        public boolean authorizationEnabled() {
            if (this.mHandler != null) {
                return this.mHandler.authorizationEnabled();
            }
            return false;
        }

        @Override
        public String parsePrincipal(SocketIOClient pClient) {
            if (this.mHandler != null) {
                return this.mHandler.parsePrincipal(pClient);
            }
            return pClient.getSessionId().toString();
        }

        @Override
        public String parseCredentials(SocketIOClient pClient) {
            if (this.mHandler != null) {
                return this.mHandler.parseCredentials(pClient);
            }
            return "";
        }

        @Override
        public void releaseClient(SocketIOClient pClient) {
            if (this.mHandler != null) {
                this.mHandler.releaseClient(pClient);
            }
        }
    }

    public interface CommonSocketIOHandler extends AuthorizationListener {
        /**
         * 处理新连接，新连接已经做完验证处理，连接建立并可以收发事件
         *
         * @param pClient 客户端对象
         */
        void handleNewClient(SocketIOClient pClient);

        /**
         * 是否启用连接身份校验
         * 如果启用，会加载 isAuthorized 到 AuthorizationListener
         *
         * @return true 启用
         */
        boolean authorizationEnabled();

        /**
         * 获取授权身份
         *
         * @param pClient 客户端对象
         * @return 授权身份
         */
        String parsePrincipal(SocketIOClient pClient);

        /**
         * 获取授权身份证明
         *
         * @param pClient 客户端对象
         * @return 授权身份证明
         */
        String parseCredentials(SocketIOClient pClient);

        /**
         * 释放积累客户端数据（如果有必要）
         *
         * @param pClient 客户端对象
         */
        void releaseClient(SocketIOClient pClient);
    }
}
