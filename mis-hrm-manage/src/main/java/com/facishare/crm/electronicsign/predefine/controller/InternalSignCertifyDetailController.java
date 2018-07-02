package com.facishare.crm.electronicsign.predefine.controller;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SystemConstants;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.predefine.manager.obj.InternalSignCertifyObjManager;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * '内部签章认证'详细页面
 */
@Slf4j
public class InternalSignCertifyDetailController extends StandardDetailController {
    private InternalSignCertifyObjManager internalSignCertifyObjManager = SpringUtil.getContext().getBean(InternalSignCertifyObjManager.class);
    private ServiceFacade serviceFacade;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        this.serviceFacade = SpringUtil.getContext().getBean(ServiceFacade.class);
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        filterEditButton(result);

        //"更多"里面添加按钮"启用"和"停用"
        addEnableAndDisableButton(result, arg.getObjectDataId());

        return result;
    }

    private void filterEditButton(Result result) {
        ObjectDataDocument dataDocument = result.getData();
        String certifyStatus = ObjectDataExt.of(dataDocument).get(InternalSignCertifyObjConstants.Field.CertifyStatus.getApiName(), String.class);
        if (Objects.equals(certifyStatus, CertifyStatusEnum.CERTIFYING.getStatus())
                || Objects.equals(certifyStatus, CertifyStatusEnum.CRTTIFIED.getStatus())) {
            ILayout layout = new Layout(result.getLayout());
            List<IButton> buttons = LayoutExt.of(layout).getButtons()
                    .stream()
                    .filter(iButton -> !Objects.equals(SystemConstants.ActionCode.Edit.getActionCode(), iButton.getAction()))
                    .collect(Collectors.toList());
            LayoutExt.of(layout).setButtons(buttons);
        }
    }

    /**
     * "更多"里面添加按钮"启用"和"停用"
     */
    private void addEnableAndDisableButton(Result result, String internalSignCertifyId) {
        Layout layout = new Layout(result.getLayout());
        List<IButton> buttons = layout.getButtons().isEmpty() ? Lists.newArrayList() : layout.getButtons();
        layout.setButtons(addEnableAndDisableButton(controllerContext.getUser(), buttons, internalSignCertifyId));
        result.setLayout(LayoutDocument.of(layout));
    }


    /**
     * 如果有"启用"权限（是"CRM管理员"角色）, "更多"里面添加"启用"按钮
     * 如果有"停用"权限（是"CRM管理员"角色）, "更多"里面添加"停用"按钮
     */
    private List<IButton> addEnableAndDisableButton(User user, List<IButton> buttons, String internalSignCertifyId) {
        log.info("InternalSignCertifyDetailController, addButtons, user:{}, buttons:{}", user, buttons);

        //启用停用状态
        IObjectData objectData = internalSignCertifyObjManager.queryById(user, internalSignCertifyId);
        //作废了，查到是null
        if (objectData == null) {
            return buttons;
        }
        String certifyStatus = (String) objectData.get(InternalSignCertifyObjConstants.Field.CertifyStatus.apiName);
        String useStatus = (String) objectData.get(InternalSignCertifyObjConstants.Field.UseStatus.apiName);

        //未启用
        if (Objects.equals(useStatus, UseStatusEnum.UN_USE.getStatus()) || Objects.equals(certifyStatus, CertifyStatusEnum.NO_RECORD.getStatus())) {
            return buttons;
        }

        //是否有"查看物流"、"确认收货"权限
        List<String> actionCodes = Lists.newArrayList(InternalSignCertifyObjConstants.Button.Enable.apiName, InternalSignCertifyObjConstants.Button.Disable.apiName);
        Map<String, Boolean> funPrivilegeMap = serviceFacade.funPrivilegeCheck(user, InternalSignCertifyObjConstants.API_NAME, actionCodes);
        log.info("functionPrivilegeService.funPrivilegeCheck user:{}, objectApiName:{}, actionCodes:{}, result:{}", user, InternalSignCertifyObjConstants.API_NAME, actionCodes, funPrivilegeMap);

        //如果有"启用"权限 + '已停用'，添加按钮
        if (funPrivilegeMap.get(InternalSignCertifyObjConstants.Button.Enable.apiName) && Objects.equals(useStatus, UseStatusEnum.OFF.getStatus())) {
            IButton viewLogistics = new Button();
            viewLogistics.setAction("Enable");
            viewLogistics.setActionType(IButton.ACTION_TYPE_DEFAULT);  //于少博：应该用default
            viewLogistics.setName("Enable_button_default");
            viewLogistics.setLabel("启用");
            buttons.add(viewLogistics);
        }

        //如果有"停用"权限 + '已启用'，才有"确认收货"
        if (funPrivilegeMap.get(InternalSignCertifyObjConstants.Button.Disable.apiName) && Objects.equals(useStatus, UseStatusEnum.ON.getStatus())) {
            IButton confirmReceipt = new Button();
            confirmReceipt.setAction("Disable");
            confirmReceipt.setActionType(IButton.ACTION_TYPE_DEFAULT);
            confirmReceipt.setName("Disable_button_default");
            confirmReceipt.setLabel("停用");
            buttons.add(confirmReceipt);
        }

        return buttons;
    }
}
