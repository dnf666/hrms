package com.facishare.crm.stock.manager;

import com.facishare.crm.stock.predefine.manager.StockWarningJobManager;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author linchf
 * @date 2018/3/26
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class StockWarningJobManagerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Resource
    private StockWarningJobManager stockWarningJobManager;

    @Test
    public void testAddJob() {
        int jobId = stockWarningJobManager.addJob("55985");
        System.out.println(jobId);
    }

    @Test
    public void testDeleteJob() {
        stockWarningJobManager.deleteJob(600);
    }

    @Test
    public void testSetRecordRemind() {
        stockWarningJobManager.setRecordRemind(new User("55983", "1000"), 10, Arrays.asList("1000"));
    }
}
