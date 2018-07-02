package com.facishare.crm.stock.manager;

import com.facishare.crm.stock.predefine.manager.ProductManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author liangk
 * @date 24/01/2018
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class CommonManagerTest {

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Autowired
    private ProductManager productManager;

    @Test
    public void testRemoveList() {
        List<String> demoList = new ArrayList<>();
        demoList.add("123");
        demoList.add("abc");

        log.info("demoList-before:{}", demoList);
        demoRemoveList(demoList);

        log.info("demoList-after:{}", demoList);
    }

    private void demoRemoveList(List<String> lists) {
        Iterator<String> listIter = lists.iterator();
        while (listIter.hasNext()) {
            if (listIter.next().equals("123")) {
                listIter.remove();
            }
        }

    }
}
