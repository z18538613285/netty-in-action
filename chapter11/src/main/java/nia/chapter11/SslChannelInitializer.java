package nia.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * Listing 11.1 Adding SSL/TLS support
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;

    public SslChannelInitializer(SslContext context,  // 传入要使用的SslContext
        boolean startTls) { //如果设置为 true，第一个写入的消息将不会被加密(客户端应该设置为 true）
        this.context = context;
        this.startTls = startTls;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.newEngine(ch.alloc()); //对于每个 SslHandler 实例，都使用 Channel 的 ByteBufAllocator 从 SslContext 获取一个新的 SSLEngine
        ch.pipeline().addFirst("ssl", //将 SslHandler 作为第一个ChannelHandler 添加到ChannelPipeline 中
            new SslHandler(engine, startTls));
    }
}
