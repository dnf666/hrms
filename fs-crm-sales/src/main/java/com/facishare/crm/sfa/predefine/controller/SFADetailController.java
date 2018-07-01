package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.crm.valueobject.SessionContext;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.github.autoconf.ConfigFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

/**
 * Created by luohl on 2017/11/14.
 */
@Slf4j
public class SFADetailController extends StandardDetailController {

    static String refObjOrderSetting;
    static Map<String, List<String>> invisibleReferenceApiNamesForMobilemap;

    static {
        ConfigFactory.getInstance().getConfig("fs-crm-java-detailpage-layout-setting", config -> {
            refObjOrderSetting = config.get("RefObjOrderSetting");
        });
        ConfigFactory.getInstance().getConfig("fs-crm-java-special-field", (config) -> {
            try {
                String invisibleReferenceApiNamesForMobileStr = config.get("invisibleReferenceApiNamesForMobile").trim();
                invisibleReferenceApiNamesForMobilemap = (Map) JSONObject.parse(invisibleReferenceApiNamesForMobileStr);
            } catch (Exception var11) {
                log.error("fs-crm-java-special-field config has error");
            }

        });
    }

    @Override
    protected Result after(Arg arg, Result result) {
        Result newResult = super.after(arg, result);

        ILayout layout = new Layout(newResult.getLayout());
        PreDefLayoutUtil.invisibleRefObjectListAddButton(arg.getObjectDescribeApiName(), layout);
        PreDefLayoutUtil.invisibleRefObjectListRelationButton(arg.getObjectDescribeApiName(), layout);
        PreDefLayoutUtil.invisibleReferenceObject(arg.getObjectDescribeApiName(), layout);
        PreDefLayoutUtil.invisibleRefObjectListAllButtonForSpecifiedRelatedObject(layout);
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            PreDefLayoutUtil.invisibleReferenceObjectForMobile(arg.getObjectDescribeApiName(), layout, invisibleReferenceApiNamesForMobilemap);
        }
        modifyLayoutButtonAddPartner(controllerContext.getUser(), layout, describe, data);
        return newResult;
    }

    protected SessionContext getSessionContext() {
        SessionContext sessionContext = new SessionContext();
        User user = getControllerContext().getUser();
        sessionContext.setEId(Long.parseLong(user.getTenantId()));
        sessionContext.setUserId(Integer.parseInt(user.getUserId()));
        return sessionContext;
    }

    /**
     * 根据（公海、線索池）的设置动态隐藏相关列表对象及隐藏字段
     */
    protected void handleRelatedListByHighSeaSetting(ILayout layout) {
        //移除外部企业字段
        List<String> needHideFieldList = Lists.newArrayList();

        boolean showRelatedObjAndSomeFieldsByHighSeaSetting = serviceFacade.checkNeedShowRelatedObjs(
                getControllerContext().getUser(), arg.getObjectDataId(), arg.getObjectDescribeApiName(), needHideFieldList);

        if (!showRelatedObjAndSomeFieldsByHighSeaSetting) {
            //隐藏相关列表对象
            GroupComponent relatedListGroupComponent = new GroupComponent();
            List<IComponent> childComponents = new ArrayList<>();
            relatedListGroupComponent.setName("relatedObject");
            relatedListGroupComponent.setHeader("相关");
            relatedListGroupComponent.setChildComponents(childComponents);

            try {
                List<IComponent> components = new ArrayList<>();
                layout.getComponents().forEach(x -> {
                    if (x.getName().equals("relatedObject")) {
                        components.add((relatedListGroupComponent));
                    } else {
                        components.add(x);
                    }
                });
                layout.setComponents(components);
            } catch (MetadataServiceException e) {
                log.error("getComponents error", e);
            }
        }

        //隐藏详细信息和顶部信息中的字段
        if (!CollectionUtils.isEmpty(needHideFieldList)) {

            LayoutExt.of(layout).getDetailInfoComponent().ifPresent(detailInfoComponent -> {
                try {
                    Optional<IComponent> fromComponent = detailInfoComponent.getChildComponents().stream()
                            .findFirst();
                    if (fromComponent.isPresent()) {
                        PreDefLayoutUtil.removeSomeFields(
                                (FormComponent) fromComponent.get(),
                                new HashSet<>(needHideFieldList));
                    }
                } catch (MetadataServiceException e) {
                    log.error("getComponents error", e);
                }

            });

            LayoutExt.of(layout).getTopInfoComponent().ifPresent(topInfoComponent -> {
                PreDefLayoutUtil.removeSomeFields(topInfoComponent, new HashSet<>(needHideFieldList));
            });
        }

    }

    /**
     * 修改对象布局增加合作伙伴操作
     * 对象中已有合作伙伴时操作：更换合作伙伴、更换外部负责人、移除合作伙伴
     * 对象中无合作伙伴时操作：更换合作伙伴
     */
    public void modifyLayoutButtonAddPartner(User user, ILayout layout, IObjectDescribe objectDescribe, IObjectData objectData) {
        if (!PartnerConstants.SUPPORT_PARTNER_APINAME.contains(objectDescribe.getApiName())) {
            return;
        }
        String nowPartner = objectData.get(PartnerConstants.FIELD_PARTNER_ID, String.class);
        ArrayList<ObjectAction> partnerObjectAction = Lists.newArrayList(ObjectAction.CHANGE_PARTNER, ObjectAction.DELETE_PARTNER, ObjectAction.CHANGE_PARTNER_OWNER);
        List<String> partnerActionCodes = partnerObjectAction.stream().map(k -> k.getActionCode()).collect(Collectors.toList());
        Map<String, Boolean> partnerPrivilege = serviceFacade.funPrivilegeCheck(user, objectDescribe.getApiName(), partnerActionCodes);
        List<IButton> buttons = partnerObjectAction.stream()
                .filter(k -> partnerPrivilege.getOrDefault(k.getActionCode(), false))
                .filter(k -> {
                    if (StringUtils.isBlank(nowPartner) && !k.getActionCode().equals(ObjectAction.CHANGE_PARTNER.getActionCode())) {
                        return false;
                    }
                    //如果是更换外部负责人,但没有外部企业时
                    if (k.getActionCode().equals(ObjectAction.CHANGE_PARTNER_OWNER.getActionCode()) && objectData.getOutTenantId() == null) {
                        return false;
                    }
                    return true;
                })
                .map(k -> createButton(objectDescribe, k)).collect(Collectors.toList());
        buttons.stream().forEach(k -> layout.addButtons(k));
    }

    protected void removeLayoutDetailInfoField(ILayout layout, List<String> needHideFieldList) {
        //隐藏详细信息和顶部信息中的字段
        if (!CollectionUtils.isEmpty(needHideFieldList)) {
            LayoutExt.of(layout).getDetailInfoComponent().ifPresent(detailInfoComponent -> {
                try {
                    Optional<IComponent> fromComponent = detailInfoComponent.getChildComponents().stream()
                            .findFirst();
                    if (fromComponent.isPresent()) {
                        PreDefLayoutUtil.removeSomeFields(
                                (FormComponent) fromComponent.get(),
                                new HashSet<>(needHideFieldList));
                    }
                } catch (MetadataServiceException e) {
                    log.error("getComponents error", e);
                }

            });
        }
    }

    private IButton createButton(IObjectDescribe objectDescribe, ObjectAction objectAction) {
        IButton button = new Button();
        button.setAction(objectAction.getActionCode());
        button.setLabel(objectAction.getActionLabel());
        button.setName(objectDescribe.getApiName() + "_" + objectAction.getActionCode() + "_button_" + IButton.ACTION_TYPE_DEFAULT);
        button.setActionType(IButton.ACTION_TYPE_DEFAULT);
        return button;
    }
}
