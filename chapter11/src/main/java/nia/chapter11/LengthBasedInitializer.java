package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Listing 11.10 Decoder for the command and the handler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(
                // 使用 LengthFieldBasedFrameDecoder 解码将帧长度编码到帧起始的前 8 个字节中的消息
                new LengthFieldBasedFrameDecoder(64 * 1024, 0, 8));
        pipeline.addLast(new FrameHandler()); // 添加 FrameHandler以处理每个帧
    }

    public static final class FrameHandler
        extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx,
             ByteBuf msg) throws Exception {
            // Do something with the frame
            // 处理帧的数据
        }
    }
}
