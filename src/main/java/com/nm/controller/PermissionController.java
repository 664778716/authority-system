package com.nm.controller;


import com.nm.config.vo.query.PermissionQueryVo;
import com.nm.entity.Permission;
import com.nm.service.PermissionService;
import com.nm.utils.Result;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    /**
     * 查询菜单列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result getMenuList(PermissionQueryVo permissionQueryVo) {
        // 调用查询菜单列表的方法
        List<Permission> permissionList = permissionService.findPermissionList(permissionQueryVo);
        // 返数据
        return Result.ok(permissionList);
    }

    /**
     * 查询上级菜单列表
     *
     * @return
     */
    @GetMapping("/parent/list")
    public Result getParentList() {
        // 调用查询上级菜单列表的方法
        List<Permission> permissionList = permissionService.findParentPermissionList();
        // 返数据
        return Result.ok(permissionList);
    }

    /**
     * 添加菜单
     * @param permission
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Permission permission) {
        // 调用添加菜单的方法
        if (permissionService.save(permission)) {
            return Result.ok().message("菜单添加成功");
        }
        return Result.error().message("菜单添加失败");

    }

    /**
     * 修改菜单
     * @param permission
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody Permission permission) {
        // 调用修改菜单的方法
        if (permissionService.updateById(permission)) {
            return Result.ok().message("菜单修改成功");
        }
        return Result.error().message("菜单修改失败");
    }
    /**
     * 删除菜单
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        // 调用删除菜单的方法
        if (permissionService.removeById(id)) {
            return Result.ok().message("菜单删除成功");
        }
        return Result.error().message("菜单删除失败");
    }
    /**
     * 检查菜单是否有子菜单
     */
    @GetMapping("/check/{id}")
    public Result check(@PathVariable("id") Long id) {
        // 调用检查菜单是否有子菜单的方法
        if (permissionService.hasChildrenOfPermission(id)) {
            return Result.error().message("该菜单有子菜单，不能删除");
        }
        return Result.ok();
    }
}

