package com.example.userservice.controller;

import com.example.userservice.entity.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation(value="获取用户列表", notes="")
    @GetMapping("/getUsers")
    List<User> getUsers(){
        return null;
    }
}
