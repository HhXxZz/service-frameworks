package org.hxz.service.frameworks.socket.tcp;

import org.hxz.service.frameworks.utils.RegexUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * Created by someone on 2018-10-18.
 *
 * </pre>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CommonTCPClient extends Thread {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(CommonTCPClient.class);

    // ===========================================================
    // Fields
    // ===========================================================
    private String             mHost;
    private Integer            mPort;
    private ChannelInitializer mInitializer;
    private EventLoopGroup     mGroup;
    private Channel            mChannel;
    private boolean            mRunning;
    private AtomicBoolean      mIsReconnecting;

    // ===========================================================
    // Constructors
    // ===========================================================
    private CommonTCPClient(String pHost, int pPort, ChannelInitializer pInitializer) throws RuntimeException {
        this.mHost = pHost;
        this.mPort = pPort;
        this.mInitializer = pInitializer;
        this.mRunning = true;
        this.mIsReconnecting = new AtomicBoolean(false);
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public static CommonTCPClient create(String pHost, int pPort, ChannelInitializer pInitializer){
        if(!RegexUtil.isIPAddress(pHost)){
            throw new RuntimeException("Can't create TCP client for host[" + pHost + "]");
        }
        if(pPort < 1 || pPort > 65535){
            throw new RuntimeException("Can't create TCP client for port[" + pPort + "] (1-65535)");
        }
        if(pInitializer == null){
            throw new RuntimeException("Can't create TCP client without initializer");
        }
        return new CommonTCPClient(pHost, pPort, pInitializer);
    }

    @Override
    public void run() {
        while(mRunning) {
            LOG.warn("TCP client connecting with host[{}] post[{}]", mHost, mPort);
            mGroup = new NioEventLoopGroup();
            try{
                Bootstrap bootstrap = new Bootstrap();

                bootstrap.group(mGroup);
                bootstrap.channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.remoteAddress(new InetSocketAddress(mHost, mPort));
                bootstrap.handler(mInitializer);

                mChannel = bootstrap.connect().channel();
                mChannel.closeFuture().sync();
            } catch (Exception e) {
                LOG.error("TCP Client interrupted! By", e);
                if(! (e instanceof InterruptedException)){
                    e.printStackTrace();
                }
            } finally {
                if(mRunning && !this.isInterrupted() && this.mIsReconnecting.compareAndSet(false, true)){
                    LOG.debug("TCP client reconnecting...");
                    try {
                        mGroup.shutdownGracefully(0, 5000, TimeUnit.MILLISECONDS).sync();
                        LOG.warn("TCP client reconnecting... EventLoopGroup shutdown");
                    } catch (Exception ignore){
                        LOG.error("TCP client EventLoopGroup shutdown fail");
                    }
                    this.mIsReconnecting.compareAndSet(true, false);
                }
            }
            if(mRunning) {
                LOG.warn("TCP client disconnected with host[{}] post[{}], reconnect in 5s", mHost, mPort);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignore) {
                }
            }
        }
        LOG.debug("Exit TCP client thread");
    }

    public void shutdown() {
        this.mRunning = false;
        LOG.debug("TCP client disconnecting...");
        disconnect();
    }

    public void reconnect(){
        if(this.mRunning && this.mIsReconnecting.compareAndSet(false, true)){
            LOG.debug("TCP client reconnecting...");
            this.disconnect();
            this.mIsReconnecting.compareAndSet(true, false);
        }
    }

    public void disconnect(){
        //this.interrupt();
        this.mChannel.close();
        try {
            mGroup.shutdownGracefully(2000, 5000, TimeUnit.MILLISECONDS).sync();
            LOG.debug("TCP client disconnecting... EventLoopGroup shutdown");
        } catch (Exception e){
            LOG.error("TCP client EventLoopGroup shutdown fail", e);
        }
        try {
            mChannel.closeFuture().sync();
        } catch (Exception e) {
            LOG.error("TCP client channel shutdown fail", e);
        }finally {
            LOG.info("TCP client shutdown successfully.");
        }
        LOG.info("TCP client channel disconnected");
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
