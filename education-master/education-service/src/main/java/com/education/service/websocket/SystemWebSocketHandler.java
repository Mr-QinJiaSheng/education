package com.education.service.websocket;

import com.education.common.cache.CacheBean;
import com.education.common.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/7/9 21:17
 */
@Component
public class SystemWebSocketHandler implements WebSocketHandler {

  //  @Autowired
  //  private CacheBean redisCacheBean;
    private static final Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);

    private Map<String, WebSocketSession> sessionMap = new HashMap<>();

  //  private static final String WEB_SOCKET_SESSION_CACHE = "websocket:session";

    /**
     * websocket 连接就绪时进行调用
     * @param webSocketSession
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

        logger.info("执行方法 afterConnectionEstablished");
        Map<String, Object> attrs = webSocketSession.getAttributes();
        String sessionId = (String) attrs.get("sessionId");
        if (!sessionMap.containsKey(sessionId)) {
            // 将webSocketSession 对象存放到redis
            sessionMap.put(sessionId, webSocketSession);
        }
      //  redisCacheBean.put(WEB_SOCKET_SESSION_CACHE, sessionId, webSocketSession);
    }

    /**
     * 向客户端发送消息
     * @param sessionId
     * @param message
     */
    public void sendMessage(String sessionId, String message) {
        WebSocketSession webSocketSession = sessionMap.get(sessionId);
        try {
            if (ObjectUtils.isNotEmpty(webSocketSession)) {
                webSocketSession.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            logger.error("发送消息异常", e);
        }
    }

    public void clearWebSocketSession(String sessionId) {
        this.sessionMap.remove(sessionId); // 清除websocket session会话信息
    }

    /**
     * 用来处理与客户端的消息推送
     * @param webSocketSession
     * @param webSocketMessage
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        logger.info("执行方法 handleMessage");
    }

    /**
     * websocket 连接异常时调用
     * @param webSocketSession
     * @param throwable
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.info("websocket 连接异常");
    }

    /**
     * websocket 关闭连接时调用
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("websocket 关闭成功");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
