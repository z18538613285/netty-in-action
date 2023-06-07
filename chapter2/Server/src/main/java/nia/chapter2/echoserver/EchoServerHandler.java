package nia.chapter2.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Listing 2.1 EchoServerHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
// 标示一个ChannelHandler 可以被多个 Channel 安全地共享
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    // 对于每个传入的消息都要调用；
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        // 将消息记录到控制台
        System.out.println(
                "Server received: " + in.toString(CharsetUtil.UTF_8));
        // 将接收到的消息写给发送者，而不冲刷出站消息
        ctx.write(in);
    }

    // 通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息；
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        // 将未决消息冲刷到远程节点，并且关闭该 Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 如果不捕获异常，会发生什么呢
     * 每个 Channel 都拥有一个与之相关联的 ChannelPipeline，其持有一个 ChannelHandler 的
     * 实例链。在默认的情况下，ChannelHandler 会把对它的方法的调用转发给链中的下一个 ChannelHandler。
     * 因此，如果 exceptionCaught()方法没有被该链中的某处实现，那么所接收的异常将会被
     * 传递到 ChannelPipeline 的尾端并被记录。为此，你的应用程序应该提供至少有一个实现了
     * exceptionCaught()方法的 ChannelHandler。
     * @param ctx
     * @param cause
     */
    // 在读取操作期间，有异常抛出时会调用。
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        // 打印异常
        cause.printStackTrace();
        // 关闭 Channel
        ctx.close();
    }
}
