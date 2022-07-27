package com.nm.config.security;

import com.nm.config.security.filter.CheckTokenFilter;
import com.nm.config.security.handler.AnonymousAuthenticationHandler;
import com.nm.config.security.handler.CustomerAccessDeniedHandler;
import com.nm.config.security.handler.LoginFailureHandler;
import com.nm.config.security.handler.LoginSuccessHandler;
import com.nm.config.security.service.CustomerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    @Resource
    private LoginFailureHandler loginFailureHandler;
    @Resource
    private AnonymousAuthenticationHandler anonymousAuthenticationHandler;
    @Resource
    private CustomerAccessDeniedHandler customerAccessDeniedHandler;
    @Resource
    private CustomerUserDetailsService customerUserDetailsService;
    @Resource
    private CheckTokenFilter checkTokenFilter;

    /**
     * 注入加密类
     * @return
     */

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    /**
     * 处理登录认证
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登录器进行过滤
        http.addFilterBefore(checkTokenFilter, UsernamePasswordAuthenticationFilter.class);
       // 登录过程处理
        http.formLogin()            // 表单登录
                .loginProcessingUrl("/api/user/login")   // 登录处理的url,地址自定义
                .successHandler(loginSuccessHandler)   // 登录成功处理器
                .failureHandler(loginFailureHandler)   // 登录失败处理器
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 不创建session
                .and()
                .authorizeRequests()  // 设置需要拦截的请求
                .antMatchers("/api/user/login").permitAll() // 登录请求放行(不拦截)
                .anyRequest().authenticated() // 其他请求都需要认证拦截
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(anonymousAuthenticationHandler)  //匿名用户无权限访问
                .accessDeniedHandler(customerAccessDeniedHandler)    //认证用户无权限访问
                .and()
                .cors();  //支持跨域请求
    }

    /**
     * 配置认证管理器
     * @param auth
     * @throws Exception
     */
    protected void configure(AuthenticationManagerBuilder auth)throws Exception {
       auth.userDetailsService(customerUserDetailsService).passwordEncoder(passwordEncoder());
    }
}
