package com.nm.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nm.config.vo.RolePermissionVo;
import com.nm.config.vo.query.RoleQueryVo;
import com.nm.dao.RolePermissionDTO;
import com.nm.entity.Role;
import com.nm.service.PermissionService;
import com.nm.service.RoleService;
import com.nm.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;

    /**
     * 分页查询角色列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result list(RoleQueryVo roleQueryVo) {
        // 创建分页对象
        IPage<Role> page = new Page<Role>(roleQueryVo.getPageNo(), roleQueryVo.getPageSize());
        // 调用分页查询方法
        roleService.findRoleListByUserId(page, roleQueryVo);
        // 返回结果
        return Result.ok(page);
    }

    /**
     * 添加角色
     */
    @PostMapping("/add")
    public Result add(@RequestBody Role role) {
        if (roleService.save(role)) {
            return Result.ok().message("角色添加成功!");
        }
        return Result.error().message("角色添加失败!");
    }

    /**
     * 修改角色
     */
    @PutMapping("/update")
    public Result update(@RequestBody Role role) {
        if (roleService.updateById(role)) {
            return Result.ok().message("角色修改成功!");
        }
        return Result.error().message("角色修改失败!");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        if (roleService.removeById(id)) {
            return Result.ok().message("角色删除成功!");
        }
        return Result.error().message("角色删除失败!");
    }

    /**
     * 分配权限-查询权限树数据
     *
     * @param userId * @param roleId
     *               * @return
     */

    @GetMapping("/getAssignPermissionTree")
    public Result getAssignPermissionTree(Long userId, Long roleId) {
        //调用查询权限树数据的方法
        RolePermissionVo permissionTree =
                permissionService.findPermissionTree(userId, roleId);
        //返回数据
        return Result.ok(permissionTree);
    }

    /**
     * 分配权限-保存权限数据
     *
     * @param rolePermissionDTO
     * @return
     */
    @PostMapping("/saveRoleAssign")
    public Result saveRoleAssign(@RequestBody RolePermissionDTO rolePermissionDTO) {
        if (roleService.saveRolePermission(rolePermissionDTO.getRoleId(),
                rolePermissionDTO.getList())) {
            return Result.ok().message("权限分配成功");
        } else {
            return Result.error().message("权限分配失败");
        }
    }


}

