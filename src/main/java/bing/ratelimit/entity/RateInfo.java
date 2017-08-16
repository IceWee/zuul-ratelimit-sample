package bing.ratelimit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流量限制信息, 用户返回给API调用者
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateInfo {

    /**
     * 单位时间内请求数限制
     */
    private Long limit;

    /**
     * 单位时间内剩余请求次数
     */
    private Long remaining;

    /**
     * 重置限制周期剩余时间(单位: 秒)
     */
    private Long reset;

}
