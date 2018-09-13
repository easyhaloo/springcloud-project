package com.example.deptservice.controller;

import com.example.deptservice.entity.Dept;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/dept")
public class DeptConroller {

    static Map<String,Dept> depts = new ConcurrentHashMap<>();


    @ApiOperation(value = "获取部门信息",notes = "获取所有部门信息")
    @GetMapping("/getDepts")
    public List<Dept> getDepts(){
        List<Dept> list = new ArrayList<>(depts.values());
        return list;
    }


    @ApiOperation(value = "获取部门信息",notes = "根据提供的ID检索单个部门信息")
    @ApiImplicitParam(name = "id",value = "部门对应的ID",dataType = "String")
    @GetMapping("/getDeptById/{id}")
    public Dept getDeptById(@PathVariable("id") String id){
        return depts.get(id);
    }

    @ApiOperation(value = "添加部门信息",notes = "部门信息对应Dept实体")
    @ApiImplicitParam(name = "dept",value = "dept实体类的信息",required = true)
    @PostMapping("/save")
    public String save(@RequestBody Dept dept){
        depts.put(dept.getId(),dept);
        return "success";
    }


    @ApiOperation(value = "修改部门信息",notes = "通过部门ID检索部门信息，并修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "部门Id",required = true),
            @ApiImplicitParam(name = "dept",value = "dept实体类信息",required = true)
    }
    )
    @PutMapping("/update/{id}")
    public String update(@PathVariable("id")String id,@RequestBody Dept dept){
        Dept dept1 = depts.get(id);
        dept1.setName(dept.getName());
        depts.put(id,dept1);
        return "success";
    }


    @ApiOperation(value = "刪除部门信息",notes = "根据部门ID检索，如果存在即删除对应部门信息")
    @ApiImplicitParam(name = "id",value = "部门Id",required = true)
    @DeleteMapping("delete/{id}")
    public String delete(@PathVariable("id")String id){
        depts.remove(id);
        return "success";
    }
}
