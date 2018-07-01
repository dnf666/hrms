package com.facishare.crm.sfainterceptor.base;

import com.facishare.paas.appframework.core.model.Controller;
import com.facishare.paas.appframework.core.model.ControllerContext;
import com.facishare.paas.appframework.core.model.ControllerLocateService;
import com.facishare.paas.appframework.core.model.SerializerManager;
import com.facishare.paas.appframework.core.predef.controller.BaseListController.Result;
import com.facishare.paas.appframework.core.predef.controller.StandardController;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.core.predef.controller.StandardListController;
import com.facishare.paas.appframework.core.predef.controller.StandardListHeaderController;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseControllerTest extends BaseTest {
    @Autowired
    private ControllerLocateService controllerLocateService;

    @Autowired
    private SerializerManager serializerManager;
    protected String apiName;

    public BaseControllerTest(String apiName) {
        this.apiName = apiName;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> Object execute(String methodName, T arg) {
        //StandardController
        ControllerContext controllerContext = new ControllerContext(requestContext, apiName, methodName);
        String body = serializerManager.getSerializer(requestContext.getContentType()).encode(arg);
        Controller controller = controllerLocateService.locateController(controllerContext, body);
        return controller.service(controller.getArg());
    }

    public <T> Result executeList(StandardListController.Arg arg) {
        arg.setObjectDescribeApiName(apiName);
        return (Result) execute(StandardController.List.name(), arg);
    }

    public <T> StandardDetailController.Result executeDetail(StandardDetailController.Arg arg) {
        arg.setObjectDescribeApiName(apiName);
        return (StandardDetailController.Result) execute(StandardController.Detail.name(), arg);
    }

    public <T> StandardListHeaderController.Result executeListHeader(StandardListHeaderController.Arg arg) {
        arg.setApiName(apiName);
        return (StandardListHeaderController.Result) execute(StandardController.ListHeader.name(), arg);
    }

}
