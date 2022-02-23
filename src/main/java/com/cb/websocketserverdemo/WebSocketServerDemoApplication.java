package com.cb.websocketserverdemo;

import com.cb.websocketserverdemo.Socket.MySocketServer;
import com.cb.websocketserverdemo.Start.ServerStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Objects;

@SpringBootApplication
public class WebSocketServerDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WebSocketServerDemoApplication.class, args);
        //获取websocket端口号
        HashMap<String,Integer> webSocketPort = (HashMap<String, Integer>) Objects.requireNonNull(run.getEnvironment().getPropertySources().get("server.ports")).getSource();
        run.getBean(ServerStart.class).startServer(webSocketPort.get("local.server.port"));
//        run.getBean(MySocketServer.class).start();
    }

}
