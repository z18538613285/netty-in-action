package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Listing 6.3 Consuming and releasing an inbound message
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class DiscardInboundHandler extends ChannelInboundHandlerAdapter {
    /**
     * 消费入站消息的简单方式 由于消费入站数据是一项常规任务，所以 Netty 提供了一个特殊的被
     * 称为 SimpleChannelInboundHandler 的 ChannelInboundHandler 实现。这个实现会在消
     * 息被 channelRead0()方法消费之后自动释放消息。
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }
}
