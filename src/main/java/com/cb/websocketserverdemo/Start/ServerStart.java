package com.cb.websocketserverdemo.Start;

import com.cb.websocketserverdemo.Socket.MySocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServerStart {
    //启动类
    protected static final Logger logger = LoggerFactory.getLogger(ServerStart.class);
    public void startServer(Integer webSocketPort) {
        //websocket随着项目启动
        logger.info("WebSocket服务已启动，端口号为：{}", webSocketPort);

        MySocketServer mySocketServer = new MySocketServer();
        mySocketServer.myStart(webSocketPort);


    }
}
