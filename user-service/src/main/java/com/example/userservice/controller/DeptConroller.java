package com.example.userservice.controller;

import com.example.userservice.entity.Dept;
import com.example.userservice.service.DeptService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/d")
public class DeptConroller {

    @Autowired
    private DeptService deptService;

    @ApiOperation(value = "获取部门信息",notes = "获取所有部门信息")
    @GetMapping("/getDepts")
    public List<Dept> getDepts(){

        return deptService.getDeptList();
    }


    @ApiOperation(value = "获取部门信息",notes = "根据提供的ID检索单个部门信息")
    @ApiImplicitParam(name = "id",value = "部门对应的ID",dataType = "String")
    @GetMapping("/getDeptById/{id}")
    public Dept getDeptById(@PathVariable("id") String id){
        return deptService.getDeptById(id);
    }

    @ApiOperation(value = "添加部门信息",notes = "部门信息对应Dept实体")
    @ApiImplicitParam(name = "dept",value = "dept实体类的信息",required = true)
    @PostMapping("/save")
    public String save(@RequestBody Dept dept){
        return deptService.save(dept);
    }


    @ApiOperation(value = "修改部门信息",notes = "通过部门ID检索部门信息，并修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "部门Id",required = true),
            @ApiImplicitParam(name = "dept",value = "dept实体类信息",required = true)
    }
    )
    @PutMapping("/update/{id}")
    public String update(@PathVariable("id")String id,@RequestBody Dept dept){

        return deptService.update(id,dept);
    }


    @ApiOperation(value = "刪除部门信息",notes = "根据部门ID检索，如果存在即删除对应部门信息")
    @ApiImplicitParam(name = "id",value = "部门Id",required = true)
    @DeleteMapping("delete/{id}")
    public String delete(@PathVariable("id")String id){

        return deptService.delete(id);
    }
}
