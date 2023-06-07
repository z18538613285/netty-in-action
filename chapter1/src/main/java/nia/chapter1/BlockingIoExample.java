package nia.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kerr.
 *
 * Listing 1.1 Blocking I/O example
 */
public class BlockingIoExample {

    /**
     * Listing 1.1 Blocking I/O example
     *
     * 阻塞调用
     * 这段代码片段将只能同时处理一个连接，要管理多个并发客户端，需要为每个新的客户端
     * Socket 创建一个新的 Thread，
     * */
    public void serve(int portNumber) throws IOException {
        // 创建一个新的 ServerSocket，
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // 对 accept()方法的调用将被阻塞，直到一个连接建立
        Socket clientSocket = serverSocket.accept();
        // 这些流对象都派生于该套接字的流对象
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
        String request, response;
        // 处理循环开始
        while ((request = in.readLine()) != null) {
            if ("Done".equals(request)) {
                // 如果客户端发送了“Done”，则退出处理循环
                break;
            }
            // 请求被传递给服器的处理方法
            response = processRequest(request);
            // 服务器的响应被发送给了客户端
            out.println(response);
        }
    }

    private String processRequest(String request){
        return "Processed";
    }
}
