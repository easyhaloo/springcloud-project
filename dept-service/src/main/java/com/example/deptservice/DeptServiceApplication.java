package com.example.deptservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;


@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class DeptServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeptServiceApplication.class, args);
    }
}
