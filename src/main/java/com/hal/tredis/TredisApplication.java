package com.hal.tredis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class TredisApplication {

    public static void main(String[] args) {
        SpringApplication.run(TredisApplication.class, args);
    }

}
