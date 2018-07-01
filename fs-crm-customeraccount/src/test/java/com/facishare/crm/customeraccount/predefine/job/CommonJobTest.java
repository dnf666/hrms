package com.facishare.crm.customeraccount.predefine.job;

import com.facishare.crm.customeraccount.enums.SettleTypeEnum;
import com.facishare.crm.customeraccount.util.DateUtil;
import com.facishare.paas.appframework.core.model.RequestContext;
import com.facishare.paas.appframework.core.model.RequestContextManager;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Optional;

@Slf4j
public class CommonJobTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    protected Scheduler scheduler = null;

    ServiceContext serviceContext;

    {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //Scheduler scheduler = null;
        try {
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEnum() {
        log.info("settypeTypeEnums->values()", SettleTypeEnum.values());
        log.info("settypeTypeEnums" + SettleTypeEnum.values().getClass());
    }

    @Test
    public void getNowBenginTime() {
        System.out.println("now begin time=" + DateUtil.getNowBenginTime());
    }

    protected void startTask(Class<? extends Job> jobClass, String cronExpression) throws SchedulerException {
        JobKey jobKey = new JobKey(jobClass.getName(), "crm");
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        log.debug("begin executing job,for jobkey:{}", jobKey);

        Trigger cronTrigger = TriggerBuilder.newTrigger().forJob(jobDetail).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    protected void intServiceContext() {
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
}
