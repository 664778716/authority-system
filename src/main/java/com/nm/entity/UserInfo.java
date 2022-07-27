package com.nm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 封装前端显示的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    // 用户id
    private Long id;
    // 用户名
    private String name;
    // 用户头像
    private String avatar;
    // 介绍
    private String introduction;
    // 角色权限集合
    private Object[] roles;

}
