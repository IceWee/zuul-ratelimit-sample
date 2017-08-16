package bing.ratelimit.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static bing.ratelimit.entity.RateLimitProperties.PREFIX;

/**
 * 流量限制配置详情, 通过自动加载application.xxx配置文件自动装配
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class RateLimitProperties {

    /**
     * 配置文件前缀
     */
    public static final String PREFIX = "zuul.ratelimit";

    /**
     * 限流策略配置集合
     */
    private Map<String, Policy> policies = new LinkedHashMap<>();

    /**
     * 是否启用开关
     */
    private boolean enabled;

}
