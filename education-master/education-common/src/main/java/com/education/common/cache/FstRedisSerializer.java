package com.education.common.cache;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FstRedisSerializer implements RedisSerializer<Object> {

    private static final Logger logger = LoggerFactory.getLogger(FstRedisSerializer.class);
    // 使用FastJsonRedisSerializer 代理
    private final FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);

    /**
     * 将对象进行序列化
     * @param value
     * @return
     * @throws SerializationException
     */
    @Override
    public byte[] serialize(Object value) throws SerializationException {
        FSTObjectOutput fstOut = null;
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            fstOut = new FSTObjectOutput(bytesOut);
            fstOut.writeObject(value);
            fstOut.flush();
            return bytesOut.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(fstOut != null)
                try {fstOut.close();} catch (IOException e) {
                    logger.error(e.getMessage(), e);}
        }
    }

    /**
     * 对象反序列化
     * @param bytes
     * @return
     * @throws SerializationException
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0)
            return null;

        FSTObjectInput fstInput = null;
        try {
            fstInput = new FSTObjectInput(new ByteArrayInputStream(bytes));
            try {
                return fstInput.readObject();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                // 解决使用RedisTemplate 获取计数器的值抛出空指针异常问题
                return fastJsonRedisSerializer.deserialize(bytes);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(fstInput != null)
                try {fstInput.close();} catch (IOException e) {logger.error(e.getMessage(), e);}
        }
    }
}
