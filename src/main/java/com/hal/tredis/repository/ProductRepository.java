package com.hal.tredis.repository;

import com.hal.tredis.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据商品名称查询
     */
    Optional<Product> findByName(String name);

    /**
     * 根据分类查询
     */
    List<Product> findByCategory(String category);

    /**
     * 根据分类查询（分页）
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    // ==================== 价格相关查询 ====================

    /**
     * 根据价格范围查询
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    /**
     * 根据价格范围查询（分页）
     */
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * 查询价格大于指定值的商品
     */
    List<Product> findByPriceGreaterThan(Double price);

    /**
     * 查询价格小于指定值的商品
     */
    List<Product> findByPriceLessThan(Double price);

    /**
     * 查询价格等于指定值的商品
     */
    List<Product> findByPrice(Double price);

    // ==================== 分类相关查询 ====================

    /**
     * 根据分类查询，按价格升序排序
     */
    List<Product> findByCategoryOrderByPriceAsc(String category);

    /**
     * 根据分类查询，按价格降序排序
     */
    List<Product> findByCategoryOrderByPriceDesc(String category);

    /**
     * 统计指定分类的商品数量
     */
    long countByCategory(String category);

    // ==================== 模糊查询 ====================

    /**
     * 根据商品名称模糊查询
     */
    List<Product> findByNameContaining(String name);

    /**
     * 根据商品名称模糊查询（忽略大小写）
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * 根据分类模糊查询
     */
    List<Product> findByCategoryContaining(String category);

    /**
     * 根据名称或分类模糊查询
     */
    List<Product> findByNameContainingOrCategoryContaining(String name, String category);

    // ==================== 复合条件查询 ====================

    /**
     * 根据分类和价格范围查询
     */
    List<Product> findByCategoryAndPriceBetween(String category, Double minPrice, Double maxPrice);

    /**
     * 根据分类和价格大于指定值查询
     */
    List<Product> findByCategoryAndPriceGreaterThan(String category, Double price);

    // ==================== 排序查询 ====================

    /**
     * 查询所有商品，按价格升序
     */
    List<Product> findAllByOrderByPriceAsc();

    /**
     * 查询所有商品，按价格降序
     */
    List<Product> findAllByOrderByPriceDesc();

    /**
     * 查询所有商品，按名称排序
     */
    List<Product> findAllByOrderByNameAsc();

    // ==================== 自定义 JPQL 查询 ====================


}