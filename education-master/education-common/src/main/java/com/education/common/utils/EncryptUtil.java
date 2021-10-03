package com.education.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.UUID;


/**
 * 摘要算法工具类
 * @author zengjintao
 * 2017年3月17号上午11:40
 */
public class EncryptUtil {

	/**
	 * md5加密
	 * @param Md5key
	 * @param salt 时间戳
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMd5(String Md5key, String salt) {
		String key = DigestUtils.md5Hex(Md5key) + "&" + salt;
		return DigestUtils.md5Hex(key);
	}

	public static String generatorKey(){
		return getMd5(UUID.randomUUID().toString());
	}

	/**
	 * md5密码加密
	 * @param key
	 * @return
	 */
	public static String getMd5(String key) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] bytes = digest.digest(key.getBytes());
			return Hex.encodeHexString(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * SHA摘要算法
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getSHA(String key) throws NoSuchAlgorithmException{
		MessageDigest sha = MessageDigest.getInstance("SHA");
		byte[] by = sha.digest(key.getBytes());
	    return Hex.encodeHexString(by);
	}
	
	/**
	 * 
	 * @param salt 可以是时间戳
	 * @return
	 */
	public static String encodeSalt(String salt) {
		return DigestUtils.sha1Hex(salt);
	}
	

	/**
	 * SHA加密算法
	 * @param key
	 * @return
	 */
	public static String getSHA1(String key) {
		try {
	      MessageDigest digest = MessageDigest.getInstance("SHA");
	      byte[] bytes = digest.digest(key.getBytes());
	      //将字节数组转换成16进制字符串
	      return Hex.encodeHexString(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}


	private static String getBase64(String str) {
	   if (ObjectUtils.isEmpty(str)) {
			return null;
		}
		byte[] b = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	   return new BASE64Encoder().encode(b);
	}
	
	// base64解密
	public static final String getStrFromBase64(String str) {
        byte[] b = null;
        String result = null;
        if (str != null&&!"".equals(str.trim())) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(str);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
