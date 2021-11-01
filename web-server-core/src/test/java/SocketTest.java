import org.hxz.service.frameworks.websocket.SocketServer;

/**
 * Created by hxz on 2021/6/22 11:27.
 */

public class SocketTest {

    public static void main(String[] args) throws InterruptedException {

        SocketServer.INSTANCE.init();

//        Configuration config = new Configuration();
//        config.setHostname("127.0.0.1");
//        config.setPort(9092);
//        config.setUseLinuxNativeEpoll(!PlatformDependent.isWindows() && Epoll.isAvailable());
//        config.setOrigin("*");
//        config.setTransports(Transport.WEBSOCKET);
//        config.setPingTimeout(35000);
//        config.setPingInterval(15000);
//        config.setAckMode(AckMode.MANUAL);
//        config.setContext("/im");
//
//        final SocketIOServer server = new SocketIOServer(config);
////        server.addNamespace("/im");
//
//        server.addEventListener("EVENT_GROUP_MESSAGE", String.class,
//                (client, data, ackRequest) -> {
//            // broadcast messages to all clients
//            System.out.println("EVENT_GROUP_MESSAGE:"+data);
//            server.getBroadcastOperations().sendEvent("EVENT_GROUP_MESSAGE", data);
//        });
//        server.addConnectListener(client -> {
//            System.out.println("=============onConnect:"+ client.getTransport().getValue());
//        });
//        //server.addListeners();
//        server.start();
//
//        Thread.sleep(Integer.MAX_VALUE);
//
//        server.stop();

    }


    static class ChatObject{
        private String userName;
        private String message;

        public ChatObject() {
        }

        public ChatObject(String userName, String message) {
            super();
            this.userName = userName;
            this.message = message;
        }

        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
