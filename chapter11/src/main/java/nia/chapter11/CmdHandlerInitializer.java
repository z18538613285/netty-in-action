package nia.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * Listing 11.9 Using a ChannelInitializer as a decoder installer
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {
    private static final byte SPACE = (byte)' ';
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CmdDecoder(64 * 1024)); //添加 CmdDecoder 以提取Cmd 对象，并将它转发给下一个ChannelInboundHandler
        pipeline.addLast(new CmdHandler()); // 添加 CmdHandler 以接收和处理 Cmd 对象
    }

    public static final class Cmd { //Cmd POJO
        private final ByteBuf name;
        private final ByteBuf args;

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }

        public ByteBuf name() {
            return name;
        }

        public ByteBuf args() {
            return args;
        }
    }

    public static final class CmdDecoder extends LineBasedFrameDecoder {
        public CmdDecoder(int maxLength) {
            super(maxLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer)
            throws Exception {
            ByteBuf frame = (ByteBuf) super.decode(ctx, buffer); // 从 ByteBuf 中提取由行尾符序列分隔的帧
            if (frame == null) {
                return null; //如果输入中没有帧，则返回 null
            }
            int index = frame.indexOf(frame.readerIndex(), // 查找第一个空格字符的索引。前面是命令名称，接着是参数
                    frame.writerIndex(), SPACE);
            return new Cmd(frame.slice(frame.readerIndex(), index), // 使用包含有命令名称和参数的切片创建新的Cmd 对象
                    frame.slice(index + 1, frame.writerIndex()));
        }
    }

    public static final class CmdHandler
        extends SimpleChannelInboundHandler<Cmd> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Cmd msg)
            throws Exception { // 处理传经 ChannelPipeline的 Cmd 对象
            // Do something with the command
        }
    }
}
