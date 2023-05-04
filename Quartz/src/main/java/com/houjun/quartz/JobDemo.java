package com.houjun.quartz;

import org.quartz.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JobDemo implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        String name = jobDetail.getKey().getName();
        String group = jobDetail.getKey().getGroup();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String data = jobDataMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining());
        System.out.println("这是任务类要做的事情！！！");
        System.out.printf("name %s,group %s,jobDataMap %s %n",name,group, data);
    }
}
