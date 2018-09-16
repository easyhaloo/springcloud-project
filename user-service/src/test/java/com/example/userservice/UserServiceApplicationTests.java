package com.example.userservice;

import com.example.userservice.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceApplicationTests {

    @Autowired
    RedisTemplate<String, User> redisTemplate;
    @Test
    public void contextLoads() {
    }



    @Test
    public void testRedisCluster(){
        User u = new User("001","xiaoming",10);
        redisTemplate.opsForValue().set("xiaoming",u);
      //  System.out.println(redisTemplate.opsForValue().get("xiaoming"));
        Assert.assertEquals(10,redisTemplate.opsForValue().get("xiaoming" +
                "" +
                "").getAge().intValue());
    }
}
