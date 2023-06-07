package nia.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Listing 10.2 Class ToIntegerDecoder2 extends ReplayingDecoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> { //其中 Void 代表不需要状态管理

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
        List<Object> out) throws Exception {

        out.add(in.readInt());
    }
}

