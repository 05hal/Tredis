package com.hal.tredis.service;

import com.hal.tredis.entity.Product;
import com.hal.tredis.entity.User;

import com.hal.tredis.repository.ProductRepository;
import com.hal.tredis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewRecordService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;

    // 1. 用户浏览商品（同时写 MySQL 和 Redis）
    public void viewProduct(Long userId, Long productId) {
        // 验证用户和商品是否存在（查 MySQL，核心数据持久化存储）
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        if (user == null || product == null) {
            throw new RuntimeException("用户或商品不存在");
        }

        // 记录浏览记录（存 Redis，高频读写、临时数据）
        String viewKey = "user:view:" + userId;
        long timestamp = System.currentTimeMillis();
        // ZSet 存储：商品ID -> 时间戳（支持按时间排序）
        redisTemplate.opsForZSet().add(viewKey, productId.toString(), timestamp);

        // 累加商品浏览次数（存 Redis，计数场景性能优势）
        String countKey = "product:view:count:" + productId;
        redisTemplate.opsForValue().increment(countKey);
    }

    // 2. 查询用户最近浏览的10个商品（读 Redis）
    public List<Product> getUserRecentViews(Long userId, int limit) {
        String viewKey = "user:view:" + userId;
        // ZSet 按分数（时间戳）倒序取前 limit 个（最新浏览）
        Set<String> productIdSet = redisTemplate.opsForZSet()
                .reverseRange(viewKey, 0, limit - 1);
        if (productIdSet == null || productIdSet.isEmpty()) {
            return Collections.emptyList();
        }

        // 从 MySQL 查商品详情（核心数据从数据库取）
        List<Long> productIds = productIdSet.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return productRepository.findAllById(productIds);
    }

    // 3. 查询商品的浏览次数（读 Redis）
    public Long getProductViewCount(Long productId) {
        String countKey = "product:view:count:" + productId;
        String count = redisTemplate.opsForValue().get(countKey);
        return count == null ? 0 : Long.valueOf(count);
    }
}