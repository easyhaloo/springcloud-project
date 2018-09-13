package com.example.userservice;

import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * @EnableFeignClients  开启Feign功能，实现服务调用
 * @EnableDiscoveryClient  暴露给服务注册中心
 */
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
