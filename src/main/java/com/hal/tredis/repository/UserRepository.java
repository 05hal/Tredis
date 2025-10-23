package com.hal.tredis.repository;

import com.hal.tredis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据用户名查询
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户id查询
     */

    Optional<User> findById(Long id);
    /**
     * 根据邮箱查询
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名或邮箱查询
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    // ==================== 存在性判断 ====================

    /**
     * 判断用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在
     */
    boolean existsByEmail(String email);

    // ==================== 模糊查询 ====================

    /**
     * 根据用户名模糊查询
     * 使用示例: findByUsernameContaining("张") -> 查询用户名包含"张"的用户
     */
    List<User> findByUsernameContaining(String username);

    /**
     * 根据用户名模糊查询（忽略大小写）
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * 根据邮箱模糊查询
     */
    List<User> findByEmailContaining(String email);

    // ==================== 排序查询 ====================

    /**
     * 查询所有用户，按用户名排序
     */
    List<User> findAllByOrderByUsernameAsc();

    /**
     * 查询所有用户，按 ID 降序
     */
    List<User> findAllByOrderByIdDesc();

    // ==================== 自定义 JPQL 查询 ====================


    // ==================== 原生 SQL 查询 ====================

    /**
     * 使用原生 SQL 根据邮箱查询
     */
    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailNative(@Param("email") String email);

    /**
     * 使用原生 SQL 模糊查询
     */
    @Query(value = "SELECT * FROM user WHERE username LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
    List<User> searchByUsernameNative(@Param("keyword") String keyword);

    // ==================== 更新操作 ====================

}