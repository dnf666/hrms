package com.facishare.crm.deliverynote.controller;

import com.facishare.crm.deliverynote.base.BaseControllerTest;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedListController;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class DeliveryNoteProductRelatedListControllerTest extends BaseControllerTest {
    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }


    public DeliveryNoteProductRelatedListControllerTest() {
        super(DeliveryNoteProductObjConstants.API_NAME);
    }

    @Test
    public void relatedListTest() {

        String argJson = "{\"associate_object_data_id\":\"5a658556830bdba29501ae53\",\"associate_object_describe_api_name\":\"DeliveryNoteObj\",\"associated_object_describe_api_name\":\"DeliveryNoteProductObj\",\"associated_object_field_related_list_name\":\"target_related_list_dnp_delivery_note_id\",\"include_associated\":true,\"search_query_info\":\"{\\\"limit\\\":10000,\\\"offset\\\":0}\"}";
        new Gson().fromJson(argJson, StandardRelatedListController.Arg.class);

        StandardRelatedListController.Arg arg = new Gson().fromJson(argJson, StandardRelatedListController.Arg.class);
        Object result = execute("RelatedList", arg);
        System.out.println(result);
    }
}
