package bing;

import bing.ratelimit.RateLimiter;
import bing.ratelimit.RedisRateLimiter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * 单元测试类
 *
 * @author: IceWee
 * @date: 2017/8/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    private static final String LIMIT = "X-RateLimit-Limit";
    private static final String REMAINING = "X-RateLimit-Remaining";
    private static final String RESET = "X-RateLimit-Reset";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationContext context;

    @Test
    public void testRedisRateLimiter() {
        RateLimiter rateLimiter = context.getBean(RateLimiter.class);
        assertTrue("RedisRateLimiter", rateLimiter instanceof RedisRateLimiter);
    }

    @Test
    public void testNotExceedingCapacityRequest() {
        ResponseEntity<String> response = this.restTemplate.exchange("/serviceA", GET, null, String.class);
        HttpHeaders headers = response.getHeaders();
        assertHeaders(headers, false);
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void testMultipleUrls() {
        String randomPath = UUID.randomUUID().toString();

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                randomPath = UUID.randomUUID().toString();
            }

            ResponseEntity<String> response = this.restTemplate.exchange("/serviceC/" + randomPath, GET, null, String.class);
            HttpHeaders headers = response.getHeaders();
            assertHeaders(headers, false);
            assertEquals(OK, response.getStatusCode());
        }
    }

    private void assertHeaders(HttpHeaders headers, boolean nullable) {
        String limit = headers.getFirst(LIMIT);
        String remaining = headers.getFirst(REMAINING);
        String reset = headers.getFirst(RESET);

        if (nullable) {
            assertNull(limit);
            assertNull(remaining);
            assertNull(reset);
        } else {
            assertNotNull(limit);
            assertNotNull(remaining);
            assertNotNull(reset);
        }
    }

}
