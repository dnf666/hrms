package com.facishare.crm.requisitionnote.service;

import com.facishare.crm.requisitionnote.base.BaseServiceTest;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.predefine.service.RequisitionNoteService;
import com.facishare.crm.requisitionnote.predefine.service.dto.RequisitionNoteType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author liangk
 * @date 15/03/2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class RequisitionNoteServiceTest extends BaseServiceTest {
    @Resource
    private RequisitionNoteService requisitionService;

    public RequisitionNoteServiceTest() {
        super(RequisitionNoteConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void enableRequisitionTest() {
        RequisitionNoteType.EnableRequisitionResult enableResult = requisitionService.enableRequisition(newServiceContext());
        System.out.println(enableResult);
    }

    @Test
    public void isConfirmedTest() {
        String requisitionId = "5acdecc5bab09c781cf09825";
        RequisitionNoteType.IsConfirmedArg arg = new RequisitionNoteType.IsConfirmedArg();
        arg.setRequisitionNoteId(requisitionId);
        requisitionService.isConfirmed(newServiceContext(), arg);
    }

    @Test
    public void addFieldAndLayoutTest() {
        requisitionService.addFieldAndData(newServiceContext());
    }
}


