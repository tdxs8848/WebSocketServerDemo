package com.cb.websocketserverdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.SpringProperties;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.tiles3.SpringBeanPreparerFactory;

import javax.websocket.*;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/")
public class MyWebSocketServer {

    /**
     * 静态变量，用来记录当前连接数
     */
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger();

    private static final int CONNECT_MAX_COUNT = 200;

    /**
     * concurrent线程安全set，用来存放每个客户端对应的MyWebSocketServer对象
     */
    public static final CopyOnWriteArraySet<MyWebSocketServer> WEBSOCKETS = new CopyOnWriteArraySet<>();

    /**
     * 与每个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接成功调用的方法
     *
     * @param session 可选的参数。与某个客户端的连接会话
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //获取最大连接数
        if (ONLINE_COUNT.get() > CONNECT_MAX_COUNT) {
            sendMessage("服务器连接数：" + ONLINE_COUNT.get() + ",连接数已超出最大值,将断开您的连接");
            WEBSOCKETS.remove(this);
            log.error("当前连接数为：{}，已超出最大连接数：{}", ONLINE_COUNT.get(),CONNECT_MAX_COUNT );
        } else {
            WEBSOCKETS.add(this);
            ONLINE_COUNT.incrementAndGet();
            log.info("有新的连接加入！当前连接总数为{}", ONLINE_COUNT.get());
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //防止连接数出现负数
        if (WEBSOCKETS.remove(this)) {
            ONLINE_COUNT.decrementAndGet();
        }
        log.info("有一连接关闭！当前连接总数为{}", ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("WebSocket接收消息：sessionId为{},消息为：{}", this.session.getId(), message);
        sendMessage("当前连接数为：" + ONLINE_COUNT + "  已处理消息：" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket接收消息错误{},sessionId为{}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        log.info("WebSocket发送消息：sessionId为{},消息为：{}", this.session.getId(), message);
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("WebSocket发送消息错误{},sessionId为{}", e.getMessage(), session.getId(), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyWebSocketServer that = (MyWebSocketServer) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }
}