package com.facishare.crm.customeraccount.predefine.job;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.constants.JobConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class InvalidRebateIncomeJobTest extends CommonJobTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Before
    public void initServiceContext() {
        super.intServiceContext();
    }

    @Test
    public void testInvalidRebateIncomeJob() {

        String customerId = "eebe39d4fca743ed80802825279353f8";

        String cronExpression = "5 0 0 * * ?";

        try {
            startTask(InavlidRebateIncomeJob.class, cronExpression);
        } catch (Exception e) {
            log.error("error occur when startTask");
        }

        try {
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCronScheduler() {

        String customerId = "eebe39d4fca743ed80802825279353f8";

        String cronExpression = "30 0 0 * * ?";

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobConstants.CUSTOMER_ID_KEY, customerId);
        jobDataMap.put(JobConstants.SERVICE_CONTEXT_KEY, serviceContext);
        JobDetail jobDetail = JobBuilder.newJob(EnableCustomerAccountJob.class).usingJobData(jobDataMap).withIdentity(customerId, "enableCustmerAccount").build();

        Trigger cronTrigger = TriggerBuilder.newTrigger().forJob(jobDetail).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        try {
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
