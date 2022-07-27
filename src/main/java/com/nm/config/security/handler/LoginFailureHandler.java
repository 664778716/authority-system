package com.nm.config.security.handler;

import com.alibaba.fastjson.JSON;
import com.nm.config.security.exception.CustomerAuthenticationException;
import com.nm.utils.Result;
import com.nm.utils.ResultCode;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *  登录认证失败处理器类
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 设置响应的编码格式
        response.setContentType("application/json;charset=utf-8");
        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        // 定义变量用于存储异常信息
        String message = null;
        // 定义状态码
        int code = 500;
        // 判断异常类型
        if(exception instanceof AccountExpiredException){
            message= "账号已过期,登录失败!";
        }else if(exception instanceof BadCredentialsException){
            message= "用户名或密码错误,登录失败!";
        }else if(exception instanceof CredentialsExpiredException){
            message= "密码已过期,登录失败!";
        }else if(exception instanceof DisabledException){
            message= "账号已被禁用,登录失败!";
        }else if(exception instanceof LockedException){
            message= "账号已被锁定,登录失败!";
        }else if(exception instanceof  InternalAuthenticationServiceException){
            message= "账户不存在,登录失败!";
        }else if(exception instanceof CustomerAuthenticationException){
            message= exception.getMessage();
            code= 600;
        }else{
            message= "登录失败!";
        }
        // 将结果转换成JSON格式字符串
        String result = JSON.toJSONString(Result.error().code(code).message(message));
        // 将结果保存到输出流中
        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
