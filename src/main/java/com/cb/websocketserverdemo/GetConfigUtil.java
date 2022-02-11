package com.cb.websocketserverdemo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
@Data
@Component
@PropertySource({"classpath:application.properties"})
public class GetConfigUtil {
    @Value("${CONNECT_MAX_COUNT}")
    private String MAX_COUNT;

}
