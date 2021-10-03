package com.education.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class IpUtils {

	private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

	/**
	 * 获取客户端ip地址
	 * @return
	 */
   public static String getAddressIp(HttpServletRequest request) {
	   String ip = request.getHeader("x-forwarded-for");
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	      ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	      ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	      ip = request.getHeader("HTTP_CLIENT_IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	      ip = request.getRemoteAddr();
	    }
	    return ip;
    }

    private static final String IP_SERVICE_URL = "http://ip.taobao.com/service/getIpInfo.php?ip=";

    /**
     * 获取ip地址
     * @param ip
     * @return
     */
    public static String getIpAddress(String ip){
		String address = null;
		try {
	//		{"code":0,"data":{"country":"中国","country_id":"CN","area":"华北","area_id":"100000","region":"北京市","region_id":"110000","city":"北京市","city_id":"110100","county":"","county_id":"-1","isp":"阿里巴巴","isp_id":"100098","ip":"47.94.12.108"}}
			String data = HttpKit.get(IP_SERVICE_URL + ip);
			JSONObject jsonObject = JSONObject.parseObject(data);
			if (ObjectUtils.isNotEmpty(jsonObject)) {
				if (jsonObject.getInteger("code") == 0) {
					JSONObject result = (JSONObject)jsonObject.get("data");
					address = (String) result.get("country") + result.get("region") + result.get("city");
				}
			}
			return address;
		} catch (Exception e) {
			logger.error("获取ip地址异常", e);
		}
		return null;
	}

	private static final String IP_URL = "https://apis.map.qq.com/ws/location/v1/ip";

    public static final String LBS_KEY = "MYOBZ-OOEW3-KYC3G-YWDXA-DMQJ6-SPBMH";
	public static String getAddressByIp(String ip) {
		Map params = new HashMap<>();
		params.put("ip", ip);
		params.put("key", LBS_KEY);
		try {
			String content = HttpKit.get(IP_URL, params);
			JSONObject jsonObject = JSONObject.parseObject(content);
			Integer status = jsonObject.getInteger("status");
			System.out.println(status);
			if (status == 0) { // 表示接口请求成功

				JSONObject result = (JSONObject) jsonObject.get("result");
				Map adInfo = (Map) result.get("ad_info");
				String nation = (String) adInfo.get("nation");
				if ("中国".equals(nation)) {
					nation = "";
				}
				String province = (String) adInfo.get("province"); // 获取省份
				String city = (String) adInfo.get("city");
				return nation + province + city;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}
}
