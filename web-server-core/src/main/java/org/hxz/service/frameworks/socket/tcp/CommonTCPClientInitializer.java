package org.hxz.service.frameworks.socket.tcp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * <pre>
 * Created by someone on 2018-10-22.
 *
 * </pre>
 */
@SuppressWarnings("unused")
public abstract class CommonTCPClientInitializer extends ChannelInitializer<SocketChannel> {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    protected void initChannel(SocketChannel pChannel) {
        initCustomChannel(pChannel.pipeline());
    }

    // ===========================================================
    // Methods
    // ===========================================================
    /**
     * 需要添加的自定义Handler
     * @param pPipe 需要添加的 {@link ChannelHandler}
     */
    protected abstract void initCustomChannel(ChannelPipeline pPipe);

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
