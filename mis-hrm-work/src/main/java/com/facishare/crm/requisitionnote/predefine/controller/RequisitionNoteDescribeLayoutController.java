package com.facishare.crm.requisitionnote.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteBusinessException;
import com.facishare.crm.requisitionnote.exception.RequisitionNoteErrorCode;
import com.facishare.crm.requisitionnote.predefine.manager.RequisitionNoteManager;
import com.facishare.crm.requisitionnote.predefine.service.dto.RequisitionNoteType;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.dto.RecordTypeLayoutStructure;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.util.SpringUtil;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteDescribeLayoutController extends StandardDescribeLayoutController {
    private RequisitionNoteManager requisitionNoteManager= SpringUtil.getContext().getBean(RequisitionNoteManager.class);

    @Override
    public void before(Arg arg) {
        //1、校验是否有出库单的创建权限
        User user = this.controllerContext.getUser();
        if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType)) {
            checkFuncPrivilege(user);
        } else if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
            IObjectData objectData = requisitionNoteManager.findById(this.controllerContext.getUser(), arg.getData_id(), RequisitionNoteConstants.API_NAME);
            if (Objects.equals(objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class), SystemConstants.LifeStatus.Ineffective.value)) {
                checkFuncPrivilege(user);
            }
        }
    }

    private void checkFuncPrivilege(User user) {
        List<String> actionCodes = Lists.newArrayList(ObjectAction.CREATE.getActionCode());
        Map<String, Boolean> funPrivilegeMap = serviceFacade.funPrivilegeCheck(user, OutboundDeliveryNoteConstants.API_NAME, actionCodes);
        if (!funPrivilegeMap.get(ObjectAction.CREATE.getActionCode())) {
            throw new RequisitionNoteBusinessException(RequisitionNoteErrorCode.BUSINESS_ERROR, "新建调拨单需要同时拥有出库单的新建权限，请联系CRM管理员添加");
        }
    }

    @Override
    public Result after(Arg arg, Result result) {
        // 新建、编辑页面隐藏"是否已确认入库"字段
        hideFieldOfInboundConfirmed(arg, result);

        //编辑页面时设置某些字段为只读
        if (StringUtils.isNotEmpty(arg.getLayout_type()) && Objects.equals(SystemConstants.LayoutType.Edit.layoutType, arg.getLayout_type())) {
            modifyRequisitionNoteFieldWhenEdit(arg, result);
        }

        return result;
    }

    private void hideFieldOfInboundConfirmed(Arg arg, Result result) {
        try {
            if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType)
                    || Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
                ILayout layout = result.getLayout().toLayout();
                layout.getComponents().forEach(iComponent -> {
                    if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)) {
                        FormComponent fc = (FormComponent) iComponent;
                        fc.getFieldSections().forEach(fieldSection -> {
                            if (fieldSection.getName().equals(LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                                List<IFormField> formFields = fieldSection.getFields().stream()
                                        .filter(iFormField -> !iFormField.getFieldName().equals(RequisitionNoteConstants.Field.InboundConfirmed.apiName))
                                        .collect(Collectors.toList());
                                fieldSection.setFields(formFields);
                            }
                        });
                    }
                });
            }
        } catch (Exception e) {
            log.error("新建调拨单页面隐藏是否已确认入库字段异常。 arg[{}], result[{}]", arg, result, e);
        }
    }

    private void modifyRequisitionNoteFieldWhenEdit(Arg arg, Result result) {
        try {
            IObjectData objectData = requisitionNoteManager.findById(this.controllerContext.getUser(), arg.getData_id(), RequisitionNoteConstants.API_NAME);
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

            //如果生命状态是未生效，则不限制字段只读
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                return;
            }

            //设置主对象字段只读
            ILayout layout = result.getLayout().toLayout();
            setReadOnly(layout, RequisitionNoteType.masterReadOnlyFields);

            //设置从对象字段只读
            result.getDetailObjectList().stream()
                    .filter(detailObjectListResult -> Objects.equals(detailObjectListResult.getObjectDescribe().get("api_name"), RequisitionNoteProductConstants.API_NAME))
                    .forEach(detailObjectListResult -> {
                        List<RecordTypeLayoutStructure> layoutList = detailObjectListResult.getLayoutList();
                        if (com.facishare.paas.appframework.common.util.CollectionUtils.notEmpty(layoutList)) {
                            layoutList.stream().filter(layoutStructure -> layoutStructure.getRecord_type().equals(MultiRecordType.RECORD_TYPE_DEFAULT))
                                    .forEach(recordTypeLayoutStructure -> {
                                        ILayout detailLayout = LayoutDocument.of(recordTypeLayoutStructure.getDetail_layout()).toLayout();
                                        setReadOnly(detailLayout, RequisitionNoteType.detailsReadOnlyFields);
                                    });
                        }
                    });

        } catch (Exception e) {
            log.error("调拨单编辑页面设置字段只读异常。 arg[{}], result[{}]", arg, result, e);
        }
    }

    private void setReadOnly(ILayout layout, List<String> readOnlyFieldApiNames) {
        try {
            layout.getComponents().forEach(iComponent -> {
                if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)) {
                    FormComponent fc = (FormComponent) iComponent;
                    fc.getFieldSections().forEach(fieldSection -> {
                        if (fieldSection.getName().equals(LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                            List<IFormField> formFields = fieldSection.getFields().stream()
                                    .map(iFormField -> {
                                        if (readOnlyFieldApiNames.contains(iFormField.getFieldName())) {
                                            iFormField.setReadOnly(true);
                                        }
                                        return iFormField;
                                    }).collect(Collectors.toList());
                            fieldSection.setFields(formFields);
                        }
                    });
                }
            });
        } catch (MetadataServiceException e) {
            throw new RuntimeException(e);
        }
    }
}
