package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * Listing 6.4 Discarding and releasing outbound data
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class DiscardOutboundHandler
    extends ChannelOutboundHandlerAdapter {
    /**
     * 在出站方向这边，如果你处理了 write()操作并丢弃了一个消息，那么你也应该负责释放它。
     *
     * 重要的是，不仅要释放资源，还要通知 ChannelPromise。否则可能会出现 ChannelFutureListener 收不到某个消息已经被处理了的通知的情况。
     * 总之，如果一个消息被消费或者丢弃了，并且没有传递给 ChannelPipeline 中的下一个
     * ChannelOutboundHandler，那么用户就有责任调用 ReferenceCountUtil.release()。
     * 如果消息到达了实际的传输层，那么当它被写入时或者 Channel 关闭时，都将被自动释放。
     *
     * @param ctx
     * @param msg
     * @param promise
     */
    @Override
    public void write(ChannelHandlerContext ctx,
        Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg);
        promise.setSuccess();
    }
}

