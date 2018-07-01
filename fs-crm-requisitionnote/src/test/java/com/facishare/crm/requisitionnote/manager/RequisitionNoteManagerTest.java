package com.facishare.crm.requisitionnote.manager;

import com.facishare.crm.requisitionnote.base.BaseTest;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.metadata.api.IObjectData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liangk
 * @date 21/01/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class RequisitionNoteManagerTest extends BaseTest {
    @Resource
    private RequisitionNoteManager requisitionNoteManager;

    @Resource
    private ServiceFacade serviceFacade;

    public RequisitionNoteManagerTest() {}

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void findDetailObjectDataListTest() {
        String id = "5ae2bfc8bab09cfeedaa916b";
        IObjectData objectData = serviceFacade.findObjectDataIncludeDeleted(user, id, RequisitionNoteConstants.API_NAME);
        List<IObjectData> objectDataList = requisitionNoteManager.findDetailObjectDataIncludeInvalid(user, objectData);
        System.out.println("-----------------------------------------");
        System.out.println(objectDataList);
    }

}
