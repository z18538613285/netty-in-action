package nia.chapter4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Listing 4.1 Blocking networking without Netty
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class PlainOioServer {
    public void serve(int port) throws IOException {
        // 将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);
        try {
            for(;;) {
                // 接收连接
                final Socket clientSocket = socket.accept();
                System.out.println(
                        "Accepted connection from " + clientSocket);
                // 创建一个新的的线程来处理该连接
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            // 将消息写给已连接的客户端
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(
                                    Charset.forName("UTF-8")));
                            out.flush();
                            // 关闭连接
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // ignore on close
                            }
                        }
                    }
                }).start(); // 启动线程
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
