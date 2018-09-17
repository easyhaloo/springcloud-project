package com.example.userservice.mapper;

import com.example.userservice.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {

    @Select("select * from users")
    @Results({
            @Result(property = "id",column = "id",javaType = String.class),
            @Result(property = "name",column = "name",javaType = String.class),
            @Result(property = "age",column = "age",javaType = Integer.class)
    })
    List<User> findAll();

    @Select("select * from users where id = #{id}")
    @Results({
            @Result(property = "id",column = "id",javaType = String.class),
            @Result(property = "name",column = "name",javaType = String.class),
            @Result(property = "age",column = "age",javaType = Integer.class)
    })
    User findById(String id);


    @Insert("insert into users(id,name,age) values (#{id},#{name},#{age})")
    void save(User user);

    @Update("update users set name = #{name},age = #{age} where id = #{id}")
    int update(User user);

    @Delete("delete from users id = #{id}")
    int removeById(String id);


}
