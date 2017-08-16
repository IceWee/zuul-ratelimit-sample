package bing.ratelimit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 限流策略实体
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    /**
     * 限制刷新周期(单位: 秒), 默认60秒. 含义: ?秒内只能发起?次请求, 过了这个周期重新计数
     */
    private Long refreshInterval = MINUTES.toSeconds(1L);

    /**
     * 请求次数限制, 即多长时间内允许?次请求
     */
    private Long limit;

    /**
     * 限制类型列表, 允许同时配置多种限制类型, 不过一般情况下只会配置一种
     */
    private List<Type> types = new ArrayList<>();

    /**
     * 限制类型, 可按照原始IP地址, 用户或者URL来限制
     */
    public enum Type {
        IP, URL, USER
    }

}
