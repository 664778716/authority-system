package com.nm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nm.config.vo.RolePermissionVo;
import com.nm.config.vo.query.RoleQueryVo;
import com.nm.dao.UserMapper;
import com.nm.entity.Role;
import com.nm.dao.RoleMapper;
import com.nm.entity.User;
import com.nm.service.PermissionService;
import com.nm.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nm.utils.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PermissionService permissionService;

    @Override
    public IPage<Role> findRoleListByUserId(IPage<Role> page, RoleQueryVo roleQueryVo) {
        // 创建条件构造器
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        // 角色名称
        queryWrapper.like(!ObjectUtils.isEmpty(roleQueryVo.getRoleName()), "role_name", roleQueryVo.getRoleName());
        // 排序
        queryWrapper.orderByAsc("id");
        // 根据用户id查询角色列表
        User user=userMapper.selectById(roleQueryVo.getUserId());
        // 如果用户不为空,且不是管理员,则只能查询自己的角色列表
        if(user!=null&& !ObjectUtils.isEmpty(user.getIsAdmin())&&user.getIsAdmin()!=1){
            queryWrapper.eq("create_user",roleQueryVo.getUserId());
        }
        return baseMapper.selectPage(page,queryWrapper);
        }

    /**
     * 保存角色权限关系
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    @Override
    public boolean saveRolePermission(Long roleId, List<Long> permissionIds) {
        //删除该角色对应的权限信息
        baseMapper.deleteRolePermission(roleId);
        //保存角色权限
        return baseMapper.saveRolePermission(roleId,permissionIds)>0;
    }

    /**
     * 分配权限-查询权限树数据
     */
    @GetMapping("/getAssignPermissionTree")
    public Result getAssignPermissionTree(Long userId,Long roleId){
        // 调用查询权限树数据的方法
        RolePermissionVo permissionVo=permissionService.findPermissionTree(userId,roleId);
        // 返回数据
        return  Result.ok(permissionVo);
    }
}
