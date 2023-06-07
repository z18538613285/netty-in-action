package nia.chapter13;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Listing 13.7 LogEventHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class LogEventHandler
    extends SimpleChannelInboundHandler<LogEvent> { // 扩展 SimpleChannelInboundHandler 以处理 LogEvent 消息

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) throws Exception {
        cause.printStackTrace(); // 当异常发生时，打印栈跟踪信息，并关闭对应的 Channel
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
        LogEvent event) throws Exception {
        StringBuilder builder = new StringBuilder(); // 创建 StringBuilder，并且构建输出的字符串
        builder.append(event.getReceivedTimestamp());
        builder.append(" [");
        builder.append(event.getSource().toString());
        builder.append("] [");
        builder.append(event.getLogfile());
        builder.append("] : ");
        builder.append(event.getMsg());
        System.out.println(builder.toString()); // 打印 LogEvent的数据
    }
}
