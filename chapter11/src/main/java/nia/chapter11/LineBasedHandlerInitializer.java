package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * Listing 11.8 Handling line-delimited frames
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class LineBasedHandlerInitializer extends ChannelInitializer<Channel>
    {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LineBasedFrameDecoder(64 * 1024)); // 该 LineBasedFrameDecoder将提取的帧转发给下一个 ChannelInboundHandler
        pipeline.addLast(new FrameHandler()); // 添加 FrameHandler以接收帧
    }

    public static final class FrameHandler
        extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, // 传入了单个帧的内容
            ByteBuf msg) throws Exception {
            // Do something with the data extracted from the frame
        }
    }
}
