package com.nm.dao;

import com.nm.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据用户id查询权限菜单列表
     */
    List<Permission> findPermissionListByUserId(Long userId);
    /**
     * 根据角色ID查询权限菜单列表
     */
    List<Permission> findPermissionListByRoleId(Long roleId);

}
