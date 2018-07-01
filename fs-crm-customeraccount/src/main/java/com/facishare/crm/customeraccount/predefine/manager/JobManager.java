package com.facishare.crm.customeraccount.predefine.manager;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class JobManager {

    public static Trigger fireAfterEvery5SecondsRepeatThrice() {
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "enbleCustomerAccountTrigger").startNow().withSchedule(simpleSchedule().withIntervalInSeconds(5).withRepeatCount(2)).build();
        return trigger;
    }
}
