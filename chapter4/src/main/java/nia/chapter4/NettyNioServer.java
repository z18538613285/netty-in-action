package nia.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Listing 4.4 Asynchronous networking with Netty
 *
 * ChannelHandler 的典型用途包括：
 *  将数据从一种格式转换为另一种格式；
 *  提供异常的通知；
 *  提供 Channel 变为活动的或者非活动的通知；
 *  提供当 Channel 注册到 EventLoop 或者从 EventLoop 注销时的通知；
 *  提供有关用户自定义事件的通知。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 *
 */
public class NettyNioServer {
    public void server(int port) throws Exception {
        final ByteBuf buf =
                Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n",
                        Charset.forName("UTF-8")));
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch)
                                              throws Exception {
                                              ch.pipeline().addLast(
                                                  new ChannelInboundHandlerAdapter() {
                                                      @Override
                                                      public void channelActive(
                                                              ChannelHandlerContext ctx) throws Exception {
                                                                ctx.writeAndFlush(buf.duplicate())
                                                                  .addListener(
                                                                          ChannelFutureListener.CLOSE);
                                                      }
                                                  });
                                      }
                                  }
                    );
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}

