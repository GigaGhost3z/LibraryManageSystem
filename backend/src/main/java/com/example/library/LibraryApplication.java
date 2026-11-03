package com.example.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 图书管理系统 启动类
 */
@SpringBootApplication
@MapperScan("com.example.library.mapper")
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
        System.out.println("""
                
                ==========================================================
                  图书管理系统后端启动成功
                  接口地址: http://localhost:8080/api/book/page
                ==========================================================
                """);
    }
}
