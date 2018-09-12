package com.example.eurekaregisterslave1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @EnableEurekaServer 启动注册中心
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaRegisterSlave1Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaRegisterSlave1Application.class, args);
    }
}
