package nia.chapter4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Listing 4.5 Writing to a Channel
 *
 * Listing 4.6 Using a Channel from many threads
 *
 * 零拷贝
 * 零拷贝（zero-copy）是一种目前只有在使用 NIO 和 Epoll 传输时才可使用的特性。它使你可以快速
 * 高效地将数据从文件系统移动到网络接口，而不需要将其从内核空间复制到用户空间，其在像 FTP 或者
 * HTTP 这样的协议中可以显著地提升性能。但是，并不是所有的操作系统都支持这一特性。特别地，它对
 * 于实现了数据加密或者压缩的文件系统是不可用的——只能传输文件的原始内容。反过来说，传输已被
 * 加密的文件则不是问题。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ChannelOperationExamples {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    /**
     * Listing 4.5 Writing to a Channel
     *
     * 写出到 Channel
     */
    public static void writingToChannel() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; // Get the channel reference from somewhere
        // 创建持有要写数据的 ByteBuf
        ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
        // 写数据并冲刷它
        ChannelFuture cf = channel.writeAndFlush(buf);
        cf.addListener(new ChannelFutureListener() { // 添加 ChannelFutureListener 以便在写操作完成后接收通知
            @Override
            public void operationComplete(ChannelFuture future) {
                 // 写操作完成，并且没有错误发生
                if (future.isSuccess()) {
                    System.out.println("Write successful");
                } else {
                    // 记录错误
                    System.err.println("Write error");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    /**
     * Listing 4.6 Using a Channel from many threads
     *
     * Netty 的 Channel 实现是线程安全的，因此你可以存储一个到 Channel 的引用，并且每当
     * 你需要向远程节点写数据时，都可以使用它，即使当时许多线程都在使用它。
     */
    public static void writingToChannelFromManyThreads() {
        final Channel channel = CHANNEL_FROM_SOMEWHERE; // Get the channel reference from somewhere
        // 创建持有要写数据的ByteBuf
        final ByteBuf buf = Unpooled.copiedBuffer("your data",
                CharsetUtil.UTF_8);
        // 创建将数据写到Channel 的 Runnable
        Runnable writer = new Runnable() {
            @Override
            public void run() {
                channel.write(buf.duplicate());
            }
        };
        // 获取到线程池Executor 的引用
        Executor executor = Executors.newCachedThreadPool();

        // write in one thread
        // 递交写任务给线程池以便在某个线程中执行
        executor.execute(writer);

        // write in another thread
        // 递交另一个写任务以便在另一个线程中执行
        executor.execute(writer);
        //...
    }
}
