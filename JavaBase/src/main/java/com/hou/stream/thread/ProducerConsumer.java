package com.hou.stream.thread;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 生产者消费者模型
 */
public class ProducerConsumer {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
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
        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                queue.add(UUID.randomUUID().toString());
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private ArrayBlockingQueue<String> queue;

        public Consumer(ArrayBlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    System.out.println(queue.poll());
                }
            }
        }
    }
}
