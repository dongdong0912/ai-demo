package com.example.usermanagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class UserManagementApplication {

    public static void main(String[] args) {
        log.info("用户管理系统启动中...");
        SpringApplication.run(UserManagementApplication.class, args);
        log.info("用户管理系统启动成功!");
    }
}
