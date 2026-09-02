package com.bookstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在线图书销售平台后端启动类
 */
@SpringBootApplication
@MapperScan("com.bookstore.mapper")
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "  BookStore 后端启动成功!\n" +
                "  接口地址: http://localhost:8080/api\n" +
                "  接口文档: http://localhost:8080/api/doc.html\n" +
                "========================================\n");
    }
}
