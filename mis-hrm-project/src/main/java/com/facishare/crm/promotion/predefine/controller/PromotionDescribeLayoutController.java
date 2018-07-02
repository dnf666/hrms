package com.facishare.crm.promotion.predefine.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.crm.promotion.enums.PromotionTypeEnum;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.dto.DetailObjectListResult;
import com.facishare.paas.appframework.metadata.dto.RecordTypeLayoutStructure;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.ObjectDescribe;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PromotionDescribeLayoutController extends StandardDescribeLayoutController {

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        try {
            String recordType = arg.getRecordType_apiName();
            ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(new ObjectDescribe(result.getObjectDescribe()));
            //根据不用业务列席过滤 type字段的选项值
            SelectOneFieldDescribe typeFieldDescribe = (SelectOneFieldDescribe) objectDescribeExt.getFieldDescribe(PromotionConstants.Field.Type.apiName);
            List<ISelectOption> selectOptions = typeFieldDescribe.getSelectOptions();
            Iterator<ISelectOption> selectOptionIterator = selectOptions.iterator();
            while (selectOptionIterator.hasNext()) {
                ISelectOption selectOption = selectOptionIterator.next();
                if (PromotionRecordTypeEnum.OrderPromotion.apiName.equals(recordType)) {
                    if (PromotionTypeEnum.isProductPromotion(selectOption.getValue())) {
                        selectOptionIterator.remove();
                    }
                } else if (PromotionRecordTypeEnum.ProductPromotion.apiName.equals(recordType)) {
                    if (!PromotionTypeEnum.isProductPromotion(selectOption.getValue())) {
                        selectOptionIterator.remove();
                    }
                }
            }
            typeFieldDescribe.setSelectOptions(selectOptions);

            String layoutType = arg.getLayout_type();
            List<DetailObjectListResult> detailObjectList = result.getDetailObjectList();
            List<DetailObjectListResult> allDetailObjectList = result.getAllDetailObjectList();
            //业务类型为订单促销时，屏蔽促销产品从对象tab
            if (PromotionRecordTypeEnum.OrderPromotion.apiName.equals(recordType)) {
                detailObjectList.removeIf(detail -> detail.getRelatedListName().equals(PromotionProductConstants.Field.Promotion.targetRelatedListName));
                allDetailObjectList.removeIf(detail -> detail.getRelatedListName().equals(PromotionProductConstants.Field.Promotion.targetRelatedListName));
            }
            //新建和编辑时 获取描述，过滤掉其他业务类型的描述
            readOnly(recordType, layoutType, detailObjectList);
            readOnly(recordType, layoutType, allDetailObjectList);

            // 控制Normal状态下只可编辑促销启用状态
            if (Objects.nonNull(arg.getData_id())) {
                IObjectData objectData = serviceFacade.findObjectData(controllerContext.getUser(), arg.getData_id(), arg.getApiname());
                String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);
                if (!SystemConstants.LifeStatus.Normal.value.equals(lifeStatus) && !SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
                    return result;
                }
            }
            List<IComponent> components = new Layout(result.getLayout()).getComponents();
            if (StringUtils.isNotEmpty(layoutType) && layoutType.equals("edit")) {
                for (IComponent component : components) {
                    if (component.getName().equals(LayoutConstants.FORM_COMPONENT_API_NAME)) {
                        FormComponent fc = (FormComponent) component;
                        List<IFieldSection> fields = fc.getFieldSections();
                        for (IFieldSection field : fields) {
                            List<IFormField> formFields = field.getFields();
                            Iterator iterator = formFields.iterator();
                            while (iterator.hasNext()) {
                                IFormField formField = (IFormField) iterator.next();
                                Set<String> notEditFieldApiNames = Sets.newHashSet(PromotionConstants.Field.Name.apiName, PromotionConstants.Field.Type.apiName, PromotionConstants.Field.StartTime.apiName, PromotionConstants.Field.Images.apiName, PromotionConstants.Field.EndTime.apiName, PromotionConstants.Field.CustomerRange.apiName);
                                if (notEditFieldApiNames.contains(formField.getFieldName())) {
                                    formField.setReadOnly(true);
                                }
                            }
                            field.setFields(formFields);
                        }
                    }
                }
            }
        } catch (MetadataServiceException e) {
            log.warn("PromotionDescribeLayoutController,arg:{}", arg, e);
        }
        return result;
    }

    public void readOnly(String recordType, String layoutType, List<DetailObjectListResult> detailObjectListResults) {
        detailObjectListResults.forEach(detailObjectListResult -> {
            if (PromotionRuleConstants.Field.Promotion.targetRelatedListName.equals(detailObjectListResult.getRelatedListName())) {
                List<RecordTypeLayoutStructure> recordTypeLayoutStructureList = detailObjectListResult.getLayoutList();
                recordTypeLayoutStructureList.removeIf(x -> !x.getRecord_type().equals(arg.getRecordType_apiName()));
            }
            if ("edit".equals(layoutType)) {
                detailObjectListResult.getLayoutList().stream().filter(recordTypeLayoutStructure -> {
                    if (arg.getRecordType_apiName().equals(PromotionRecordTypeEnum.OrderPromotion.apiName)) {
                        return recordTypeLayoutStructure.getRecord_type().equals(PromotionRecordTypeEnum.OrderPromotion.apiName);
                    } else if (arg.getRecordType_apiName().equals(PromotionRecordTypeEnum.ProductPromotion.apiName)) {
                        return recordTypeLayoutStructure.getRecord_type().equals(PromotionRecordTypeEnum.ProductPromotion.apiName);
                    }
                    return false;
                }).forEach(recordTypeLayoutStructure -> {
                    Map<String, Object> map = recordTypeLayoutStructure.getDetail_layout();
                    List<Map> components = (List<Map>) map.get("components");
                    components.stream().filter(component -> component.get("api_name").toString().equals("form_component")).forEach(component -> {
                        List<Map> fieldSections = (List<Map>) component.get("field_section");
                        Map<String, Object> fieldSection = fieldSections.get(0);
                        List<Map> fields = (List<Map>) fieldSection.get("form_fields");
                        fields.forEach(field -> field.put("is_readonly", true));
                    });
                });
            }
        });
    }
}
