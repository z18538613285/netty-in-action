package nia.chapter13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Listing 13.6 LogEventDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx,
        DatagramPacket datagramPacket, List<Object> out)
        throws Exception {
        ByteBuf data = datagramPacket.content(); // 获取对 DatagramPacket 中的数据（ByteBuf）的引用
        int idx = data.indexOf(0, data.readableBytes(), // 获取该 SEPARATOR的索引
            LogEvent.SEPARATOR);
        String filename = data.slice(0, idx) // 提取文件名
            .toString(CharsetUtil.UTF_8);
        String logMsg = data.slice(idx + 1, // 提取日志消息
            data.readableBytes()).toString(CharsetUtil.UTF_8);
        LogEvent event = new LogEvent(datagramPacket.sender(), // 构建一个新的 LogEvent 对象，并且将它添加到（已经解码的消息的）列表中
            System.currentTimeMillis(), filename, logMsg);
        out.add(event);
    }
}
