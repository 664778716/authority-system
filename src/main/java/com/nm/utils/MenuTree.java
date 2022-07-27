package com.nm.utils;

import com.nm.config.vo.RouterVo;
import com.nm.entity.Permission;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 菜单树工具类
 */
public class MenuTree {

    /**
     * 生成路由
     *
     * @param menuList 菜单列表
     * @param pid      父级id
     * @return
     */
    public static List<RouterVo> makeRouter(List<Permission> menuList, Long pid) {
        // 创建路由保存信息
        List<RouterVo> routerList = new ArrayList<RouterVo>();
        // 判断当前菜单是否为空,如果不为空则使用菜单列表,否则创建集合对象
        Optional.ofNullable(menuList).orElse(new ArrayList<Permission>())
                // 筛选不为空的菜单及与菜单父id相同的数据
                .stream().filter(item -> item != null && item.getParentId() == pid)
                .forEach(item -> {
                    // 创建路由对象
                    RouterVo router = new RouterVo();
                    // 设置路由名称
                    router.setName(item.getName());
                    // 设置路由路径
                    router.setPath(item.getPath());
                    // 判断当前菜单是否是一级菜单
                    if (item.getParentId() == 0L) {
                        // 一级菜单组件
                        router.setComponent("Layout");
                        // 显示路由
                        router.setAlwaysShow(true);
                    } else {
                        // 具体某一个组件
                        router.setComponent(item.getUrl());
                        // 表示折叠菜单
                        router.setAlwaysShow(false);
                    }
                    // 设置Meta信息
                    router.setMeta(router.new Meta(item.getLabel(), item.getIcon(), item.getCode().split(",")));
                    // 递归生成路由
                    List<RouterVo> children = makeRouter(menuList, item.getId()); // 子菜单
                    router.setChildren(children);// 设置子菜单
                    // 将路由信息添加到路由集合中
                    routerList.add(router);
                });
        // 返回路由集合
        return routerList;
    }

    /**
     * 生成菜单树
     *
     * @param menuList 菜单列表
     * @param pid      父级id
     * @return
     */

    public static List<Permission> makeMenuTree(List<Permission> menuList, Long pid) {
        // 创建路由保存信息
        List<Permission> permissionList = new ArrayList<Permission>();
        // 判断当前菜单是否为空,如果不为空则使用菜单列表,否则创建集合对象
        Optional.ofNullable(menuList).orElse(new ArrayList<Permission>())
                .stream().filter(item -> item != null && item.getParentId() == pid)
                .forEach(item -> {
                    // 创建权限菜单
                    Permission permission = new Permission();
                    // 将原有的属性复制到新的对象中
                    BeanUtils.copyProperties(item, permission);
                    // 获取每个item对象的子菜单,递归生成菜单树
                    List<Permission> children = makeMenuTree(menuList, item.getId());
                    // 设置子菜单
                    permission.setChildren(children);
                    // 将菜单对象添加到菜单集合中
                    permissionList.add(permission);
                });
        // 返回菜单集合
        return permissionList;
    }
}
