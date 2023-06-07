package nia.chapter6;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Listing 6.9 Caching a ChannelHandlerContext
 *
 * 为何要共享同一个ChannelHandler 在多个ChannelPipeline中安装同一个ChannelHandler
 * 的一个常见的原因是用于收集跨越多个 Channel 的统计信息。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class WriteHandler extends ChannelHandlerAdapter {
    private ChannelHandlerContext ctx;
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 存储到 ChannelHandlerContext的引用以供稍后使用
        this.ctx = ctx;
    }
    public void send(String msg) {
        // 使用之前存储的到 ChannelHandlerContext的引用来发送消息
        ctx.writeAndFlush(msg);
    }
}
