package com.example.userservice;

import com.example.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @EnableFeignClients  开启Feign功能，实现服务调用
 * @EnableDiscoveryClient  暴露给服务注册中心
 */
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class UserServiceApplication {

//    @Autowired
//    static RedisTemplate<String, User> redisTemplate;
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
//        System.out.println( redisTemplate.opsForValue().get("xiaoming"));
    }
}
