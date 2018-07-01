package com.facishare.crm.deliverynote.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.exception.DeliveryNoteErrorCode;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.dto.DetailObjectListResult;
import com.facishare.paas.appframework.metadata.dto.RecordTypeLayoutStructure;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.TableComponent;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DeliveryNoteDescribeLayoutController extends StandardDescribeLayoutController {

    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private StockManager stockManager = SpringUtil.getContext().getBean(StockManager.class);

    @Override
    public void before(Arg arg) {
        // 校验是否有出库单的创建权限
        User user = this.controllerContext.getUser();

        if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType)) {
            boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
            if (isStockEnable) {
                deliveryNoteManager.checkOutboundDeliveryNoteCreateRight(user);
            }
        } else if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
            boolean isStockEnable = stockManager.isStockEnable(user.getTenantId());
            IObjectData objectData = deliveryNoteManager.getObjectDataById(this.controllerContext.getUser(), arg.getData_id());
            String deliveryWarehouseId = objectData.get(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName, String.class);
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
            if (isStockEnable
                    && StringUtils.isNotEmpty(deliveryWarehouseId)
                    && Objects.equals(lifeStatus, SystemConstants.LifeStatus.Ineffective.value)) {
                deliveryNoteManager.checkOutboundDeliveryNoteCreateRight(user);
            }
        }
    }

    @Override
    public Result after(Arg arg, Result result) {
        // 新建、编辑页面隐藏状态字段
        hiddenStatusField(arg, result);

        // 设置不可编辑字段为只读
        setEditLayoutReadonlyField(arg, result);

        return result;
    }

    private void hiddenStatusField(Arg arg, Result result) {
        try {
            if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType)
                    || Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
                ILayout layout = result.getLayout().toLayout();
                // 隐藏状态字段
                removeFieldSection(layout, Lists.newArrayList(DeliveryNoteObjConstants.Field.Status.apiName));
                // 隐藏收货备注字段
                removeFieldSection(layout, Lists.newArrayList(DeliveryNoteObjConstants.Field.ReceiveRemark.apiName));
                // 隐藏收货备日期段
                removeFieldSection(layout, Lists.newArrayList(DeliveryNoteObjConstants.Field.ReceiveDate.apiName));
                // 隐藏本次收货数及收货备注
                removeRealStockAndReceiveRemarkFiled(result);
            }
        } catch (Exception e) {
            log.error("新建发货单页面隐藏状态字段异常。 arg[{}], result[{}]", arg, result, e);
        }
    }

    private void setEditLayoutReadonlyField(Arg arg, Result result) {
        try {
            if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
                IObjectData objectData = deliveryNoteManager.getObjectDataById(this.controllerContext.getUser(), arg.getData_id());
                String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

                //如果生命状态是未生效，则不限制字段只读
                if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                    return;
                }
                //设置主对象字段只读
                ILayout layout = result.getLayout().toLayout();
                setReadOnly(layout, DeliveryNoteObjConstants.READONLY_FIELD_API_NAMES_FOR_NORMAL_EDIT);

                //设置从对象只读字段
                result.getDetailObjectList().stream()
                        .filter(detailObjectListResult -> Objects.equals(detailObjectListResult.getObjectDescribe().get("api_name"), DeliveryNoteProductObjConstants.API_NAME))
                        .forEach(detailObjectListResult -> {
                            List<RecordTypeLayoutStructure> layoutList = detailObjectListResult.getLayoutList();
                            if (com.facishare.paas.appframework.common.util.CollectionUtils.notEmpty(layoutList)) {
                                layoutList.forEach(recordTypeLayoutStructure -> {
                                    ILayout detailLayout = LayoutDocument.of(recordTypeLayoutStructure.getDetail_layout()).toLayout();
                                    setReadOnly(detailLayout, DeliveryNoteProductObjConstants.READONLY_FIELD_API_NAMES_FOR_NORMAL_EDIT);
                                });
                            }
                        });
            }
        } catch (Exception e) {
            log.error("入库单新建，编辑页面隐藏调拨单字段异常。 arg[{}], result[{}]", arg, result, e);
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
            log.error("layout[{}], readOnlyFieldApiNames[{}]", layout, readOnlyFieldApiNames, e);
            throw new com.facishare.crm.exception.DeliveryNoteBusinessException(DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, e.getMessage());
        }
    }

    private void removeFieldSection(ILayout layout, List<String> filedNames) throws MetadataServiceException {
        layout.getComponents().forEach(iComponent -> {
            if (Objects.equals(iComponent.getName(), LayoutConstants.FORM_COMPONENT_API_NAME)) {
                FormComponent fc = (FormComponent) iComponent;
                fc.getFieldSections().forEach(fieldSection -> {
                    if (fieldSection.getName().equals(LayoutConstants.BASE_FIELD_SECTION_API_NAME)) {
                        List<IFormField> formFields = fieldSection.getFields().stream()
                                .filter(iFormField -> !filedNames.contains(iFormField.getFieldName()))
                                .collect(Collectors.toList());
                        fieldSection.setFields(formFields);
                    }
                });
            }
        });
    }

    private void removeIncludeField(ILayout layout, List<String> filedNames) throws MetadataServiceException {
        layout.getComponents().forEach(iComponent -> {
            TableComponent tc = (TableComponent) iComponent;
            tc.setIncludeFields(tc.getIncludeFields().stream()
                    .filter(iTableColumn -> !filedNames.contains(iTableColumn.getName())).collect(Collectors.toList()));
        });
    }

    private void removeRealStockAndReceiveRemarkFiled(Result result) {
        List<DetailObjectListResult> detailObjectListResultList = result.getDetailObjectList();
        if (CollectionUtils.notEmpty(detailObjectListResultList)) {
            detailObjectListResultList.forEach(detailObjectListResult -> {
                List<RecordTypeLayoutStructure> layoutList = detailObjectListResult.getLayoutList();
                if (CollectionUtils.notEmpty(layoutList)) {
                    List<String> removeFieldApiNames = Lists.newArrayList(DeliveryNoteProductObjConstants.Field.RealReceiveNum.apiName, DeliveryNoteProductObjConstants.Field.ReceiveRemark.apiName);
                    layoutList.stream()
                            .filter(detailLayoutStructure -> Objects.equals(detailLayoutStructure.getDetail_layout().get(ILayout.REF_OBJECT_API_NAME), DeliveryNoteProductObjConstants.API_NAME))
                            .forEach(detailLayoutStructure -> {
                                // web侧用detail_layout控制
                                ILayout detailLayout = LayoutDocument.of(detailLayoutStructure.getDetail_layout()).toLayout();
                                try {
                                    this.removeFieldSection(detailLayout, removeFieldApiNames);
                                } catch (MetadataServiceException e) {
                                    log.error("detailLayout[{}], removeFieldApiNames[{}]", detailLayout, removeFieldApiNames, e);
                                    throw new com.facishare.crm.exception.DeliveryNoteBusinessException(com.facishare.crm.exception.DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, e.getMessage());
                                }
                            });

                    layoutList.stream()
                            .filter(detailLayoutStructure -> Objects.equals(detailLayoutStructure.getList_layout().get(ILayout.REF_OBJECT_API_NAME), DeliveryNoteProductObjConstants.API_NAME))
                            .forEach(detailLayoutStructure -> {
                                // app侧用list_layout控制
                                ILayout listLayout = LayoutDocument.of(detailLayoutStructure.getList_layout()).toLayout();
                                try {
                                    this.removeIncludeField(listLayout, removeFieldApiNames);
                                } catch (MetadataServiceException e) {
                                    log.error("listLayout[{}], removeFieldApiNames[{}]", listLayout, removeFieldApiNames, e);
                                    throw new com.facishare.crm.exception.DeliveryNoteBusinessException(com.facishare.crm.exception.DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, e.getMessage());

                                }
                            });
                }
            });
        }
    }
}
