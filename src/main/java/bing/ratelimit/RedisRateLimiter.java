package bing.ratelimit;

import bing.ratelimit.entity.Policy;
import bing.ratelimit.entity.RateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 限流计算器器Redis实现类, 可以处理分布式限流
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
public class RedisRateLimiter implements RateLimiter {

    @Autowired
    private StringRedisTemplate redis;

    @Override
    public RateInfo consume(Policy policy, String key) {
        final Long limit = policy.getLimit();
        final Long refreshInterval = policy.getRefreshInterval();
        final Long current = this.redis.boundValueOps(key).increment(1L); // 当前请求数+1并返回
        Long expire = this.redis.getExpire(key);
        if (expire == null || expire == -1) { // 如果当前请求已到刷新周期, redis会自动移除技术器, 重新为当前请求初始化限流信息
            this.redis.expire(key, refreshInterval, SECONDS);
            expire = refreshInterval;
        }
        return new RateInfo(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire));
    }

}
