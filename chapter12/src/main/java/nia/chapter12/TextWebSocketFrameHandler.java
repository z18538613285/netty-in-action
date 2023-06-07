package nia.chapter12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Listing 12.2 Handling text frames
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class TextWebSocketFrameHandler
    extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, //重写 userEventTriggered()方法以处理自定义事件
        Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler
             .ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 如果该事件表示握手成功，则从该Channelipeline中移除 HttpRequestHandler，因为将不会接收到任何HTTP 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            group.writeAndFlush(new TextWebSocketFrame(
                    "Client " + ctx.channel() + " joined")); // 通知所有已经连接的WebSocket 客户端新的客户端已经连接上了
            group.add(ctx.channel()); // 将新的 WebSocket Channel添加到 ChannelGroup 中，以便它可以接收到所有的消息
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
        TextWebSocketFrame msg) throws Exception {
        // 增加消息的引用计数，并将它写到 ChannelGroup 中所有已经连接的客户端
        group.writeAndFlush(msg.retain());
    }
}
