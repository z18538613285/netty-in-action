package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * Listing 11.7 Sending heartbeats
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel>
    {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(
                new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS)); //IdleStateHandler 将在被触发时发送一个IdleStateEvent 事件
        pipeline.addLast(new HeartbeatHandler()); //将一个HeartbeatHandler添加到ChannelPipeline中
    }

    public static final class HeartbeatHandler
        extends ChannelInboundHandlerAdapter { //实现 userEventTriggered()方法以发送心跳消息

        private static final ByteBuf HEARTBEAT_SEQUENCE =  // 发送到远程节点的心跳消息
                Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
                "HEARTBEAT", CharsetUtil.ISO_8859_1));
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx,
            Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) { // 发送心跳消息，并在发送失败时关闭该连接
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                     .addListener(
                         ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                super.userEventTriggered(ctx, evt); //不是 IdleStateEvent事件，所以将它传递给下一个 ChannelInboundHandler
            }
        }
    }
}
