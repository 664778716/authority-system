package com.nm.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nm.config.vo.query.UserQueryVo;
import com.nm.entity.User;
import com.nm.service.UserService;
import com.nm.utils.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表
     *
     * @return
     */
    @GetMapping("/listAll")
    public Result listAll() {
        return Result.ok(userService.list());
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        // 查询用户
        User item = userService.findUserByUserName(user.getUsername());
        // 判断用户对象是否为空
        if (item != null) {
            return Result.error().message("该登录名称已被使用,请重新输入!");
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 调用保存用户信息的方法
        if (userService.save(user)) {
            return Result.ok().message("用户添加成功!");
        }
        return Result.error().message("用户添加失败!");
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    public Result list(UserQueryVo userQueryVo) {
        // 创建分页对象
        IPage<User> page = new Page<User>(userQueryVo.getPageNo(),userQueryVo.getPageSize());
        // 调用分页查询方法
        userService.findUserListByPage(page,userQueryVo);
        // 返回数据
        return Result.ok(page);
    }
}

