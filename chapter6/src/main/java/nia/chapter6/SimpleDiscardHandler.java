package nia.chapter6;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Listing 6.2 Using SimpleChannelInboundHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable
public class SimpleDiscardHandler
    extends SimpleChannelInboundHandler<Object> {
    /**
     * 由于 SimpleChannelInboundHandler 会自动释放资源，所以你不应该存储指向任何消
     * 息的引用供将来使用，因为这些引用都将会失效。
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx,
        Object msg) {
        // No need to do anything special
        // 不需要任何显式的资源释放
    }
}
