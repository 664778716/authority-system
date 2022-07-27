package com.nm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nm.config.vo.RolePermissionVo;
import com.nm.config.vo.query.PermissionQueryVo;
import com.nm.dao.UserMapper;
import com.nm.entity.Permission;
import com.nm.dao.PermissionMapper;
import com.nm.entity.User;
import com.nm.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nm.utils.MenuTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Resource
    private UserMapper userMapper;

    /**
     * 根据用户id查询权限菜单列表
     */
    @Override
    public List<Permission> findPermissionListByUserId(Long userId) {
        return baseMapper.findPermissionListByUserId(userId);
    }

    /**
     * 查询菜单列表
     *
     * @return
     */
    @Override
    public List<Permission> findPermissionList(PermissionQueryVo permissionQueryVo) {
        // 创建条件构造器对象
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        // 设置排序的字段
        queryWrapper.orderByAsc("order_num");
        // 调用查询菜单列表的方法
        List<Permission> permissionList = baseMapper.selectList(queryWrapper);
        List<Permission> menuTree = MenuTree.makeMenuTree(permissionList, 0L);
        return menuTree;
    }

    /**
     * 查询上级菜单列表
     *
     * @return
     */
    @Override
    public List<Permission> findParentPermissionList() {
        // 创建条件构造器对象
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        // 只查询目录和菜单的数据
        queryWrapper.in("type", Arrays.asList(0, 1));
        // 设置排序的字段
        queryWrapper.orderByAsc("order_num");
        // 调用查询菜单列表的方法
        List<Permission> permissionList = baseMapper.selectList(queryWrapper);
        // 构造顶级菜单的数据
        Permission permission = new Permission();
        permission.setId(0L);
        permission.setParentId(-1L);
        permission.setLabel("顶级菜单");
        permissionList.add(permission);
        // 生成菜单数据
        List<Permission> menuTree = MenuTree.makeMenuTree(permissionList, -1L);
        return menuTree;
    }

    /**
     * 检查菜单是否有子菜单
     *
     * @param id
     * @return
     */
    @Override
    public boolean hasChildrenOfPermission(Long id) {
        // 创建条件构造器对象
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        // 查询父级ID
        queryWrapper.eq("parent_id", id);
        // 判断数量是否大于0,如果大于0,说明有子菜单
        if (baseMapper.selectCount(queryWrapper) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查询分配权限树列表
     *
     * @param userId
     * @param roleId
     * @return
     */

    @Override
    public RolePermissionVo findPermissionTree(Long userId, Long roleId) {
        // 1.查询当前用户信息
        User user = userMapper.selectById(userId);
        List<Permission> list = null;
        // 2.判断当前用户角色是否是超级管理员,如果是超级管理员,则查询所有菜单信息,如果不是超级管理员,则查询当前用户的菜单信息
        if (!ObjectUtils.isEmpty(user.getIsAdmin()) && user.getIsAdmin() == 1) {
            // 查询所有菜单信息
            list = baseMapper.selectList(null);
        } else {
            // 根据ID查询当前用户的菜单信息
            list = baseMapper.findPermissionListByUserId(userId);
        }
        // 3.封装成树数据
        List<Permission> permissionList = MenuTree.makeMenuTree(list, 0L);
        // 4.查询要分配的角色的权限信息
        List<Permission> rolePermissionList = baseMapper.findPermissionListByRoleId(roleId);
        // 5.找出该角色存在的shuju
        List<Long> listIds = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    Optional.ofNullable(rolePermissionList).orElse(new ArrayList<>())
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(obj -> {
                                if (item.getId().equals(obj.getId())) {
                                    listIds.add(item.getId());
                                    return;
                                }
                            });
                });
        // 创建
        RolePermissionVo vo = new RolePermissionVo();
        vo.setPermissionList(permissionList);
        vo.setCheckList(listIds.toArray());
        return vo;
    }

}
