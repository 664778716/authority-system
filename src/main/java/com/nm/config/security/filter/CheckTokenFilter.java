package com.nm.config.security.filter;

import com.nm.config.redis.RedisService;
import com.nm.config.security.exception.CustomerAuthenticationException;
import com.nm.config.security.handler.LoginFailureHandler;
import com.nm.config.security.service.CustomerUserDetailsService;
import com.nm.utils.JwtUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Data
@Component
public class CheckTokenFilter extends OncePerRequestFilter {
    // 登录请求地址
    @Value("${request.login.url}")
    private String loginUrl;
    @Resource
    private RedisService redisService;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private CustomerUserDetailsService customerUserDetailsService;
    @Resource
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取当前请求的url地址
            String url = request.getRequestURI();
            // 判断当前请求是否是登陆请求,如果不是登录请求,则进行token校验
            if(!url.equals(loginUrl)){
                // 进行token认证
                this.validateToken(request);
            }
        } catch (AuthenticationException e) {
            // 验证失败
            loginFailureHandler.onAuthenticationFailure(request, response, e);
        }
        // 登录请求不需要携带token,可以直接放行
        doFilter(request, response, filterChain);
    }

    /**
     * 进行token认证信息
     * @param request
     */
    private void validateToken(HttpServletRequest request) {
        // 获取前端提交过来的token信息
        // 从headers头部获取token信息
        String token = request.getHeader("token");
        // 如果请求头部中没有携带token信息,则从请求参数中获取token信息
        if(ObjectUtils.isEmpty(token)){
            token = request.getParameter("token");// 从参数中获取token信息
        }
        // 如果请求参数中也没有携带token信息,则抛出异常
        if(ObjectUtils.isEmpty(token)){
            throw new CustomerAuthenticationException("token不存在!");
        }
        // 判断redis中是否存在token信息
        String tokenKey="token_"+token;
        String redisToken = redisService.get(tokenKey);
        // 判断Redis中是否存在token信息,如果为空,则表示token信息已失效
        if(ObjectUtils.isEmpty(redisToken)){
            throw new CustomerAuthenticationException("token已过期");

        }
        // 如果token和Redis中的token信息不一致,则表示token信息已失效
        if(!token.equals(redisToken)){
            throw new CustomerAuthenticationException("token认证失败");
        }
        // 如果token存在,则从token中解析出用户名
        String username = jwtUtils.getUsernameFromToken(token);
        // 判断用户名是否为空
        if(ObjectUtils.isEmpty(username)){
            throw new CustomerAuthenticationException("token解析失败");
        }
        // 获取用户名对应的用户信息
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        // 判断用户信息是否为空
        if(userDetails==null){
            throw new CustomerAuthenticationException("用户不存在");
        }
        // 创建用户身份认证对象 UsernamePasswordAuthenticationToken(用户信息,密码,用户权限);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        // 设置请求信息
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 将验证信息交给SpringSecurity进行认证
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    }
