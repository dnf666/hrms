package com.facishare.crm.customeraccount.predefine.job;

import java.util.Optional;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.customeraccount.constants.JobConstants;
import com.facishare.crm.customeraccount.predefine.manager.JobManager;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class EnableCustomerAccountJobTest {

    ServiceContext serviceContext;

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Before
    public void initServiceContext() {
        String tenantId = "55732";
        String fsUserId = "1000";
        RequestContext.RequestContextBuilder requestContextBuilder = RequestContext.builder();
        requestContextBuilder.tenantId(tenantId);
        Optional<User> user = Optional.of(new User(tenantId, fsUserId));

        requestContextBuilder.user(user);
        requestContextBuilder.contentType(RequestContext.ContentType.FULL_JSON);
        requestContextBuilder.postId("123");
        RequestContext requestContext = requestContextBuilder.build();
        RequestContextManager.setContext(requestContext);
        RequestContextManager.setContext(RequestContext.builder().postId("111").tenantId("55732").build());

        serviceContext = new ServiceContext(requestContext, null, null);
    }

    @Test
    public void testCreateJob() {

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = null;

        try {
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        String customerId = "eebe39d4fca743ed80802825279353f8";
        JobKey jobKey = new JobKey(customerId, "enableCustomerAccount");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobConstants.CUSTOMER_ID_KEY, customerId);
        jobDataMap.put(JobConstants.SERVICE_CONTEXT_KEY, serviceContext);
        JobDetail jobDetail = JobBuilder.newJob(EnableCustomerAccountJob.class).usingJobData(jobDataMap).withIdentity(jobKey).build();

        try {
            // scheduler.getListenerManager().addJobListener(new EnableCustomerAccountJobListener(), KeyMatcher.keyEquals(jobKey));

            scheduler.scheduleJob(jobDetail, JobManager.fireAfterEvery5SecondsRepeatThrice());
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //        JobDetail enableCustomerAccountJob = n
    }

    @Test
    public void testCreateJob1() {

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        String customerId = "eebe39d4fca743ed80802825279353f8";

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobConstants.CUSTOMER_ID_KEY, customerId);
        jobDataMap.put(JobConstants.SERVICE_CONTEXT_KEY, serviceContext);
        JobDetail jobDetail = JobBuilder.newJob(EnableCustomerAccountJob.class).usingJobData(jobDataMap).withIdentity(customerId, "enableCustmerAccount").build();

        try {
            scheduler.scheduleJob(jobDetail, JobManager.fireAfterEvery5SecondsRepeatThrice());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        //        JobDetail enableCustomerAccountJob = n
    }

    @Test
    public void testCronScheduler() {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

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
