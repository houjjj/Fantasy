package com.hou.stream.thread;

import lombok.SneakyThrows;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 生产者消费者模型
 * 要求，生产一个消费一个
 */
public class ProducerConsumer {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        Thread producer = new Thread(new Producer(queue));
        Thread consumer = new Thread(new Consumer(queue));
        producer.start();
        consumer.start();
    }

    static class Producer implements Runnable {
        private ArrayBlockingQueue<String> queue;

        public Producer(ArrayBlockingQueue<String> queue) {
            this.queue = queue;
        }

        @SneakyThrows
        @Override
        public void run() {
            for (int i = 0; ; i++) {
                synchronized (queue) {
                    // 唤醒在当前 锁对象上等待的线程
                    queue.notify();
                    String uuid = UUID.randomUUID().toString();
                    queue.add(uuid);
                    System.out.println("生产：" + uuid);
                    // 当前线程休眠并放弃锁
                    queue.wait();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private ArrayBlockingQueue<String> queue;

        public Consumer(ArrayBlockingQueue<String> queue) {
            this.queue = queue;
        }

        @SneakyThrows
        @Override
        public void run() {
            for (int i = 0; ; i++) {
                synchronized (queue) {
                    queue.notify();
                    System.out.println("消费：" + queue.poll());
                    queue.wait();
                }
            }
        }
    }
}
