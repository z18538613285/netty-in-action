package nia.chapter11;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by kerr.
 *
 * Listing 11.11 Transferring file contents with FileRegion
 */
public class FileRegionWriteHandler extends ChannelInboundHandlerAdapter {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final File FILE_FROM_SOMEWHERE = new File("");

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        File file = FILE_FROM_SOMEWHERE; //get reference from somewhere
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference from somewhere
        //...
        FileInputStream in = new FileInputStream(file); // 创建一个 FileInputStream
        FileRegion region = new DefaultFileRegion( // 以该文件的完整长度创建一个新的 DefaultFileRegion
                in.getChannel(), 0, file.length());
        channel.writeAndFlush(region).addListener( // 发送该 DefaultFileRegion，并注册一个ChannelFutureListener
            new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future)
               throws Exception {
               if (!future.isSuccess()) {
                   Throwable cause = future.cause();
                   // Do something
               }
            }
        });
    }
}
