package base.service.frameworks.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageResponseEncoder extends MessageToByteEncoder<MessageResponse> {

    @Override
    public void encode(ChannelHandlerContext ctx, MessageResponse in, ByteBuf out) throws Exception {
    	byte[] data = SerializationUtil.serializeResponse(in);
        out.writeInt(data.length);
    	out.writeBytes(data);
    }
}
