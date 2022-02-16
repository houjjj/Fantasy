package com.hou.stream.thread;

public class ThreadDemo1 {
    ThreadLocal<String> tl = new ThreadLocal<>();
    ThreadLocal<String> tl2 = new ThreadLocal<>();
    private String content;

    public String getContent() {
//        return content;
        return tl.get();
    }

    public void setContent(String content) {
//        this.content = content;
        tl.set(content);
        tl2.set(content);
    }

    public static void main(String[] args) {
        ThreadDemo1 demo1 = new ThreadDemo1();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
//                synchronized (ThreadDemo1.class) {
                    demo1.setContent(Thread.currentThread().getName() + "的数据");
                    System.out.println("------------------------------------");
                    System.out.println(Thread.currentThread().getName() + "---->" + demo1.getContent());
//                }
            });
//            thread.setName("线程" + i);
            thread.start();
        }
    }
}
