package com.education.admin.api;

import com.alibaba.fastjson.JSONObject;
import com.education.mapper.system.SystemAdminMapper;
import com.education.mapper.system.SystemRoleMapper;
import com.education.service.system.SystemAdminService;
import com.jfinal.kit.HttpKit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class EducationAdminApiApplicationTests {


    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private SystemAdminMapper systemAdminMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void deleteFile() {
        File file = new File("F:\\static\\images\\2020\\07\\18\\0ff5d94ee6de4ad5a7b138df7799a8b9.png");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testCache() {
        Map params = new HashMap<>();
        params.put("login_name", "testsfs");
        params.put("password", "123456");
        params.put("encrypt", "123456000");
        int result = systemAdminService.save(params);
        System.err.println(params);
       // redisTemplate.opsForValue().set("test", new LoginController(new SystemAdminService()));
      //  System.out.println(redisTemplate.opsForValue().get("test"));
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            String content = HttpKit.get("http://localhost/test");
            System.out.println(content);
        }
    }

    @Test
    @Transactional
    public void contextLoads() {
        Map params = new HashMap<>();
        params.put("admin_id", 1);
        params.put("role_id", 2);
        systemAdminMapper.save(params);
        this.saveRole();
        int i = 1 / 0;
    }

   // @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRole() {
        Map params = new HashMap<>();
        params.put("name", "test");
      //  systemRoleMapper.save(params);
    }

    private static final String IP_URL = "https://apis.map.qq.com/ws/location/v1/ip";

    @Test
    public void testIp() {
       Map params = new HashMap<>();
       params.put("ip", "223.104.10.172");
       params.put("key", "MYOBZ-OOEW3-KYC3G-YWDXA-DMQJ6-SPBMH");
       String content = HttpKit.get(IP_URL, params);
        JSONObject jsonObject = JSONObject.parseObject(content);
        Integer status = jsonObject.getInteger("status");
        System.out.println(status);
        if (status == 0) { // 表示接口请求成功

            JSONObject result = (JSONObject) jsonObject.get("result");
            Map adInfo = (Map) result.get("ad_info");
            String nation = (String) adInfo.get("nation");
            String province = (String) adInfo.get("province"); // 获取省份
            String city = (String) adInfo.get("city");
            System.out.println(nation + province + city);
        }
    }
}
