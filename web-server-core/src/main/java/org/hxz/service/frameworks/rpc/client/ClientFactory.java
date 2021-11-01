package org.hxz.service.frameworks.rpc.client;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxz on 2021/7/2 10:14.
 */

public class ClientFactory extends BasePooledObjectFactory<ClientChannel> {
    private static final Logger logger = LoggerFactory.getLogger(ClientFactory.class);

    private final BaseRpcClient client;
    private ClientChannel clientChannel;
    public ClientFactory(String host, int port){
        client = new BaseRpcClient(host,port);
    }

    @Override
    public ClientChannel create() throws Exception {
        clientChannel = new ClientChannel();
        ChannelFuture channelFuture = client.connect();
        if(channelFuture == null){
            logger.error("BaseRpcClient.connect.channelFuture is null.error");
            return null;
        }
        clientChannel.setChannelFuture(channelFuture);
        channelFuture.awaitUninterruptibly();
        if (!channelFuture.isSuccess()) {
            logger.warn("Making new connection on " + client.getInfo() + " not success", channelFuture.cause());
        }
        logger.info("Making new connection on " + client.getInfo() + " and adding to pool done");
        clientChannel.setChannelFuture(channelFuture);
        return clientChannel;
    }

    @Override
    public PooledObject<ClientChannel> wrap(ClientChannel clientChannel) {
        return new DefaultPooledObject<>(clientChannel);
    }

    @Override
    public void destroyObject(PooledObject<ClientChannel> pooledObject) throws Exception {
        ClientChannel ch = pooledObject.getObject();
        Channel channel = ch.getChannelFuture().channel();
        if (channel.isOpen() && channel.isActive()) {
            channel.close();
        }
        logger.info("Closing channel and destroy connection from pool done");
    }

    @Override
    public void passivateObject(PooledObject<ClientChannel> pooledObject) throws Exception {

    }

    @Override
    public boolean validateObject(PooledObject<ClientChannel> pooledObject) {
        ClientChannel ch = pooledObject.getObject();
        Channel channel = ch.getChannelFuture().channel();
        return channel.isActive();
    }

    @Override
    public void activateObject(PooledObject<ClientChannel> pooledObject) throws Exception {

    }


}
