package bing.ratelimit.filter;

import bing.ratelimit.RateLimiter;
import bing.ratelimit.entity.Policy;
import bing.ratelimit.entity.RateInfo;
import bing.ratelimit.entity.RateLimitProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static bing.ratelimit.entity.Policy.Type.*;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * 限流过滤器
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String ANONYMOUS = "anonymous";

    private final RateLimiter rateLimiter;
    private final RateLimitProperties properties;
    private final RouteLocator routeLocator;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return this.properties.isEnabled() && policy().isPresent();
    }

    @Override
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletResponse response = ctx.getResponse();
        final HttpServletRequest request = ctx.getRequest();

        policy().ifPresent(policy -> {
            final RateInfo rate = this.rateLimiter.consume(policy, key(request, policy.getTypes()));
            response.setHeader(Headers.LIMIT, rate.getLimit().toString());
            response.setHeader(Headers.REMAINING, String.valueOf(Math.max(rate.getRemaining(), 0)));
            response.setHeader(Headers.RESET, rate.getReset().toString());
            if (rate.getRemaining() < 0) {
                ctx.setResponseStatusCode(TOO_MANY_REQUESTS.value());
                ctx.put("rateLimitExceeded", "true");
                log.warn("单位时间内请求数量超过限制, 请求上限数: {}...", rate.getLimit());
                throw new ZuulRuntimeException(new ZuulException(TOO_MANY_REQUESTS.toString(),
                        TOO_MANY_REQUESTS.value(), null));
            }
        });
        log.info("已经过流量过滤...");
        return null;
    }

    /**
     * 获取请求路由信息
     *
     * @return
     */
    private Route route() {
        String requestURI = URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
        return this.routeLocator.getMatchingRoute(requestURI);
    }

    /**
     * 根据路由信息获取策略, 可能返回空
     *
     * @return
     */
    private Optional<Policy> policy() {
        return (route() != null) ? Optional.ofNullable(this.properties.getPolicies().get(route().getId())) : Optional.empty();
    }

    /**
     * 生成请求唯一标识
     *
     * @param request
     * @param types
     * @return
     */
    private String key(final HttpServletRequest request, final List<Policy.Type> types) {
        final Route route = route();
        final StringJoiner joiner = new StringJoiner(":");
        joiner.add(route.getId());
        if (!types.isEmpty()) {
            if (types.contains(URL)) {
                joiner.add(route.getPath());
            }
            if (types.contains(IP)) {
                joiner.add(getRequestIP(request));
            }
            if (types.contains(USER)) {
                joiner.add(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : ANONYMOUS);
            }
        }
        return joiner.toString();
    }

    /**
     * 解析远程主机IP地址
     *
     * @param request
     * @return
     */
    private String getRequestIP(final HttpServletRequest request) {
        if (request.getHeader(X_FORWARDED_FOR) != null) {
            return request.getHeader(X_FORWARDED_FOR);
        }
        return request.getRemoteAddr();
    }

    interface Headers {
        String LIMIT = "X-RateLimit-Limit";
        String REMAINING = "X-RateLimit-Remaining";
        String RESET = "X-RateLimit-Reset";
    }

}
