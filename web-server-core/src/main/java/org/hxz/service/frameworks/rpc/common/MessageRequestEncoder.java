package org.hxz.service.frameworks.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageRequestEncoder extends MessageToByteEncoder<MessageRequest> {

    @Override
    public void encode(ChannelHandlerContext ctx, MessageRequest in, ByteBuf out) throws Exception {
    	byte[] data = SerializationUtil.serializeRequest(in);
        out.writeInt(data.length);
    	out.writeBytes(data);
    }
}
