package com.example.userservice.controller;

import com.example.userservice.entity.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/user")
public class UserController {


    /**
     * 模拟用户列表
     */
    static Map<String, User> users = new ConcurrentHashMap<>();

    @ApiOperation(value = "获取用户列表", notes = "")
    @GetMapping("/getUsers")
    List<User> getUsers() {
        List<User> list = new ArrayList<>(users.values());
        return list;
    }


    @ApiOperation(value = "创建用户", notes = "根据User实体对象创建用户")
    @ApiImplicitParam(name = "user", value = "用户实体的信息User", required = true)
    @PostMapping("/save")
    public String save(@RequestBody User user) {
        users.put(user.getId(), user);
        return "success";
    }

    @ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String")
    @GetMapping("getUserById/{id}")
    public User getUserById(@PathVariable("id") String id) {
        return users.get(id);
    }


    @ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "User")
    })
    @PutMapping(value = "update/{id}")

    public String update(@PathVariable String id, @RequestBody User user) {
        User u = users.get(id);
        u.setName(user.getName());
        u.setAge(user.getAge());
        users.put(id, u);
        return "success";
    }

    @ApiOperation(value = "删除用户", notes = "根据url的id来指定删除对象")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String")
    @DeleteMapping(value = "delete/{id}")
    public String deleteUser(@PathVariable String id) {
        users.remove(id);
        return "success";
    }


}
