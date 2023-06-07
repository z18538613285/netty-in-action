package nia.chapter5;

import io.netty.buffer.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ByteProcessor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

import static io.netty.channel.DummyChannelHandlerContext.DUMMY_INSTANCE;

/**
 * Created by kerr.
 *
 * Listing 5.1 Backing array
 *
 * Listing 5.2 Direct buffer data access
 *
 * Listing 5.3 Composite buffer pattern using ByteBuffer
 *
 * Listing 5.4 Composite buffer pattern using CompositeByteBuf
 *
 * Listing 5.5 Accessing the data in a CompositeByteBuf
 *
 * Listing 5.6 Access data
 *
 * Listing 5.7 Read all data
 *
 * Listing 5.8 Write data
 *
 * Listing 5.9 Using ByteBufProcessor to find \r
 *
 * Listing 5.10 Slice a ByteBuf
 *
 * Listing 5.11 Copying a ByteBuf
 *
 * Listing 5.12 get() and set() usage
 *
 * Listing 5.13 read() and write() operations on the ByteBuf
 *
 * Listing 5.14 Obtaining a ByteBufAllocator reference
 *
 * Listing 5.15 Reference counting
 *
 * Listing 5.16 Release reference-counted object
 *
 * @tips
 * 下面是一些 ByteBuf API 的优点：
 *  它可以被用户自定义的缓冲区类型扩展；
 *  通过内置的复合缓冲区类型实现了透明的零拷贝；
 *  容量可以按需增长（类似于 JDK 的 StringBuilder）；
 *  在读和写这两种模式之间切换不需要调用 ByteBuffer 的 flip()方法；
 *  读和写使用了不同的索引；
 *  支持方法的链式调用；
 *  支持引用计数；
 *  支持池化。
 *
 */
public class ByteBufExamples {
    private final static Random random = new Random();
    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;
    /**
     * Listing 5.1 Backing array
     *
     * 堆缓冲区
     * 最常用的 ByteBuf 模式是将数据存储在 JVM 的堆空间中。这种模式被称为支撑数组
     * （backing array），它能在没有使用池化的情况下提供快速的分配和释放。
     * 这种方式，非常适合于有遗留的数据需要处理的情况。
     */
    public static void heapBuffer() {
        ByteBuf heapBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        // 检查 ByteBuf 是否有一个支撑数组
        if (heapBuf.hasArray()) {
            // 获取对该数组的引用
            byte[] array = heapBuf.array();
            // 计算第一个字节的偏移量
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            // 获取可读字节数
            int length = heapBuf.readableBytes();
            // 使用数组、偏移量和长度作为参数调用你的方法
            handleArray(array, offset, length);
        }
        /**
         * 注意 当 hasArray()方法返回 false 时，尝试访问支撑数组将触发一个 Unsupported
         * OperationException。这个模式类似于 JDK 的 ByteBuffer 的用法。
         */
    }

    /**
     * Listing 5.2 Direct buffer data access
     *
     * 直接缓冲区是另外一种 ByteBuf 模式。我们期望用于对象创建的内存分配永远都来自于堆
     * 中，但这并不是必须的——NIO 在 JDK 1.4 中引入的 ByteBuffer 类允许 JVM 实现通过本地调
     * 用来分配内存。这主要是为了避免在每次调用本地 I/O 操作之前（或者之后）将缓冲区的内容复
     * 制到一个中间缓冲区（或者从中间缓冲区把内容复制到缓冲区）。
     */
    public static void directBuffer() {
        ByteBuf directBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        if (!directBuf.hasArray()) {
            // 检查 ByteBuf 是否由数组支撑。如果不是，则这是一个直接缓冲区
            // 获取可读字节数
            int length = directBuf.readableBytes();
            // 分配一个新的数组来保存具有该长度的字节数据
            byte[] array = new byte[length];
            // 将字节复制到该数组
            directBuf.getBytes(directBuf.readerIndex(), array);
            handleArray(array, 0, length);
        }
    }

    /**
     * Listing 5.3 Composite buffer pattern using ByteBuffer
     *
     * 第三种也是最后一种模式使用的是复合缓冲区，它为多个 ByteBuf 提供一个聚合视图。在
     * 这里你可以根据需要添加或者删除 ByteBuf 实例，这是一个 JDK 的 ByteBuffer 实现完全缺
     * 失的特性。
     */
    public static void byteBufferComposite(ByteBuffer header, ByteBuffer body) {
        // Use an array to hold the message parts
        ByteBuffer[] message =  new ByteBuffer[]{ header, body };

        // Create a new ByteBuffer and use copy to merge the header and body
        ByteBuffer message2 =
                ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();
    }


