package com.hal.tredis.entity;

import jakarta.persistence.*;  // 使用 JPA 注解
import lombok.Data;

@Data
@Entity  // JPA 注解
@Table(name = "product")  // JPA 注解
public class Product {

    @Id  // JPA 注解
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // JPA 注解
    private Long id;

    @Column(name = "name", nullable = false, length = 255)  // JPA 注解
    private String name;      // 商品名

    @Column(name = "price", nullable = false)  // JPA 注解
    private Double price;     // 价格（结构化、需精确存储）

    @Column(name = "category", length = 100)  // JPA 注解
    private String category;  // 分类
}