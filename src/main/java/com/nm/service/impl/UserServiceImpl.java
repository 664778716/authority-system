package com.nm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nm.config.vo.query.UserQueryVo;
import com.nm.entity.User;
import com.nm.dao.UserMapper;
import com.nm.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findUserByUserName(String username) {
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("username", username);
        // 执行查询
        return baseMapper.selectOne(queryWrapper);

    }


    /**
     * 分页查询用户列表
     * @param page
     * @param userQueryVo
     * @return
     */
    @Override
    public IPage<User> findUserListByPage(IPage<User> page, UserQueryVo userQueryVo) {
        // 创建条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        // 部门编号
        queryWrapper.eq(!ObjectUtils.isEmpty(userQueryVo.getDepartmentId()), "department_id", userQueryVo.getDepartmentId());
        // 用户名称
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getUsername()), "username", userQueryVo.getUsername());
        // 真实姓名
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getRealName()),"real_name", userQueryVo.getRealName());
        // 电话
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getPhone()), "phone", userQueryVo.getPhone());
        // 执行查询
        return baseMapper.selectPage(page, queryWrapper);
    }
}
