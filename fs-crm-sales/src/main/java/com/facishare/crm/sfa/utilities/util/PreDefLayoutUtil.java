package com.facishare.crm.sfa.utilities.util;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDescribeDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.FormComponentExt;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.dto.DetailObjectListResult;
import com.facishare.paas.appframework.metadata.dto.RecordTypeLayoutStructure;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.RelatedObjectList;
import com.facishare.paas.metadata.ui.layout.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import static com.facishare.crm.userdefobj.DefObjConstants.*;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by luohl on 2017/11/14.
 */
@Slf4j
public class PreDefLayoutUtil {

    public static void setFormComponentFieldReadOnly(FormComponent formComponent, List<String> fieldNames) {
        setFormComponentFieldsReadOnly(formComponent, fieldNames);
    }

    public static void setFormComponentFieldsReadOnly(FormComponent formComponent, List<String> fieldNames) {
        List<IFieldSection> fieldSectionsResult = Lists.newArrayList();
        for (IFieldSection fieldSection : formComponent.getFieldSections()) {
            List<IFormField> fields = fieldSection.getFields();
            for (IFormField formField : fields) {
                if (fieldNames.indexOf(formField.getFieldName()) >= 0) {
                    formField.setReadOnly(Boolean.TRUE);
                }
            }
            fieldSection.setFields(fields);
            fieldSectionsResult.add(fieldSection);
        }
        formComponent.setFieldSections(fieldSectionsResult);
    }

    //特殊处理老对象,是必填的,那么就强制改成非只读的,但是  销售订单的客户订单除外。
    public static void specialDealOldObjRequiredReadOnly(FormComponent formComponent, IObjectDescribe describe) {

        try {
            //是必填的,那么就强制改成非只读的,但是  销售订单的客户订单除外。
            List<IFieldSection> fieldSectionsResult = Lists.newArrayList();
            for (IFieldSection fieldSection : formComponent.getFieldSections()) {
                List<IFormField> fields = fieldSection.getFields();
                for (IFormField formField : fields) {
                    String fieldName = formField.getFieldName();
                    if (Objects.equals(describe.getFieldDescribe(fieldName).isRequired(), Boolean.TRUE) && !(Utils.SALES_ORDER_API_NAME.equals(describe.getApiName()) && "account_id".equals(fieldName))) {
                        formField.setReadOnly(false);
                    }
                }
                fieldSection.setFields(fields);
                fieldSectionsResult.add(fieldSection);
            }
            formComponent.setFieldSections(fieldSectionsResult);
        } catch (Exception e) {
            log.error("error in specialDealOldObjRequiredReadOnly", e);
        }
    }

    //特殊处理客户名称,读取配置,根据配置决定客户名称属性在编辑中是否是只读的。
    public static void specialDealAccountObjAccountName(String apiname, ServiceFacade serviceFacade, FormComponent formComponent, String tenantId) {
        if (!apiname.equals(SFAPreDefineObject.Account.getApiName()))
            return;
        if (serviceFacade.canUpdateAccountName(tenantId))
            return;
        //如果配置中是不可以修改的,那么就将name强制设定为true
        List<IFieldSection> fieldSectionsResult = Lists.newArrayList();
        for (IFieldSection fieldSection : formComponent.getFieldSections()) {
            List<IFormField> fields = fieldSection.getFields();
            for (IFormField formField : fields) {
                if ("name".equals(formField.getFieldName())) {
                    formField.setReadOnly(Boolean.TRUE);
                }
            }
            fieldSection.setFields(fields);
            fieldSectionsResult.add(fieldSection);
        }
        formComponent.setFieldSections(fieldSectionsResult);
    }

    public static void removeAutoNumberOfPreDefineObj(FormComponent formComponent, IObjectDescribe describe) {
        //某些老对象的text类型有属性is_auto_number， 为true时是自动编号，此时不下发该field
        List<IFieldDescribe> fieldDescribes = describe.getFieldDescribes();

        if (CollectionUtils.isEmpty(fieldDescribes)) {
            return;
        }

        Set<String> set = Sets.newHashSet();
        for (IFieldDescribe field : fieldDescribes) {
            if (!Objects.equals(field.getType(), IFieldType.TEXT)) {
                continue;
            }

            Boolean isAutoNumber = field.get("is_auto_number", Boolean.class);
            if (Objects.equals(isAutoNumber, Boolean.TRUE)) {
                set.add(field.getApiName());
            }
        }

        if (CollectionUtils.isEmpty(set)) {
            return;
        }

        FormComponentExt.of(formComponent).removeFields(set);
    }

    public static void removeSpecialFieldsFromDetailObjectList(List<DetailObjectListResult> detailObjectList, String layoutType) {
        if (detailObjectList == null) {
            return;
        }
        for (DetailObjectListResult detailObject : detailObjectList) {
            IObjectDescribe objectDescribe = ObjectDescribeDocument.of(detailObject.getObjectDescribe())
                    .toObjectDescribe();
            List<RecordTypeLayoutStructure> layoutList = detailObject.getLayoutList();
            for (RecordTypeLayoutStructure layout : layoutList) {
                FormComponent formComponent = (FormComponent) LayoutExt.of(
                        LayoutDocument.of(layout.getDetail_layout()).toLayout()
                ).getFormComponent().get().getFormComponent();
                removeSpecialFieldNameFromFormComponent(formComponent, objectDescribe, layoutType);
            }
        }
    }

