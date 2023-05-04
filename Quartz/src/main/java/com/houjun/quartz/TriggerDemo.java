package com.houjun.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * quartz 三个核心对象
 * 1. 调度器
 * 2. 触发器
 * 3. 任务
 */
public class TriggerDemo {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 1. 创建调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        // 2. 创建 trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .withIdentity("trigger1", "group1")
//                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(2).repeatForever())
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
        // 3. 创建 job
        JobDetail jobDetail = JobBuilder.newJob()
                .ofType(JobDemo.class)
                .withIdentity("job1", "group1")
                .usingJobData("data1","value1")
                .build();
        // 4. 调度器装配触发器和任务
        scheduler.scheduleJob(jobDetail,trigger);
        scheduler.start();
    }

}
