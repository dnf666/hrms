package com.facishare.crm.stock.predefine.controller;

import com.facishare.crm.constants.LayoutConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.stock.constants.GoodsReceivedNoteProductConstants;
import com.facishare.crm.stock.enums.GoodsReceivedTypeEnum;
import com.facishare.crm.stock.predefine.manager.GoodsReceivedNoteManager;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.dto.RecordTypeLayoutStructure;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liangk
 * @date 23/03/2018
 */
@Slf4j(topic = "stockAccess")
public class GoodsReceivedNoteDescribeLayoutController extends StandardDescribeLayoutController {
    private GoodsReceivedNoteManager goodsReceivedNoteManager = SpringUtil.getContext().getBean(GoodsReceivedNoteManager.class);
    @Override
    public Result after(Arg arg, Result result) {
        super.after(arg, result);
        //编辑页面时限制字段为只读
        if (StringUtils.isNotEmpty(arg.getLayout_type()) && arg.getLayout_type().equals(SystemConstants.LayoutType.Edit.layoutType)) {
            modifyGoodsReceivedNoteFieldWhenEdit(arg, result);
        }

        // 新建、编辑页面隐藏"调拨单编号"字段
        hideGoodsReceivedNoteField(arg, result);

        //新建时隐藏调拨入库类型
        delGoodsReceivedNoteType(arg, result);

        return result;
    }

    private void hideGoodsReceivedNoteField(Arg arg, Result result) {
        try {
            if (Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Add.layoutType)
                    || Objects.equals(arg.getLayout_type(), SystemConstants.LayoutType.Edit.layoutType)) {
                ILayout layout = result.getLayout().toLayout();
                List<String> removeFields = Lists.newArrayList(GoodsReceivedNoteConstants.Field.RequisitionNote.apiName);
                removeFieldSection(layout, removeFields);
            }
        } catch (Exception e) {
            log.error("入库单新建，编辑页面隐藏字段异常。 arg[{}], result[{}]", arg, result, e);
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

    private void delGoodsReceivedNoteType(Arg arg, Result result) {
        //默认业务类型新建时不显示调拨入库类型
        if (Objects.equals(arg.getRecordType_apiName(), MultiRecordType.RECORD_TYPE_DEFAULT)) {
            IObjectDescribe objectDescribe = result.getObjectDescribe().toObjectDescribe();
            List<IFieldDescribe> fieldDescribes = objectDescribe.getFieldDescribes();
            fieldDescribes.stream().forEach(field -> {
                if (Objects.equals(field.getApiName(), GoodsReceivedNoteConstants.Field.GoodsReceivedType.apiName)) {
                    List<Map> optionList = field.get(SelectOneFieldDescribe.OPTIONS, List.class);
                    optionList.removeIf(opt -> Objects.equals(opt.get("label"), GoodsReceivedTypeEnum.REQUISITION.label));
                    field.set(SelectOneFieldDescribe.OPTIONS, optionList);
                }
            });
        }
    }

    private void modifyGoodsReceivedNoteFieldWhenEdit(Arg arg, Result result) {
        try {
            IObjectData objectData = goodsReceivedNoteManager.findById(this.controllerContext.getUser(), arg.getData_id(), GoodsReceivedNoteConstants.API_NAME);
            String lifeStatus = objectData.get(SystemConstants.Field.LifeStatus.apiName, String.class);

            //如果生命状态是未生效，则不限制字段只读
            if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
                return;
            }

            List<String> goodsReceivedNoteReadOnlyFieldsForEdit = Lists.newArrayList(GoodsReceivedNoteConstants.Field.Warehouse.apiName,
                    GoodsReceivedNoteConstants.Field.Name.apiName);

            List<String> goodsReceivedNoteProductReadOnlyFieldsForEdit = Lists.newArrayList(GoodsReceivedNoteProductConstants.Field.GoodsReceivedNote.apiName,
                    GoodsReceivedNoteProductConstants.Field.Product.apiName,
                    GoodsReceivedNoteProductConstants.Field.GoodsReceivedAmount.apiName);

            //设置主对象字段只读
            ILayout layout = result.getLayout().toLayout();
            setReadOnly(layout, goodsReceivedNoteReadOnlyFieldsForEdit);

            //设置从对象字段只读
            result.getDetailObjectList().stream()
                    .filter(detailObjectListResult -> Objects.equals(detailObjectListResult.getObjectDescribe().get("api_name"), GoodsReceivedNoteProductConstants.API_NAME))
                    .forEach(detailObjectListResult -> {
                        List<RecordTypeLayoutStructure> layoutList = detailObjectListResult.getLayoutList();
                        if (com.facishare.paas.appframework.common.util.CollectionUtils.notEmpty(layoutList)) {
                            layoutList.stream().filter(layoutStructure -> layoutStructure.getRecord_type().equals(MultiRecordType.RECORD_TYPE_DEFAULT))
                                    .forEach(recordTypeLayoutStructure -> {
                                        ILayout detailLayout = LayoutDocument.of(recordTypeLayoutStructure.getDetail_layout()).toLayout();
                                        setReadOnly(detailLayout, goodsReceivedNoteProductReadOnlyFieldsForEdit);
                                    });
                        }
                    });

        } catch (Exception e) {
            log.error("入库单编辑页面设置字段只读异常。 arg[{}], result[{}]", arg, result, e);
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
