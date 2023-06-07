package com.houjun.springboot;

import com.houjun.springboot.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/hello")
public class HelloController {

//    @Autowired
//    private ThreadPoolExecutor poolExecutor;
//
//    @RequestMapping
//    public void hello(){
////        NamedThreadFactory namedThreadFactory = new NamedThreadFactory();
//        System.out.println(poolExecutor.getPoolSize());
//    }

    @Autowired
    HelloService helloService;

    @GetMapping("update")
    public void update() {
        helloService.update();
    }

    @GetMapping("insert")
    public void insert() {
        helloService.insert();
    }
}
