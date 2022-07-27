package com.nm.controller;

import com.nm.config.redis.RedisService;
import com.nm.config.vo.RouterVo;
import com.nm.config.vo.TokenVo;
import com.nm.entity.Permission;
import com.nm.entity.User;
import com.nm.entity.UserInfo;
import com.nm.utils.JwtUtils;
import com.nm.utils.MenuTree;
import com.nm.utils.Result;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 刷新token信息
 */
@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private RedisService redisService;


    /**
     * 刷新token
     *
     * @param request
     * @return
     */
    @PostMapping("/refreshToken")
    public Result refreshToken(HttpServletRequest request) {
        // 从Headers中获取token信息
        String token = request.getHeader("token");
        // 判断Headers头部是否存在token信息
        if (ObjectUtils.isEmpty(token)) {
            // 从请求参数中获取token信息
            token = request.getParameter("token");
        }
        // 从Spring Security中获取token信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取用户的身份信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 定义变量,保存新的token信息
        String newToken = null;
        // 验证提交过来的token信息是否是合法的
        if (jwtUtils.validateToken(token, userDetails)) {
            // 重新生成新的token信息
            newToken = jwtUtils.refreshToken(token);
        }
        // 获取本次token的过期时间
        long expireTime = Jwts.parser()
                .setSigningKey(jwtUtils.getSecret())
                .parseClaimsJws(newToken.replace("jwt_", ""))
                .getBody().getExpiration().getTime();
        // 清除原来的token信息
        String oidTokenKeys = "token_" + token;
        redisService.del(oidTokenKeys);
        // 将新的token信息存入redis中
        String newTokenKey = "token_" + newToken;
        redisService.set(newTokenKey, newToken, jwtUtils.getExpiration() / 1000);
        // 创建TokenVo对象,并返回给前端
        TokenVo tokenVo = new TokenVo(expireTime, newToken);
        // 返回数据
        return Result.ok(tokenVo).message("刷新token成功");
    }
    /**
     * 获取用户信息
     */
    @GetMapping("/getInfo")
    public Result getInfo(){
        // 从Spring Security中获取token信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 判断用户信息authentication是否为空
        if(authentication ==null){
            return Result.error().message("用户信息查询失败");
        }
        // 获取用户的身份信息
        User user= (User) authentication.getPrincipal();
        // 获取该用户拥有的角色权限信息
        List<Permission> permissionList = user.getPermissionList();
        // 获取权限编码
        Object[] roles = permissionList.stream().filter(Objects::nonNull).map(Permission::getCode).toArray();
        //创建用户信息
        UserInfo userInfo=new UserInfo(user.getId(), user.getNickName(),user.getAvatar(),null,roles);
        // 返回数据
    return Result.ok(userInfo).message("用户信息查询成功!");

        }
        /**
         * 获取登录用户菜单的数据
         */
        @GetMapping("/getMenuList")
        public Result getMenuList(){
            // 从Spring Security中获取token信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 获取用户的身份信息
            User user= (User) authentication.getPrincipal();
            // 获取该用户拥有的角色权限信息
            List<Permission> permissionList = user.getPermissionList();
            // 筛选当前用户用户的目录和菜单信息
            List<Permission> collect = permissionList.stream()
                    // 只筛选目录和菜单信息,不需要按钮添加到路由菜单汇总 1表示菜单,2表示按钮
                    .filter(item -> item != null && item.getType() != 2)
                    .collect(Collectors.toList());
            // 生成路由数据
            List<RouterVo> makeMenuTree = MenuTree.makeRouter(collect, 0L);
            // 返回数据
            return Result.ok(makeMenuTree).message("菜单信息查询成功!");


        }

        @PostMapping("/loginOut")
        public Result logout(HttpServletRequest request, HttpServletResponse response){
            // 获取token信息
            String token = request.getHeader("token");
            // 如果头部中没有携带token,则从参数中获取
            if(ObjectUtils.isEmpty(token)){
                // 从参数中获取token信息
                token=request.getParameter("token");
            }
            // 从spring Security上下文对象中获取用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 判断用户信息是否为空,如果不为空,则需要清空用户的信息
            if(authentication !=null){
                // 清空用户的信息
                new SecurityContextLogoutHandler().logout(request,response,authentication);
                // 清除Redis缓存中的token信息
                redisService.del("token_"+token);
                return  Result.ok().message("退出登录成功!");
            }
            return  Result.ok().message("退出登录失败!");
        }
    }
