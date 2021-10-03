package com.education.common.model;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/19 15:31
 */
public class JwtToken {

    private final String secret;

    private static final Logger logger = LoggerFactory.getLogger(JwtToken.class);

    public JwtToken(String secret) {
        if (secret == null) {
            throw new NullPointerException("secret value cant not ben null");
        }
        this.secret = secret;
    }

    /**
     * 生成jwt token
     * @param value
     * @param expirationTime
     * @return
     */
    public String createToken(Object value, long expirationTime) {
        // 生成SecretKey 对象
        SecretKey secretKey = this.createSecretKey();
        String jsonValue = JSONObject.toJSONString(value);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date();
        JwtBuilder jwtBuilder = Jwts.builder().setIssuedAt(now).setSubject(jsonValue).signWith(signatureAlgorithm, secretKey);
        if (expirationTime > 0L) {
            long expMillis = nowMillis + expirationTime;
            Date exp = new Date(expMillis);
            jwtBuilder.setExpiration(exp);  // 设置token过期时间
        }
        return jwtBuilder.compact();
    }

    private SecretKey createSecretKey() {
        byte[] bytes = DatatypeConverter.parseBase64Binary(secret);
        return new SecretKeySpec(bytes, 0, bytes.length, "AES");
    }

    /**
     * 解析token
     * @param token
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T parserToken(String token, Class<T> clazz) {
        SecretKey secretKey = this.createSecretKey();
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            String subject = claims.getSubject();
            return (T) JSONObject.parseObject(subject, clazz);
        } catch (SignatureException | MalformedJwtException e) {
           // token 签名失败
            logger.error("token 签名失败", e);
        } catch (ExpiredJwtException e) {
            // token 已过期
            logger.error("token 已过期", e);
        } catch (Exception e) {
            logger.error("token验证异常", e);
        }
        return null;
    }
}
