package com.nm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nm.config.vo.query.DepartmentQueryVo;
import com.nm.entity.Department;
import com.nm.service.DepartmentService;
import com.nm.utils.Result;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 查询部门列表
     * @param departmentQueryVo
     * @return
     */
    @GetMapping("/list")
    public Result list(DepartmentQueryVo departmentQueryVo){
        // 调用查询部门的方法
        List<Department> departmentList= departmentService.findDepartList(departmentQueryVo);
        // 返回数据
        return Result.ok(departmentList);
    }

    /**
     * 查询上级部门列表
     * @return
     */
    @GetMapping("/parent/list")
    public Result getParentDepartment(){
        // 调用查询部门的方法
        List<Department> departmentList= departmentService.findParentDepartment();
        // 返回数据
        return Result.ok(departmentList);
    }

    /**
     * 新增部门
     * @param department
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Department department){
        // 调用查询部门的方法
        if(departmentService.save(department)){
            return Result.ok().message("部门添加成功");
        }
        // 返回数据
        return Result.error().message("部门添加失败");
    }
    /**
     * 修改部门
     * @param department
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody Department department){
        // 调用查询部门的方法
        if(departmentService.updateById(department)){
            return Result.ok().message("部门修改成功");
        }
        // 返回数据
        return Result.error().message("部门修改失败");
    }
    /**
     * 删除部门
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id){
        // 调用查询部门的方法
        if(departmentService.removeById(id)){
            return Result.ok().message("部门删除成功");
        }
        // 返回数据
        return Result.error().message("部门删除失败");
    }

    /**
     * 查询某个部门下是否存在子部门
     * @param id
     * @return
     */
    @GetMapping("/check/{id}")
    public Result check(@PathVariable Long id){
        // 调用查询部门下是否有子部门的方法
        if(departmentService.hasChildrenOfDepartment(id)){
            return Result.exist().message("该部门下有子部门,不能删除");
        }
        // 调用查询部门下是否存在用户信息的方法
        if(departmentService.hasUserOfDepartment(id)){
            return Result.exist().message("该部门下有用户信息,不能删除");
        }
        return  Result.ok();
        }
    }


