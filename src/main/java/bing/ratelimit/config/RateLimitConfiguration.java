package bing.ratelimit.config;

import bing.ratelimit.RateLimiter;
import bing.ratelimit.RedisRateLimiter;
import bing.ratelimit.entity.RateLimitProperties;
import bing.ratelimit.filter.RateLimitFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static bing.ratelimit.entity.RateLimitProperties.PREFIX;

/**
 * 流量限制配置, 只有打开enabled开关才加载配置
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", havingValue = "true")
public class RateLimitConfiguration {

    /**
     * 注册限流过滤器
     *
     * @param rateLimiter         限流计算器, Spring自动注入
     * @param rateLimitProperties 限流配置, Spring自动注入
     * @param routeLocator
     * @return
     */
    @Bean
    public RateLimitFilter rateLimiterFilter(RateLimiter rateLimiter, RateLimitProperties rateLimitProperties, RouteLocator routeLocator) {
        return new RateLimitFilter(rateLimiter, rateLimitProperties, routeLocator);
    }

    /**
     * 注册Redis限流器
     *
     * @return
     */
    @Bean
    public RateLimiter redisRateLimiter() {
        return new RedisRateLimiter();
    }

}
