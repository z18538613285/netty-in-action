package nia.chapter12;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Listing 12.1 HTTPRequestHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 扩展 SimpleChannelInboundHandler 以处理FullHttpRequest 消息
    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class
             .getProtectionDomain()
             .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                 "Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
        FullHttpRequest request) throws Exception {
        // 如果请求了 WebSocket协议升级，则增加引用计数（调用 retain()方法），并将它传递给下一个ChannelInboundHandler
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            // 处理 100 Continue请求以符合 HTTP1.1 规范
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(INDEX, "r"); // 读取index.html
            HttpResponse response = new DefaultHttpResponse(
                request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(
                HttpHeaders.Names.CONTENT_TYPE,
                "text/html; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);
            if (keepAlive) { // 如果请求了keep-alive，则添加所需要的 HTTP头信息
                response.headers().set(
                    HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set( HttpHeaders.Names.CONNECTION,
                    HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response); // 将 HttpResponse写到客户端
            if (ctx.pipeline().get(SslHandler.class) == null) { // 将 index.html写到客户端
                ctx.write(new DefaultFileRegion(
                    file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush( // 写 LastHttpContent并冲刷至客户端
                LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) { // 如果没有请求keep-alive，则在写操作完成后关闭Channel
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
