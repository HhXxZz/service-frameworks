package org.hxz.service.frameworks.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageRequestDecoder extends ByteToMessageDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRequestDecoder.class);

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    	try{
	        if (in.readableBytes() < 4) {
	            return;
	        }
	        in.markReaderIndex();
	        int dataLength = in.readInt();
	        /*if (dataLength <= 0) {
	            ctx.close();
	        }*/
	        if (in.readableBytes() < dataLength) {
	            in.resetReaderIndex();
	            return;
	        }
	        byte[] data = new byte[dataLength];
	        in.readBytes(data);
	
	        Object obj = SerializationUtil.deserializeRequest(data);
	        out.add(obj);
    	}catch(Exception e){
    		logger.error("decode", e);
    	}
    }

}
