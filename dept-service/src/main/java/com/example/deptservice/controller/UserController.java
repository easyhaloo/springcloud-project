package com.example.deptservice.controller;


import com.example.deptservice.entity.User;
import com.example.deptservice.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "获取用户列表", notes = "")
    @GetMapping("/getUsers")
    List<User> getUsers() {

        return userService.getUserList();
    }


    @ApiOperation(value = "创建用户", notes = "根据User实体对象创建用户")
    @ApiImplicitParam(name = "user", value = "用户实体的信息User", required = true)
    @PostMapping("/save")
    public String save(@RequestBody User user) {
        return userService.save(user);
    }

    @ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String")
    @GetMapping("getUserById/{id}")
    public User getUserById(@PathVariable("id") String id) {
        return userService.getUserById(id);
    }


    @ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    })
    @PutMapping(value = "update/{id}")
    public String update(@PathVariable String id, @RequestBody User user) {
        return userService.update(id,user);
    }

    @ApiOperation(value = "删除用户", notes = "根据url的id来指定删除对象")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String")
    @DeleteMapping(value = "delete/{id}")
    public String deleteUser(@PathVariable String id) {
        return userService.delete(id);
    }
}
