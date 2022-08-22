package com.houjun.quartz;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class TriggerDemo {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 任务类
        JobDetail jobDetail = new JobDetail("aaa", "A", JobDemo.class);
        // 触发器
        SimpleTrigger mytrigger = new SimpleTrigger("mytrigger", SimpleTrigger.REPEAT_INDEFINITELY, 3000);
        mytrigger.setStartTime(new Date());

        // 调度器工厂
        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = stdSchedulerFactory.getScheduler();
        scheduler.scheduleJob(jobDetail,mytrigger);
        scheduler.start();
        Thread.sleep(10_000);
        scheduler.shutdown();
    }
}
