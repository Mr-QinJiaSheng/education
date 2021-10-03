package com.education.service.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/9 21:27
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SystemWebSocketHandler systemWebSocketHandler;
    @Autowired
    private HandshakeInterceptor handshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(systemWebSocketHandler, "/webSocket")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*"); // 配置允许跨域

        webSocketHandlerRegistry.addHandler(systemWebSocketHandler, "/sockJs/webSocket")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*").withSockJS(); // 配置允许跨域
    }
}
