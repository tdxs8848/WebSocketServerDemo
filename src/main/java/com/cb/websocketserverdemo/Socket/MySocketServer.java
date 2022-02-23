package com.cb.websocketserverdemo.Socket;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Data
@Service
public class MySocketServer {

     static final Logger logger = LoggerFactory.getLogger(MySocketServer.class);
    //    @Value("${port}")
    private Integer port;
    private boolean started;
    private ServerSocket serverSocket;
    //使用多线程，需要线程池，防止并发过高时创建过多线程耗尽资源
    private ExecutorService threadPool = Executors.newCachedThreadPool();
//    public static final CopyOnWriteArraySet<MySocketServer> SOCKETS = new CopyOnWriteArraySet<>();

//    public void start(Integer port) {
//        this.port = port + 1;
//        try {
//            serverSocket = new ServerSocket(this.port);
//            started = true;
//            logger.info("Socket服务已启动，端口号为：{}", serverSocket.getLocalPort());
//        } catch (IOException e) {
//            logger.error("端口异常信息", e);
//            System.exit(0);
//        }
//        while (started) {
//            try {
//                Socket socket = serverSocket.accept();
//                Runnable runnable = () -> {
//                    try {
//                        //接收客户端数据
//                        StringBuilder xmlString = onMessage(socket);
//                        //处理逻辑：xmlStringToEsb为处理结果
//                        //返回给客户端
//                        assert xmlString != null;
////                        sendMessage(socket, xmlString.toString());
//                        socket.close();
//                    } catch (IOException e) {
//                        logger.error("threadPool Error:{}", e);
//                    }
//                };
//                //接收线程返回结果
//                Future future = threadPool.submit(runnable);
//                logger.info("isFuture:{}-------", future.isDone());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void myStart(Integer port) {
        this.port = port + 1;
        started = true;
        try {
            serverSocket = new ServerSocket(this.port);
            logger.info("Socket服务已启动，端口号为：{}", serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (started) {
            try {
                threadPool.execute(new SocketThread(serverSocket.accept()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


//        while (started) {
//            try (
//                    Socket clientSocket = serverSocket.accept();
//                    PrintWriter out =
//                            new PrintWriter(clientSocket.getOutputStream(), true);
//                    BufferedReader in = new BufferedReader(
//                            new InputStreamReader(clientSocket.getInputStream()));
//            ) {
//                String inputLine;
//                out.println("已连接至socket服务器");
//
//                while ((inputLine = in.readLine()) != null) {
//                    out.println("服务器已处理" + inputLine);
//                    if (inputLine.equals("Bye."))
//                        break;
//                }
//            } catch (IOException e) {
//
//                System.out.println(e.getMessage());
//            }
//        }
    }


    private static StringBuilder onMessage(Socket socket) {
        byte[] bytes = new byte[1024];
        int len;
        try {
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
            }
            //此处，需要关闭服务器的输出流，但不能使用inputStream.close().
            socket.shutdownInput();
            logger.info("收到消息:{}", sb);
            return sb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendMessage(Socket socket, String message) {
        try {
            //向客户端返回数据
            OutputStream outputStream = socket.getOutputStream();
            message = "服务端已处理消息：" + message;
            //首先需要计算得知消息的长度
            byte[] sendBytes = message.getBytes("UTF-8");
            //然后将消息的长度优先发送出去
//            outputStream.write(sendBytes.length >> 8);
//            outputStream.write(sendBytes.length);
            //然后将消息再次发送出去
            outputStream.write(sendBytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
class SocketThread extends Thread {
    private Socket socket = null;
    static final Logger logger = LoggerFactory.getLogger(SocketThread.class);
    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
            out.println("已连接至socket服务器");

            while ((inputLine = in.readLine()) != null) {

                if ( inputLine.equals("Client: SocketBreak") || inputLine.equals("SocketBreak") ) {
                    out.println("将断开您的连接");
                    break;
                }
                out.println("服务器已处理" + inputLine);
            }
            socket.close();
            logger.info("{}运行结束",socket);

        } catch (IOException e) {
            logger.error("threadPool Error:{}", Arrays.toString(e.getStackTrace()));
        }
    }
}
