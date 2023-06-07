package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Listing 6.1 Releasing message resources
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class DiscardHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当某个 ChannelInboundHandler 的实现重写 channelRead()方法时，它将负责显式地
     * 释放与池化的 ByteBuf 实例相关的内存。Netty 为此提供了一个实用方法 ReferenceCountUtil.release()，
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 丢弃已接收的消息
        ReferenceCountUtil.release(msg);
    }

}

