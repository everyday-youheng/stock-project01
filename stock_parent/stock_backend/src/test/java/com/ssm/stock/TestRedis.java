package com.ssm.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

/**
 * 测试redis
 */

@SpringBootTest
public class TestRedis {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test01(){
        // 1. 根据RedisTemplate模板对象，获取操作字符串类型的接口对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean aBoolean = valueOperations.setIfAbsent("city1234", "nanjing");

        //取值
        String city1234 = (String) valueOperations.get("city1234");
        System.out.println(city1234);
    }
}















