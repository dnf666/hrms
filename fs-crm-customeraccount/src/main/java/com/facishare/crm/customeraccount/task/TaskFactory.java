package com.facishare.crm.customeraccount.task;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.facishare.crm.customeraccount.predefine.job.EffectiveRebateIncomeJob;
import com.facishare.crm.customeraccount.predefine.job.EnableCustomerAccountJob;
import com.facishare.crm.customeraccount.predefine.job.InavlidRebateIncomeJob;
import com.facishare.crm.customeraccount.util.ConfigCenter;
import com.facishare.crm.customeraccount.util.IpUtil;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskFactory {
    private static Scheduler scheduler;

    public static void init() {
        try {
            if (isOpen()) {
                SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                scheduler = schedulerFactory.getScheduler();
                startTasks();
                scheduler.start();
                log.info("start run tasks.");
            } else {
                log.info("ignore run tasks.");
            }
        } catch (Exception e) {
            log.warn("init job error", e);
        }
    }

    private static boolean isOpen() {
        String ip = IpUtil.getLocalHostIP();
        String host = IpUtil.getLocalHostName();
        log.info("ip={},host={}", ip, host);
        String machine = ConfigCenter.taskMachine;
        if (StringUtil.isNullOrEmpty(machine)) {
            log.info("taskMachine is empty");
            return false;
        }
        if (!StringUtil.isNullOrEmpty(ip) && !ip.endsWith("127.0.0.1") && machine.equals(ip)) {
            return true;
        }
        if (!StringUtil.isNullOrEmpty(host) && !host.endsWith("localhost") && machine.equals(host)) {
            return true;
        }
        return false;
    }

    private static void startTasks() {
        try {
            startTask(EnableCustomerAccountJob.class, "30 1 0 * * ?");
            startTask(EffectiveRebateIncomeJob.class, "30 0 0 * * ?");
            startTask(InavlidRebateIncomeJob.class, "30 0 0 * * ?");
        } catch (Exception e) {
            log.warn("start job error", e);
        }
    }

    private static void startTask(Class<? extends Job> jobClass, String cronExpression) throws SchedulerException {
        JobKey jobKey = new JobKey(jobClass.getName(), "crm");
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        Trigger cronTrigger = TriggerBuilder.newTrigger().forJob(jobDetail).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

}
