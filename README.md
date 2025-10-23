Redis学习


一、实验概述
1.1 实验目的
- 掌握 Spring Boot 集成 Redis 的方法
- 理解 Redis ZSet 数据结构在用户浏览记录场景的应用
- 实践 Redis 与 MySQL 的配合使用
1.2 实验环境
- Spring Boot 3.5.6 + Spring Data Redis
- Redis 6.x (端口 6379)
- MySQL 8.0
二、Redis 配置
2.1 Maven 依赖
xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
2.2 配置文件
properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0

---
三、Redis 核心用法
3.1 注入 RedisTemplate
java
@Service
@RequiredArgsConstructor
public class ViewRecordService {
    private final RedisTemplate<String, String> redisTemplate;
}
3.2 ZSet 操作 - 记录浏览
java
// 添加浏览记录到 ZSet（时间戳作为分数）
String viewKey = "user:view:" + userId;
double timestamp = System.currentTimeMillis();
redisTemplate.opsForZSet().add(viewKey, productId.toString(), timestamp);
3.3 ZSet 操作 - 查询浏览记录
java
// 按时间倒序获取最近浏览的 N 个商品ID
Set<String> productIdSet = redisTemplate.opsForZSet()
    .reverseRange(viewKey, 0, limit - 1);
3.4 ZSet 操作 - 限制数据量
java
// 只保留最近 100 条记录
Long size = redisTemplate.opsForZSet().zCard(viewKey);
if (size != null && size > 100) {
    redisTemplate.opsForZSet().removeRange(viewKey, 0, size - 101);
}
3.5 String 操作 - 计数器
java
// 增加商品浏览次数计数
String countKey = "product:view:count:" + productId;
redisTemplate.opsForValue().increment(countKey);

// 获取浏览次数
String count = redisTemplate.opsForValue().get(countKey);
3.6 删除操作
java
// 删除用户浏览历史
redisTemplate.delete(viewKey);
四、RedisTemplate 方法总结
4.1 ZSet（有序集合）操作
暂时无法在飞书文档外展示此内容
4.2 String 操作
暂时无法在飞书文档外展示此内容
4.3 通用操作
暂时无法在飞书文档外展示此内容
五、Redis CLI 命令
5.1 连接 Redis
bash
# 连接本地 Redis
redis-cli

# 连接指定主机端口
redis-cli -h localhost -p 6379
5.2 ZSet 查询命令
bash
# 查看所有用户浏览记录的 key
KEYS user:view:*

# 查看用户1的浏览记录（倒序，带分数）
ZREVRANGE user:view:1 0 -1 WITHSCORES

# 查看用户1最近浏览的5个商品
ZREVRANGE user:view:1 0 4

# 查看用户1的浏览记录数量
ZCARD user:view:1

# 查看商品101在用户1浏览记录中的时间戳
ZSCORE user:view:1 101
5.3 String 查询命令
bash
# 查看商品101的浏览次数
GET product:view:count:101

# 查看所有商品浏览计数的 key
KEYS product:view:count:*
5.4 通用命令
bash
# 查看所有 key
KEYS *

# 查看 key 的类型
TYPE user:view:1

# 查看当前数据库的 key 数量
DBSIZE

# 删除指定 key
DEL user:view:1

# 清空当前数据库
FLUSHDB
```

---

## 六、数据存储设计

### 6.1 用户浏览记录（ZSet）

**Key 格式**: `user:view:{userId}`

**数据结构**:
```
member (商品ID)    score (时间戳)
"101"          →   1729660800000
"102"          →   1729660805000
"103"          →   1729660810000
```

**特点**:
- 自动按时间戳排序
- 支持快速获取最新/最旧的浏览记录
- 可限制数据量，只保留最近N条

### 6.2 商品浏览计数（String）

**Key 格式**: `product:view:count:{productId}`

**数据结构**:
```
key                         value
"product:view:count:101" → "25"
"product:view:count:102" → "18"
```

**特点**:
- 支持原子性自增操作
- 高效的计数统计

---

## 七、Redis 与 MySQL 配合

### 7.1 数据流程
```
写操作：
Controller → Service → Redis (ZSet 记录浏览)
                    → Redis (String 计数+1)

读操作：
Controller → Service → Redis (获取商品ID列表)
                    → MySQL (批量查询商品详情)
                    → 返回排序后的商品列表
7.2 核心代码片段
java
// 1. 写：记录浏览到 Redis
redisTemplate.opsForZSet().add("user:view:" + userId, 
    productId.toString(), System.currentTimeMillis());

// 2. 读：从 Redis 获取ID，从 MySQL 获取详情
Set<String> productIds = redisTemplate.opsForZSet()
    .reverseRange("user:view:" + userId, 0, 9);
List<Product> products = productRepository.findByIdIn(productIds);

---
八、实验测试
8.1 API 测试命令
bash
# 记录浏览
curl -X POST http://localhost:8080/views/1/101
curl -X POST http://localhost:8080/views/1/102
curl -X POST http://localhost:8080/views/1/103

# 查询用户浏览记录
curl http://localhost:8080/views/user/1

# 查询商品浏览次数
curl http://localhost:8080/views/product/101/count
8.2 验证 Redis 数据
bash
# 进入 Redis CLI
redis-cli

# 查看用户1的浏览记录
ZREVRANGE user:view:1 0 -1 WITHSCORES

# 查看商品101的浏览次数
GET product:view:count:101

---
九、实验总结
9.1 Redis 数据结构选型
暂时无法在飞书文档外展示此内容
9.2 关键技术点
1. ZSet 的 score 使用时间戳：实现按时间排序
2. reverseRange：获取最新数据（倒序）
3. 限制数据量：防止内存无限增长
4. Redis + MySQL 配合：热数据在 Redis，详情在 MySQL
5. 批量查询优化：findByIdIn 减少数据库查询次数
9.3 性能优势
- Redis 查询浏览记录：< 1ms
- MySQL 批量查询详情：< 10ms
- 总响应时间：< 20ms
相比纯 MySQL 查询（> 100ms），性能提升 5-10 倍。# Tredis
a start-up user-product view application for redis