    public static void removeSpecialFieldNameFromFormComponent(FormComponent formComponent, IObjectDescribe describe, String layoutType) {
        Set<String> specialFieldNameToRemove;
        if (layoutType.equals(LAYOUT_TYPE_EDIT)) {
            specialFieldNameToRemove = invisibleFieldNameListForEditLayout.get(describe.getApiName());
        } else {
            specialFieldNameToRemove = invisibleFieldNameListForAddLayout.get(describe.getApiName());
        }

        removeSomeFields(formComponent, specialFieldNameToRemove);
    }

    public static void removeSomeFields(FormComponent formComponent, Set<String> invisibleFieldNameList) {
        if (invisibleFieldNameList == null || invisibleFieldNameList.size() == 0)
            return;
        List<IFieldSection> fieldSectionsResult = Lists.newArrayList();
        for (IFieldSection fieldSection : formComponent.getFieldSections()) {
            List<IFormField> fields = fieldSection.getFields();
            for (IFormField formField : fieldSection.getFields()) {
                if (invisibleFieldNameList.contains(formField.getFieldName())) {
                    fields.remove(formField);
                }
            }
            fieldSection.setFields(fields);
            fieldSectionsResult.add(fieldSection);
        }
        formComponent.setFieldSections(fieldSectionsResult);
    }

    public static RelatedObjectList getEmailComponent() {
        RelatedObjectList component = new RelatedObjectList();
        component.setName("CRMEmail_related_list_generate_by_UDObjectServer__c");
        component.setHeader("邮件");
        component.setRefObjectApiName("CRMEmail");
        component.setRelatedListName("");

        return component;
    }

    private static void removeRelatedComponentButtonsByActions(ILayout layout, List<String> invisibleRelatedObjects, List<String> actions) {
        LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
            try {
                x.getChildComponents().forEach(childComponent -> {
                    if (invisibleRelatedObjects.contains(childComponent.get("ref_object_api_name", String.class))) {
                        List<IButton> buttons = childComponent.getButtons();
                        buttons.removeIf(button -> actions.contains(button.getAction()));
                        childComponent.setButtons(buttons);
                    }
                });
            } catch (MetadataServiceException e) {
                log.error("getChildComponents error");
            }
        });
    }

    public static void invisibleRefObjectListAddButton(String describeApiName, ILayout layout) {
        List<String> invisibleRelatedObjects = invisibleAddButtonMap.get(describeApiName);
        if (!org.springframework.util.CollectionUtils.isEmpty(invisibleRelatedObjects)) {
            removeRelatedComponentButtonsByActions(layout, invisibleRelatedObjects
                    , Collections.singletonList("Add"));
        }
    }

    public static void invisibleRefObjectListRelationButton(String describeApiName, ILayout layout) {
        List<String> invisibleRelatedObjects = invisibleRelationButtonMap.get(describeApiName);
        if (!org.springframework.util.CollectionUtils.isEmpty(invisibleRelatedObjects)) {
            removeRelatedComponentButtonsByActions(layout, invisibleRelatedObjects
                    , Arrays.asList("BulkRelate", "BulkDisRelate"));
        }
    }

    public static void invisibleRefObjectListAllButtonForSpecifiedRelatedObject(ILayout layout) {
        List<String> relatedObjects = Collections.singletonList("SalesOrderProductObj");
        removeRelatedComponentButtonsByActions(layout, relatedObjects
                , Arrays.asList("Add", "BulkRelate", "BulkDisRelate"));
    }

    public static void invisibleReferenceObject(String describeApiName, ILayout layout) {
        List<String> invisibleReference = invisibleReferenceMap.get(describeApiName);
        if (!org.springframework.util.CollectionUtils.isEmpty(invisibleReference)) {
            LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
                try {
                    List<IComponent> childComponents = x.getChildComponents();
                    if("ContactObj".equals(describeApiName)){
                        childComponents.removeIf(childComponent ->
                                "introducer".equals(childComponent.get("field_api_name", String.class)));

                    }
                    childComponents.removeIf(childComponent ->
                            invisibleReference.contains(childComponent.get("ref_object_api_name", String.class)));
                    x.setChildComponents(childComponents);
                } catch (MetadataServiceException e) {
                    log.error("getChildComponents error");
                }
            });
        }
    }

    public static void invisibleReferenceObjectForMobile(String describeApiName, ILayout layout,Map<String, List<String>>  invisibleReferenceApiNamesForMobilemap) {
        List<String> invisibleReference = invisibleReferenceApiNamesForMobilemap.get(describeApiName);
        if (!org.springframework.util.CollectionUtils.isEmpty(invisibleReference)) {
            LayoutExt.of(layout).getRelatedComponent().ifPresent(x -> {
                try {
                    List<IComponent> childComponents = x.getChildComponents();
                    childComponents.removeIf(childComponent ->
                            invisibleReference.contains(childComponent.get("api_name", String.class))||
                            invisibleReference.contains(childComponent.get("ref_object_api_name", String.class)));
                    x.setChildComponents(childComponents);
                } catch (MetadataServiceException e) {
                    log.error("getChildComponents error");
                }
            });
        }
    }
}