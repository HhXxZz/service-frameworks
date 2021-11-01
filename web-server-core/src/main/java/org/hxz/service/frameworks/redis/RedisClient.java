package org.hxz.service.frameworks.redis;

import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hxz on 2021/10/25 16:32.
 */

public enum RedisClient {
    INSTANCE;

    private final AtomicBoolean init = new AtomicBoolean(false);

    private StatefulRedisConnection<String,String> connection;

    public void init(){
        if(init.compareAndSet(false,true)) {
            RedisURI redisUri = RedisURI.Builder.redis("127.0.0.1")
                    .withPort(6379)
                    //.withPassword("authentication".toCharArray())
                    //.withDatabase(2)
                    .build();
            io.lettuce.core.RedisClient client = io.lettuce.core.RedisClient.create(redisUri);
            connection = client.connect();
        }
    }

    public void set(String key,String value){
        if(init.get()) {
            RedisCommands<String, String> commands = connection.sync();
            commands.set(key, value);
        }
    }

    public String get(String key){
        if(init.get()) {
            RedisCommands<String, String> commands = connection.sync();
            return commands.get(key);
        }
        return null;
    }


}
