package org.hxz.service.frameworks.rpc.common;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class SerializationUtil {

    private static final Schema<MessageRequest> requestSchema = RuntimeSchema.createFrom(MessageRequest.class);
    private static final Schema<MessageResponse> responseSchema = RuntimeSchema.createFrom(MessageResponse.class);
    
    /**
     * 序列化（对象 -> 字节数组）
     */
    public static byte[] serializeRequest(MessageRequest request) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            return ProtostuffIOUtil.toByteArray(request, requestSchema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 序列化（对象 -> 字节数组）
     */
    public static byte[] serializeResponse(MessageResponse response) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            return ProtostuffIOUtil.toByteArray(response, responseSchema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化（字节数组 -> 对象）
     */
    public static MessageRequest deserializeRequest(byte[] data) {
        try {
            MessageRequest request = new MessageRequest();
            ProtostuffIOUtil.mergeFrom(data, request, requestSchema);
            return request;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 反序列化（字节数组 -> 对象）
     */
    public static MessageResponse deserializeResponse(byte[] data) {
        try {
        	MessageResponse response = new MessageResponse();
            ProtostuffIOUtil.mergeFrom(data, response, responseSchema);
            return response;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


}
