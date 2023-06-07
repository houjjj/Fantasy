package com.houjun.kafka.controller.mqadmin;

import lombok.extern.slf4j.Slf4j;


import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Closer {
    
    private static final ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((thread, e) -> {
            log.error("", e);
        });
        t.setDaemon(true);
        t.setName("专门用来关闭 rocketmq kafka 连接的");
        return t;
    });
    
    public static void doFinal(Runnable r) {
        pool.execute(r);
    }
    
//    public static void main(String[] args) throws Exception {
//        DefaultMQProducer producer = new
//            DefaultMQProducer("producer");
//        // Specify name server addresses.
//        producer.setNamesrvAddr("192.168.1.70:30774");
//        //Launch the instance.
//        producer.start();
//        for (int i = 0; i < 100; i++) {
//            //Create a message instance, specifying topic, tag and message body.
//            Message msg = new Message("test2333" /* Topic */,
//                "TagA" /* Tag */,
//                ("Hello RocketMQ " +
//                    i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//            );
//            //Call send message to deliver message to one of brokers.
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//            Thread.sleep(10000);
//        }
//        //Shut down once the producer instance is not longer in use.
//        producer.shutdown();
//
//    }
}
