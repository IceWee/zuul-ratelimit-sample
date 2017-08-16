package bing.ratelimit;

import bing.ratelimit.entity.Policy;
import bing.ratelimit.entity.RateInfo;

/**
 * 限流计算器器接口
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
public interface RateLimiter {

    /**
     * 限流计算
     *
     * @param policy 限流策略
     * @param key 根据请求以及其他信息生成的唯一标识
     * @return 当前限流信息
     */
    RateInfo consume(Policy policy, String key);

}
