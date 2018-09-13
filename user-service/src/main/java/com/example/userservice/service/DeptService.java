package com.example.userservice.service;

import com.example.userservice.entity.Dept;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "dept-service",path = "/dept",fallback = DeptService.DeptFallBack.class)
@Component
public interface DeptService {
    @GetMapping("/getDepts")
    List<Dept> getDeptList();

    @GetMapping("/getDeptById/{id}")
    Dept getDeptById(@PathVariable("id") String id);

    @PostMapping("/save")
    String save(Dept dept);

    @PutMapping("/update/{id}")
    String update(@PathVariable("id") String id,Dept dept);

    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable("id") String id);

    /**降級处理类
     * 需要@Component注解，方便Spring进行扫描，并实例化对象
     */
    @Component
    class DeptFallBack implements DeptService{

        @Override
        public List<Dept> getDeptList() {
            return null;
        }

        @Override
        public Dept getDeptById(String id) {
            return null;
        }

        @Override
        public String save(Dept dept) {
            return "存储失败 dept:"+dept;
        }

        @Override
        public String update(String id, Dept dept) {
            return "更新失败,id :"+id+" ,dept:"+dept;
        }

        @Override
        public String delete(String id) {
            return "删除失败 ,id: "+id;
        }
    }
}
