package com.hal.tredis.controller;
import com.hal.tredis.entity.Product;
import com.hal.tredis.service.ViewRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/views")
@RequiredArgsConstructor
public class ViewRecordController {
    private final
    ViewRecordService viewRecordService;

    // 模拟用户浏览商品
    @PostMapping("/{userId}/{productId}")
    public String view(@PathVariable Long userId, @PathVariable Long productId) {
        viewRecordService.viewProduct(userId, productId);
        return "浏览成功";
    }

    // 查询用户最近浏览
    @
            GetMapping("/user/{userId}")
    public List<Product> getUserViews(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "10") int limit) {
        return viewRecordService.getUserRecentViews(userId, limit);
    }

    // 查询商品浏览次数
    @GetMapping("/product/{productId}/count")
    public Long getProductCount(@PathVariable Long productId) {
        return viewRecordService.getProductViewCount(productId);
    }
}