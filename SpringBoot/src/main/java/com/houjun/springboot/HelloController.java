package com.houjun.springboot;

import org.springframework.beans.factory.annotation.Autowired;
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
}
