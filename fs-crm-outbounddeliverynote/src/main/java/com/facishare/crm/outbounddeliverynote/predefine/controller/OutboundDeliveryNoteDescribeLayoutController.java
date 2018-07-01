package com.facishare.crm.outbounddeliverynote.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.exception.StockBusinessException;
import com.facishare.crm.stock.exception.StockErrorCode;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.dto.DetailObjectListResult;
import com.facishare.paas.appframework.metadata.dto.RecordTypeLayoutStructure;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/15
 */
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteDescribeLayoutController extends StandardDescribeLayoutController {

    @Override
    protected Result doService(Arg arg) {
        Result result = super.doService(arg);
        log.info("OutboundDeliveryNoteDescribeLayout", arg);



        if (Objects.equals(arg.getRecordType_apiName(), OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName)
                && (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType) || Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType))) {
            log.info("OutboundDeliveryNoteDescribeLayout filter", arg);
            //过滤字段
            filterField(result);
            //过滤出库选项
            filterOption(result);
//            //过滤出库单产品库存字段
//            filterStock(result);
            if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
                setReadOnlyField(arg, result);
            }
        }



        return result;
    }

    private void filterField(Result result) {
        LayoutDocument layoutDocument = result.getLayout();
        List<Map<String, Object>> components = (List<Map<String, Object>>) layoutDocument.get("components");
        if (!CollectionUtils.isEmpty(components)) {
            List<Map<String, Object>> fieldSections = (List<Map<String, Object>>) components.get(0).get("field_section");
            if (!CollectionUtils.isEmpty(fieldSections)) {
                List<Map<String, String>> formSections = (List<Map<String, String>>) fieldSections.get(0).get("form_fields");
                formSections.removeIf(formSectionMap -> Objects.equals(formSectionMap.get("field_name"), OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName) || Objects.equals(formSectionMap.get("field_name"), OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName));
            }
        }

    }

    private void filterOption(Result result) {
        ObjectDescribeDocument describeDocument = result.getObjectDescribe();
        Map<String, Map<String, Object>> fields = (Map<String, Map<String, Object>>) describeDocument.get("fields");
        Map<String, Object> outboundType = fields.get(OutboundDeliveryNoteConstants.Field.Outbound_Type.apiName);
        List<Map<String, String>> options = (List<Map<String, String>>) outboundType.get("options");
        List<String> unValidOptions = Arrays.asList(OutboundTypeEnum.SALES_OUTBOUND.value, OutboundTypeEnum.REQUISITION_OUTBOUND.value);
        options.removeIf(option -> {
            String value = option.get("value");
            return unValidOptions.contains(value);
        });
    }


    private void filterStock(Result result) {
        List<DetailObjectListResult> detailObjectListResults = result.getAllDetailObjectList();
        if (!CollectionUtils.isEmpty(detailObjectListResults)) {
            List<RecordTypeLayoutStructure> layoutList = detailObjectListResults.get(0).getLayoutList();
            if (!CollectionUtils.isEmpty(layoutList)) {
                Map<String, Object> detailLayout = layoutList.get(0).getDetail_layout();
                List<Map<String, Object>> components = (List<Map<String, Object>>) detailLayout.get("components");
                if (!CollectionUtils.isEmpty(components)) {
                    List<Map<String, Object>> fieldSections = (List<Map<String, Object>>) components.get(0).get("field_section");
                    if (!CollectionUtils.isEmpty(fieldSections)) {
                        List<Map<String, String>> formSections = (List<Map<String, String>>) fieldSections.get(0).get("form_fields");
                        formSections.removeIf(formSectionMap -> Objects.equals(formSectionMap.get("field_name"), OutboundDeliveryNoteProductConstants.Field.Stock.apiName));
                    }
                }
            }
        }
    }

    private void setReadOnlyField(Arg arg, Result result) {
        try {
            IObjectData objectData = serviceFacade.findObjectData(controllerContext.getUser(), arg.getData_id(), OutboundDeliveryNoteConstants.API_NAME);
            if (objectData == null) {
                throw new StockBusinessException(StockErrorCode.BUSINESS_ERROR, "查询出库单对象失败");
            }

            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                return;
            }

            List<String> outboundDeliveryNoteReadOnlyFieldsForEdit = Lists.newArrayList(OutboundDeliveryNoteConstants.Field.Warehouse.apiName,
                    GoodsReceivedNoteConstants.Field.Name.apiName);

            List<String> outboundDeliveryNoteProductReadOnlyFieldsForEdit = Lists.newArrayList(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName,
                    OutboundDeliveryNoteProductConstants.Field.Product.apiName,
                    OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName);

            //设置主对象字段只读
            ILayout layout = result.getLayout().toLayout();
            setReadOnly(layout, outboundDeliveryNoteReadOnlyFieldsForEdit);

            //设置从对象字段只读
            //设置从对象只读字段
            result.getDetailObjectList().stream()
                    .filter(detailObjectListResult -> Objects.equals(detailObjectListResult.getObjectDescribe().get("api_name"), OutboundDeliveryNoteProductConstants.API_NAME))
                    .forEach(detailObjectListResult -> {
                        List<RecordTypeLayoutStructure> layoutList = detailObjectListResult.getLayoutList();
                        if (com.facishare.paas.appframework.common.util.CollectionUtils.notEmpty(layoutList)) {
                            layoutList.stream().filter(layoutStructure -> layoutStructure.getRecord_type().equals(MultiRecordType.RECORD_TYPE_DEFAULT))
                                    .forEach(recordTypeLayoutStructure -> {
                                        ILayout detailLayout = LayoutDocument.of(recordTypeLayoutStructure.getDetail_layout()).toLayout();
                                        setReadOnly(detailLayout, outboundDeliveryNoteProductReadOnlyFieldsForEdit);
                                    });
                        }});
        } catch (Exception e) {
            log.error("出库单编辑页面设置字段只读异常。 arg[{}], result[{}]", arg, result, e);
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