    /**
     * Listing 5.4 Composite buffer pattern using CompositeByteBuf
     */
    public static void byteBufComposite() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headerBuf = BYTE_BUF_FROM_SOMEWHERE; // can be backing or direct
        ByteBuf bodyBuf = BYTE_BUF_FROM_SOMEWHERE;   // can be backing or direct
        messageBuf.addComponents(headerBuf, bodyBuf); // 将 ByteBuf 实例追加到 CompositeByteBuf
        //...
        messageBuf.removeComponent(0); // remove the header // 删除位于索引位置为 0（第一个组件）的 ByteBuf
        // 循环遍历所有的 ByteBuf 实例
        for (ByteBuf buf : messageBuf) {
            System.out.println(buf.toString());
        }
    }

    /**
     * Listing 5.5 Accessing the data in a CompositeByteBuf
     * CompositeByteBuf 可能不支持访问其支撑数组，因此访问
     * 据类似于（访问）直接缓冲区的模式，
     */
    public static void byteBufCompositeArray() {
        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        int length = compBuf.readableBytes();
        byte[] array = new byte[length];
        compBuf.getBytes(compBuf.readerIndex(), array);
        handleArray(array, 0, array.length);
    }

    /**
     * Listing 5.6 Access data
     * 随机访问索引
     */
    public static void byteBufRelativeAccess() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.getByte(i);
            System.out.println((char) b);
        }
    }

    /**
     * Listing 5.7 Read all data
     */
    public static void readAllData() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }
    }

    /**
     * Listing 5.8 Write data
     */
    public static void write() {
        // Fills the writable bytes of a buffer with random integers.
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        while (buffer.writableBytes() >= 4) {
            buffer.writeInt(random.nextInt());
        }
    }

    /**
     * Listing 5.9 Using ByteProcessor to find \r
     *
     * use {@link io.netty.buffer.ByteBufProcessor in Netty 4.0.x}
     */
    public static void byteProcessor() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        int index = buffer.forEachByte(ByteProcessor.FIND_CR);
    }

    /**
     * Listing 5.9 Using ByteBufProcessor to find \r
     *
     * use {@link io.netty.util.ByteProcessor in Netty 4.1.x}
     */
    public static void byteBufProcessor() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        int index = buffer.forEachByte(ByteBufProcessor.FIND_CR);
    }

    /**
     * Listing 5.10 Slice a ByteBuf
     *
     * 对 ByteBuf 进行切片
     */
    public static void byteBufSlice() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        // 创建该 ByteBuf 从索引 0 开始到索引 15结束的一个新切片
        ByteBuf sliced = buf.slice(0, 15);
        // 将打印“Netty in Action”
        System.out.println(sliced.toString(utf8));
        // 更新索引 0 处的字节
        buf.setByte(0, (byte)'J');
        // 将会成功，因为数据是共享的，对其中一个所做的更改对另外一个也是可见的
        assert buf.getByte(0) == sliced.getByte(0);
    }

    /**
     * Listing 5.11 Copying a ByteBuf
     *
     * 复制一个 ByteBuf
     */
    public static void byteBufCopy() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        // 创建该 ByteBuf 从索引 0 开始到索引 15结束的分段的副本
        ByteBuf copy = buf.copy(0, 15);
        System.out.println(copy.toString(utf8));
        buf.setByte(0, (byte)'J');
        // 将会成功，因为数据不是共享的
        assert buf.getByte(0) != copy.getByte(0);
    }

    /**
     * Listing 5.12 get() and set() usage
     */
    public static void byteBufSetGet() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.out.println((char)buf.getByte(0));// 打印第一个字符'N'
        int readerIndex = buf.readerIndex(); //存储当前的 readerIndex和 writerIndex
        int writerIndex = buf.writerIndex();
        buf.setByte(0, (byte)'B'); // 将索引 0 处的字节更新为字符'B'
        System.out.println((char)buf.getByte(0)); // 打印第一个字符，现在是'B'
        assert readerIndex == buf.readerIndex(); // 将会成功，因为这些操作并不会修改相应的索引
        assert writerIndex == buf.writerIndex();
    }

    /**
     * Listing 5.13 read() and write() operations on the ByteBuf
     */
    public static void byteBufWriteRead() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.out.println((char)buf.readByte()); // 打印第一个字符'N'
        int readerIndex = buf.readerIndex(); // 存储当前 readerIndex
        int writerIndex = buf.writerIndex();
        buf.writeByte((byte)'?'); // 将字符'?'追加到缓冲区
        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex(); // 将会成功，因为 writeByte()方法移动了 writerIndex
    }

    private static void handleArray(byte[] array, int offset, int len) {}

    /**
     * Listing 5.14 Obtaining a ByteBufAllocator reference
     */
    public static void obtainingByteBufAllocatorReference(){
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        // 按需分配
        ByteBufAllocator allocator = channel.alloc(); //从 Channel 获取一个到ByteBufAllocator 的引用
        //...
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere
        ByteBufAllocator allocator2 = ctx.alloc(); // 从 ChannelHandlerContext 获取一个到 ByteBufAllocator 的引用
        //...
    }

    /**
     * Listing 5.15 Reference counting
     * */
    public static void referenceCounting(){
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        ByteBufAllocator allocator = channel.alloc(); //从 Channel 获取ByteBufAllocator
        //...
        ByteBuf buffer = allocator.directBuffer(); // 从 ByteBufAllocator分配一个 ByteBuf
        assert buffer.refCnt() == 1;// 检查引用计数是否为预期的 1
        //...
    }

    /**
     * Listing 5.16 Release reference-counted object
     */
    public static void releaseReferenceCountedObject(){
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        boolean released = buffer.release(); //减少到该对象的活动引用。当减少到 0 时，该对象被释放，并且该方法返回 true
        //...
    }


}
