package com.education.common.config;

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

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null || bytes.length == 0)
            return null;

        FSTObjectInput fstInput = null;
        try {
            fstInput = new FSTObjectInput(new ByteArrayInputStream(bytes));
            return fstInput.readObject();
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
