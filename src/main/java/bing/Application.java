package bing;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableZuulProxy
@SpringCloudApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 示例Controller
     *
     * @author: IceWee
     * @date: 2017/8/16
     */
    @RestController
    @RequestMapping(path = "/services", produces = MediaType.TEXT_PLAIN_VALUE)
    public class SampleController {

        @GetMapping("/serviceA")
        public String serviceA() {
            return "serviceA";
        }

        @GetMapping("/serviceB")
        public String serviceB() {
            return "serviceB";
        }

        @GetMapping("/serviceC/{paramName}")
        public String serviceD(@PathVariable String paramName) {
            return "seriviceC " + paramName;
        }

    }

}
