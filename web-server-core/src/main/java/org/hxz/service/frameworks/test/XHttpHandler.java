package org.hxz.service.frameworks.test;

import org.hxz.service.frameworks.rpc.client.ClientPool;
import org.hxz.service.frameworks.rpc.common.MessageRequest;
import org.hxz.service.frameworks.rpc.common.MessageResponse;
import org.hxz.service.frameworks.rpc.server.ServiceManager;
import org.hxz.service.frameworks.utils.GsonUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.hxz.service.frameworks.utils.ResponseUtil.Content_Type_Text_Plain;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class XHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger logger = LoggerFactory.getLogger(XHttpHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		boolean keepAlive = HttpUtil.isKeepAlive(msg);

		ClientPool clientPool = ServiceManager.INSTANCE.chooseClient("service");
		MessageRequest request = new MessageRequest();
		request.setRequestId(UUID.randomUUID().toString());
		request.setServiceName("service");
		request.setApi("");
		MessageResponse responseStr = clientPool.syncTransport(request);

		FullHttpResponse response = new DefaultFullHttpResponse(
				HTTP_1_1, OK,
				Unpooled.copiedBuffer(GsonUtil.toJson(responseStr), CharsetUtil.UTF_8));

		response.headers().set(CONTENT_TYPE, Content_Type_Text_Plain);

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			// Add keep alive header as per:
			// - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}


		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("Server Closed:"+ctx.channel().remoteAddress().toString(), cause);
		ctx.close();
	}
}