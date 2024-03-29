import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

import java.net.URISyntaxException;

/**
 * Created by hxz on 2021/6/23 15:11.
 */

public class SocketClientTest {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        IO.Options options = new IO.Options();
        options.transports = new String[]{WebSocket.NAME};
        options.path = "/im";
        Socket mSocket = IO.socket("http://127.0.0.1:9092?uid=1001&token=2222", options);

        mSocket.connect();


        mSocket.on("EVENT_MESSAGE", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println(objects[0].toString());
            }
        });


        mSocket.emit("EVENT_MESSAGE","a");

        Thread.sleep(Integer.MAX_VALUE);


    }
}
