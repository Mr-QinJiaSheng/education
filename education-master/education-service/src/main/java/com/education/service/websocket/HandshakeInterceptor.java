package com.education.service.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * websocket 连接拦截器
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/9 21:13
 */
@Component
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HandshakeInterceptor.class);
    /**
     * websocket 连接之前进行调用
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        logger.error("执行方法HandshakeInterceptor beforeHandshake");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();
            String sessionId = servletRequest.getSession().getId(); // 获取客户端会话id
            attributes.put("sessionId", sessionId);
        }
        return true;
    }

    /**
     * websocket 连接之后调用
     * @param request
     * @param response
     * @param wsHandler
     * @param ex
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        logger.error("执行方法HandshakeInterceptor afterHandshake");
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
