package com.example.deptservice.service;


import com.example.deptservice.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通过Feign调用user-service模块的服务，并使用hystrix熔断处理，通过fallback属性来进行降级处理，防止远程服务调用，出现超时出错问题
 * 所造成的请求阻塞。
 */
@FeignClient(value = "user-service",path = "/user",fallback = UserService.UserFallBack.class)
@Component
public interface UserService {

    @GetMapping("/getUsers")
    List<User> getUserList();

    @GetMapping("/getUserById/{id}")
    User getUserById(@PathVariable("id") String id);

    @PostMapping("/save")
    String save(User user);

    @PutMapping("/update/{id}")
    String update(@PathVariable("id") String id,User user);

    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable("id") String id);


    /**
     * 降级处理类
     */
     @Component
     class UserFallBack implements UserService{

        @Override
        public List<User> getUserList() {
            return null;
        }

        @Override
        public User getUserById(String id) {
            return null;
        }

        @Override
        public String save(User user) {
            return "error";
        }

        @Override
        public String update(String id, User user) {
            return "error";
        }

        @Override
        public String delete(String id) {
            return "error";
        }
    }
}
