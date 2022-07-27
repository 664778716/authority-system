package com.nm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nm.config.vo.query.UserQueryVo;
import com.nm.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nm
 * @since 2022-07-06
 */
public interface UserService extends IService<User> {
    /**
     *  根据用户名查询用户
     * @param username
     * @return
     */
    User findUserByUserName(String username);

    /**
     * 分页查询用户列表
     * @param page
     * @param userQueryVo
     * @return
     */
    IPage<User> findUserListByPage(IPage<User> page, UserQueryVo userQueryVo);
}
