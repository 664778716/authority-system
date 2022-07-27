package com.nm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@SpringBootTest
public class TestUser {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void test1(){
        System.out.println(passwordEncoder.encode("123456"));
    }
}
