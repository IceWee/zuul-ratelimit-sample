# Spring Cloud Zuul Rate-Limit Sample

> 本例子使用ZuulFilter和Redis实现分布式API请求限流

### 限流算法描述

> 为每个request生成一个唯一标识key，将key缓存到Redis中，并根据策略中的refreshInterval设置过期时间。
每次有相同key的请求则技术器自增，限流过滤器会读取当前key剩余请求次数，并检查是否超过了限制次数，如果超出就返回429，即too many requests.

### Redis key生成规则

> key的生成规则比较简单，格式如下：

```
routeId:type
```

> 举例：请求用户服务接口，限流策略type为IP，IP地址为：192.168.10.10，那么生成的可以为：

```
userService:192.168.10.10
```

### 限制类型(type)

1. ip
根据IP地址限制接口请求次数

2. url
根据URL限制接口请求次数

3. user
根据登录用户限制接口请求次数