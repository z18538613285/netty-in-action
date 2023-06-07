package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        /**
         * 设置端口值（如果端口参数的格式不正确，则抛出一个NumberFormatException）
         */
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }
        int port = Integer.parseInt(args[0]);
        // 调用服务器的 start()方法
        new EchoServer(port).start();
    }

    /**
     * 传输
     * 在这一节中，你将遇到术语传输。在网络协议的标准多层视图中，传输层提供了端到端的或者主机
     * 到主机的通信服务。
     * 因特网通信是建立在 TCP 传输之上的。除了一些由 Java NIO 实现提供的服务器端性能增强之外，
     * NIO 传输大多数时候指的就是 TCP 传输。
     *
     * @throws Exception
     */
    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建 ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            /**
             * 你使用了一个特殊的类——ChannelInitializer。这是关键。当一个新的连接
             * 被接受时，一个新的子 Channel 将会被创建，而 ChannelInitializer 将会把一个你的
             * EchoServerHandler 的实例添加到该 Channel 的 ChannelPipeline 中。正如我们之前所
             * 解释的，这个 ChannelHandler 将会收到有关入站消息的通知。
             */
            b.group(group)
                .channel(NioServerSocketChannel.class) // 指定所使用的 NIO传输 Channel
                .localAddress(new InetSocketAddress(port)) // 使用指定的端口设置套接字地址
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    /**
                     * 添加一个EchoServerHandler 到子Channel的 ChannelPipeline
                     * @param ch
                     * @throws Exception
                     */
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // EchoServerHandler 被标注为@Shareable，所以我们可以总是使用同样的实例
                        ch.pipeline().addLast(serverHandler);
                    }
                    // 这里对于所有的客户端连接来说，都会使用同一个 EchoServerHandler，因为其被标注为@Sharable，
                });

            // 异步地绑定服务器；调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            // 获取 Channel 的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
