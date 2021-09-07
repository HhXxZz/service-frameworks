package base.service.frameworks.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import base.service.frameworks.misc.Code;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by someone on 2017-01-18.
 *
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
public class ResponseUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(ResponseUtil.class);

    private static final StringBuilder ERROR_HTML = new StringBuilder();
    private static final StringBuilder SHOW_CACHE_HTML = new StringBuilder();
    static{
        ERROR_HTML.append("<!DOCTYPE html>").append(System.lineSeparator());
        ERROR_HTML.append("<html>").append(System.lineSeparator());
        ERROR_HTML.append("<head>").append(System.lineSeparator());
        ERROR_HTML.append("<title>Error</title>").append(System.lineSeparator());
        ERROR_HTML.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>").append(System.lineSeparator());
        ERROR_HTML.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>").append(System.lineSeparator());
        ERROR_HTML.append("<meta content=\"yes\" name=\"apple-mobile-web-app-capable\"/>").append(System.lineSeparator());
        ERROR_HTML.append("</head>").append(System.lineSeparator());
        ERROR_HTML.append("<body>").append(System.lineSeparator());
        ERROR_HTML.append("<h3>(%d) %s</h3>").append(System.lineSeparator());
        ERROR_HTML.append("</body>").append(System.lineSeparator());
        ERROR_HTML.append("</html>").append(System.lineSeparator());

        SHOW_CACHE_HTML.append("<!DOCTYPE html>");
        SHOW_CACHE_HTML.append("<html>");
        SHOW_CACHE_HTML.append("<head>");
        SHOW_CACHE_HTML.append("<title>show cache</title>");
        SHOW_CACHE_HTML.append("<style>");
        SHOW_CACHE_HTML.append("pre {font-size: 13px; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale; color: #333;}");
        SHOW_CACHE_HTML.append("a:link, a:visited {background-color: #0066cc; color: white; margin-left: 5px; padding: 5px 10px; text-align: center; text-decoration: none; display: inline-block; }");
        SHOW_CACHE_HTML.append("a:hover, a:active {background-color: #0099ff;}");
        SHOW_CACHE_HTML.append(".selected {background-color: #0099ff !important;}");
        SHOW_CACHE_HTML.append("</style>");
        SHOW_CACHE_HTML.append("</head>");
        SHOW_CACHE_HTML.append("<body>");
        SHOW_CACHE_HTML.append("<div>");
        SHOW_CACHE_HTML.append("{cache-menu}");
        SHOW_CACHE_HTML.append("</div>");
        SHOW_CACHE_HTML.append("<pre>");
        SHOW_CACHE_HTML.append("{cache-content}");
        SHOW_CACHE_HTML.append("</pre>");
        SHOW_CACHE_HTML.append("</body>");
        SHOW_CACHE_HTML.append("</html>");
    }

    public static final String Content_Type_Application_Json       = "application/json; charset=UTF-8";
    public static final String Content_Type_Application_Javascript = "application/javascript; charset=UTF-8";
    public static final String Content_Type_Text_Plain             = "text/plain; charset=UTF-8";
    public static final String Content_Type_Text_HTML              = "text/html; charset=UTF-8";
    public static final String Content_Type_Application_form       = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String Content_Type_Image_Jpeg             = "image/jpeg";
    public static final String Content_Pragma                      = "no-cache";
    public static final String Content_Cache_Control               = "no-cache";
    public static final int    Content_Expires                     = 0;



    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================
    public static String getShowCacheHTMLTemplate(){
        return SHOW_CACHE_HTML.toString();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public static void writeResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pContent) {
        writeResponse(pRequest, pContext, null, pContent, null);
    }

    public static void writeResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pContent, HttpResponseStatus pStatus) {
        writeResponse(pRequest, pContext, null, pContent, pStatus);
    }

    public static void writeResponse(HttpRequest pRequest, ChannelHandlerContext pContext, Map<AsciiString, String> pHeaders, String pContent) {
        writeResponse(pRequest, pContext, pHeaders, pContent, null);
    }

    public static void writeResponse(HttpRequest pRequest, ChannelHandlerContext pContext, Map<AsciiString, String> pHeaders, String pContent, HttpResponseStatus pStatus) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(pRequest);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, pRequest.decoderResult().isSuccess() ? (pStatus != null ? pStatus : OK) : BAD_REQUEST,
                Unpooled.copiedBuffer(pContent, CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, Content_Type_Text_Plain);

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        if(pHeaders != null && pHeaders.size() > 0){
            for(AsciiString header : pHeaders.keySet()){
                response.headers().set(header, pHeaders.get(header));
            }
        }

        // Write the response.
        pContext.writeAndFlush(response, pContext.voidPromise());

        if(!keepAlive){
            pContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void writeImageResponse(HttpRequest pRequest, ChannelHandlerContext pContext, Map<AsciiString, String> pHeaders, BufferedImage pImage) {
        if(pImage != null) {
            ByteBuf buffer = Unpooled.buffer();
            ByteBufOutputStream out = new ByteBufOutputStream((buffer));
            try{
                ImageIO.write(pImage, "jpeg", out);
                buffer = out.buffer();
            }catch(IOException e){
                e.printStackTrace();
            }
            if(buffer != null) {
                // Decide whether to close the connection or not.
                boolean keepAlive = HttpUtil.isKeepAlive(pRequest);
                // Build the response object.
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1, pRequest.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                        buffer);

                response.headers().set(CONTENT_TYPE, Content_Type_Image_Jpeg);
                response.headers().set(PRAGMA,Content_Pragma);
                response.headers().set(CACHE_CONTROL, Content_Cache_Control);
                response.headers().set(EXPIRES, Content_Expires);

                if (keepAlive) {
                    // Add 'Content-Length' header only for a keep-alive connection.
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                    // Add keep alive header as per:
                    // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                    response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }

                if (pHeaders != null && pHeaders.size() > 0) {
                    for (AsciiString header : pHeaders.keySet()) {
                        response.headers().set(header, pHeaders.get(header));
                    }
                }

                // Write the response.
                pContext.writeAndFlush(response, pContext.voidPromise());

                if (!keepAlive) {
                    pContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }else{
                LOG.error("Empty image buffer returned");
                pContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }else{
            LOG.error("No image returned");
            pContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static void writeResponseObject(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult){
        writeResponseObject(pRequest, pContext, pCallback, pResult, false, null);
    }

    public static void writeResponseObject(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult, boolean pEnableExpose){
        writeResponseObject(pRequest, pContext, pCallback, pResult, pEnableExpose, null);
    }

    public static void writeResponseObject(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult, HttpResponseStatus pStatus){
        writeResponseObject(pRequest, pContext, pCallback, pResult, false, pStatus);
    }

    public static void writeResponseObject(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult, boolean pEnableExpose, HttpResponseStatus pStatus){
        Map<AsciiString, String> headers = new HashMap<>();
        String result;
        if(StringUtil.isEmpty(pCallback)){
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Json);
        }else{
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Javascript);
        }
        writeResponse(pRequest, pContext, headers, GsonUtil.toJson(pResult, pEnableExpose), pStatus);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Code pCode){
        writeErrorResponse(pRequest, pContext, pCallback, pCode, null);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Code pCode, HttpResponseStatus pStatus){
        if(pCode == null){
            pCode = Code.BaseCode.ERR_DEFAULT;
        }
        writeErrorResponse(pRequest, pContext, pCallback, pCode.getCode(), pCode.getMessage(), pStatus);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, int pCode, String pMessage){
        writeErrorResponse(pRequest, pContext, pCallback, pCode, pMessage, null);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, int pCode, String pMessage, HttpResponseStatus pStatus){
        Map<AsciiString, String> headers = new HashMap<>();

        String result;
        if(StringUtil.isEmpty(pCallback)){
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Json);
            result = String.format("{\"code\":%d, \"msg\":\"%s\"}", pCode, pMessage);
        }else{
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Javascript);
            result = String.format("%s({\"code\":%d, \"msg\":\"%s\"})", pCallback, pCode, pMessage);
        }
        writeResponse(pRequest, pContext, headers, result, pStatus);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Code pCode, Object pResult){
        writeErrorResponse(pRequest, pContext, pCallback, pCode.getCode(), pCode.getMessage(), pResult, false, null);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Code pCode, Object pResult, HttpResponseStatus pStatus){
        writeErrorResponse(pRequest, pContext, pCallback, pCode.getCode(), pCode.getMessage(), pResult, false, pStatus);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Code pCode, Object pResult, boolean pEnableExpose){
        writeErrorResponse(pRequest, pContext, pCallback, pCode, pResult, pEnableExpose, null);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Code pCode, Object pResult, boolean pEnableExpose, HttpResponseStatus pStatus){
        if(pCode == null){
            pCode = Code.BaseCode.ERR_DEFAULT;
        }
        writeErrorResponse(pRequest, pContext, pCallback, pCode.getCode(), pCode.getMessage(), pResult, pEnableExpose, pStatus);
    }

    public static void writeErrorResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, int pCode, String pMessage, Object pResult, boolean pEnableExpose, HttpResponseStatus pStatus){
        Map<AsciiString, String> headers = new HashMap<>();
        String result;
        if(StringUtil.isEmpty(pCallback)){
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Json);
            result = String.format("{\"code\":%d, \"msg\":\"%s\", \"data\": %s}",
                    pCode,
                    pMessage,
                    GsonUtil.toJson(pResult, pEnableExpose));
        }else{
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Javascript);
            result = String.format("%s({\"code\":%d, \"msg\":\"%s\", \"data\": %s})",
                    pCallback,
                    pCode,
                    pMessage,
                    GsonUtil.toJson(pResult, pEnableExpose));
        }
        writeResponse(pRequest, pContext, headers, result, pStatus);
    }

    public static void writeHTMLResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pHTMLContent){
        writeHTMLResponse(pRequest, pContext, pHTMLContent, null);
    }

    public static void writeHTMLResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pHTMLContent, HttpResponseStatus pStatus){
        Map<AsciiString, String> headers = new HashMap<>();
        headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Text_HTML);

        writeResponse(pRequest, pContext, headers, pHTMLContent, pStatus);
    }

    public static void writeSuccessResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult){
        writeSuccessResponse(pRequest, pContext, pCallback, pResult, false, null);
    }

    public static void writeSuccessResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult, HttpResponseStatus pStatus){
        writeSuccessResponse(pRequest, pContext, pCallback, pResult, false, pStatus);
    }

    public static void writeSuccessResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult, boolean pEnableExpose){
        writeSuccessResponse(pRequest, pContext, pCallback, pResult, pEnableExpose, null);
    }

    public static void writeSuccessResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, Object pResult, boolean pEnableExpose, HttpResponseStatus pStatus){
        Map<AsciiString, String> headers = new HashMap<>();
        String result;
        if(StringUtil.isEmpty(pCallback)){
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Json);
            result = String.format("{\"code\":%d, \"msg\":\"%s\", \"data\": %s}",
                    Code.BaseCode.SUCCESS.getCode(),
                    Code.BaseCode.SUCCESS.getMessage(),
                    GsonUtil.toJson(pResult, pEnableExpose));
        }else{
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Javascript);
            result = String.format("%s({\"code\":%d, \"msg\":\"%s\", \"data\": %s})",
                    pCallback,
                    Code.BaseCode.SUCCESS.getCode(),
                    Code.BaseCode.SUCCESS.getMessage(),
                    GsonUtil.toJson(pResult, pEnableExpose));
        }
        writeResponse(pRequest, pContext, headers, result, pStatus);
    }

    public static void writeSimpleSuccessResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback){
        writeSimpleSuccessResponse(pRequest, pContext, pCallback, null);
    }

    public static void writeSimpleSuccessResponse(HttpRequest pRequest, ChannelHandlerContext pContext, String pCallback, HttpResponseStatus pStatus){
        Map<AsciiString, String> headers = new HashMap<>();

        String result;
        if(StringUtil.isEmpty(pCallback)){
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Json);
            result = String.format("{\"code\":%d, \"msg\":\"%s\"}", Code.BaseCode.SUCCESS.getCode(), Code.BaseCode.SUCCESS.getMessage());
        }else{
            headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Application_Javascript);
            result = String.format("%s({\"code\":%d, \"msg\":\"%s\"})", pCallback, Code.BaseCode.SUCCESS.getCode(), Code.BaseCode.SUCCESS.getMessage());
        }
        writeResponse(pRequest, pContext, headers, result, pStatus);
    }

    public static void writeErrorHTMLResponse(HttpRequest pRequest, ChannelHandlerContext pContext, Code pCode){
        writeErrorHTMLResponse(pRequest, pContext, pCode, null);
    }

    public static void writeErrorHTMLResponse(HttpRequest pRequest, ChannelHandlerContext pContext, Code pCode, HttpResponseStatus pStatus){
        if(pCode == null){
            pCode = Code.BaseCode.ERR_DEFAULT;
        }
        Map<AsciiString, String> headers = new HashMap<>();
        headers.put(HttpHeaderNames.CONTENT_TYPE, Content_Type_Text_HTML);

        String content = String.format(ERROR_HTML.toString(), pCode.getCode(), pCode.getMessage());
        writeResponse(pRequest, pContext, headers, content, pStatus);
    }

    public static void redirect(HttpRequest request, ChannelHandlerContext pContext, String pUrl){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, pUrl);

        // Close the connection as soon as the error message is sent.
        pContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
