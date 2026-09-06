package houjun.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/redis")
public class HelloController {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @GetMapping("set")
    public void set() {
        redisTemplate.boundValueOps("hello").set("world");
    }

    @GetMapping("get")
    public void get() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("时间：" + now + "; key: hello ,value: " + redisTemplate.boundValueOps("hello").get());
    }


}
