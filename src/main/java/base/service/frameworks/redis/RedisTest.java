package base.service.frameworks.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Created by hxz on 2021/7/13 15:18.
 */

public class RedisTest {


    public static void main(String[] args) {
        RedisURI redisUri = RedisURI.Builder.redis("127.0.0.1")
                .withPort(6379)
                //.withPassword("authentication".toCharArray())
                //.withDatabase(2)
                .build();

        RedisClient client = RedisClient.create(redisUri);

        StatefulRedisConnection<String, String> connection = client.connect();

        RedisCommands<String, String> commands = connection.sync();
        //commands.xadd()
        commands.set("foo","123123");
        String value = commands.get("foo");
        //client.g
        System.out.println(value);


    }

}
