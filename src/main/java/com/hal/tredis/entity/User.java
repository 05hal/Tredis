package com.hal.tredis.entity;

import jakarta.persistence.*;  // 使用 JPA 注解
import lombok.Data;

@Data
@Entity  // JPA 注解
@Table(name = "user")  // JPA 注解
public class User {

    @Id  // JPA 注解
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // JPA 注解
    private Long id;

    @Column(name = "username", nullable = false, length = 100)  // JPA 注解
    private String username;  // 用户名

    @Column(name = "email", length = 100)  // JPA 注解
    private String email;     // 邮箱（结构化数据）
}