package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.utilities.common.convert.ObjectDataFieldConvert;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.model.LayoutDocument;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.core.predef.controller.StandardDetailController;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ui.layout.Button;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.impl.ui.layout.component.GroupComponent;
import com.facishare.paas.metadata.ui.layout.IButton;
import com.facishare.paas.metadata.ui.layout.IComponent;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.crm.sfa.utilities.constant.PriceBookConstants.Field.ACTIVESTATUS;
import static com.facishare.paas.appframework.core.model.RequestContext.Android_CLIENT_INFO_PREFIX;
import static com.facishare.paas.appframework.core.model.RequestContext.CLIENT_INFO;
import static com.facishare.paas.appframework.core.model.RequestContext.IOS_CLIENT_INFO_PREFIX;

@Slf4j
public class PriceBookDetailController extends StandardDetailController {

    @Override
    public Result doService(Arg arg) {
        return super.doService(arg);
    }

    @Override
    public void before(Arg arg) {
        super.before(arg);
    }

    @Override
    public Result after(Arg arg, Result result) {
        if (result.getData() != null) {
            packData(result.getData());
            specialLogicForStandardPriceBookProduct(result);
        }
        return super.after(arg, result);
    }

    @Override
    protected ILayout getLayout() {
        ILayout layout = super.getLayout();
        specialLogicForLayout(layout);
        return layout;
    }

    private void specialLogicForLayout(ILayout layout) {
        List<IButton> buttons = layout.getButtons();
        if (data.get("is_standard") != null && (Boolean) data.get("is_standard")) {
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.INVALID.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.CHANGE_OWNER.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.LOCK.getActionCode()));
        }
        String clientInfo = getControllerContext().getRequestContext().getAttribute(CLIENT_INFO);
        if (clientInfo.startsWith(Android_CLIENT_INFO_PREFIX) || clientInfo.startsWith(IOS_CLIENT_INFO_PREFIX)) {
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.CREATE.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.UPDATE.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.DELETE.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.PRINT.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.START_BPM.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.INTELLIGENTFORM.getActionCode()));
            buttons.removeIf(k -> k.getAction().equals(ObjectAction.CLONE.getActionCode()));
        }
        layout.setButtons(buttons);

        //根据配置隐藏关联对象列表的新建按钮
        PreDefLayoutUtil.invisibleRefObjectListAddButton(arg.getObjectDescribeApiName(),layout);
    }


    private void specialLogicForStandardPriceBookProduct(Result result) {
        try {
            ObjectDataDocument objectDataDocument = result.getData();
            Boolean is_standard = (Boolean) objectDataDocument.get("is_standard");
            LayoutDocument layoutDocument = result.getLayout();
            ILayout layout = layoutDocument.toLayout();
            List<IComponent> groupComponentList = layout.getComponents();
            for (IComponent component : groupComponentList) {
                if (is_standard && "detailInfo".equals(component.getName())) {
                    GroupComponent groupComponent = (GroupComponent) component;
                    FormComponent formComponent = (FormComponent) groupComponent.getChildComponents().get(0);
                    List<IFieldSection> fieldSections = formComponent.getFieldSections();
                    for (IFieldSection fieldSection : fieldSections) {
                        List<IFormField> fields = fieldSection.getFields();
                        for (IFormField field : fields) {
                            if (!ACTIVESTATUS.getApiName().equals(field.getFieldName())) {
                                field.setReadOnly(true);
                            }
                        }
                    }
                }
                try {
                    if ("PriceBookProductObj_md_group_component".equals(component.getName())){
                        ArrayList ls = (ArrayList) ((GroupComponent) component).getContainerDocument().get("child_components");
                        Map doc = (Map) ls.get(0);
                        //标准价目表要隐藏web端的价目表产品的操作按钮
                        doc.put("buttons", generateButtons(is_standard));
                        doc.get("child_components");
                        ArrayList lsIneer = (ArrayList) doc.get("child_components");
                        //针对终端的特殊处理，移除所有的价目表产品的操作按钮。
                        Map docInner = (Map) lsIneer.get(0);
                        docInner.put("buttons", Lists.newArrayList());
                    }
                    if ("relatedObject".equals(component.getName())) {
                        //价目表下要把关联的销售订单页签的所有button都去掉。
                        ArrayList<Map> childComponents = (ArrayList) ((GroupComponent) component).getContainerDocument().get("child_components");
                        for (Map entry : childComponents) {
                            if ("SalesOrderObj".equals(entry.get("ref_object_api_name"))) {
                                ArrayList btnList = (ArrayList) entry.get("buttons");
                                btnList.clear();
                            }
                        }

                    }
                } catch (Exception ex) {
                    log.error("PriceBookObj objdetail error:", ex);
                }
            }
        } catch (MetadataServiceException e) {
            log.error("component exception:", e);
        }
    }

    //TODO  需要增加权限判断
    private List<Map> generateButtons(Boolean is_standard) {
        if (UdobjConstants.LOCK_STATUS_VALUE_LOCK.equals(ObjectDataExt.of(data).getLockStatus())) {
            return Lists.newArrayList();
        }
        String functionAdd = StandardAction.Add.getFunPrivilegeCodes().get(0);
        String functionEdit = StandardAction.Edit.getFunPrivilegeCodes().get(0);
        String functionDelete = StandardAction.Delete.getFunPrivilegeCodes().get(0);
        List<String> functionCodeList = Arrays.asList(functionAdd, functionEdit, functionDelete);
        Map<String, Boolean> functionMap = serviceFacade.funPrivilegeCheck(this.getControllerContext().getUser(), PriceBookConstants.API_NAME_PRODUCT, functionCodeList);

        List<Map> buttonList = Lists.newArrayList();
        if (functionMap.get(functionEdit)) {
            Button buttonEdit = new Button();
            buttonEdit.setAction("Edit");
            buttonEdit.setActionType("custom");
            buttonEdit.setLabel("编辑");
            buttonEdit.setName("PriceBookProductObj_button_custom__c");
            buttonList.add(buttonEdit.getContainerDocument());
        }
        if (!is_standard&&functionMap.get(functionDelete)) {
            Button buttonInvalid = new Button();
            buttonInvalid.setAction("Delete");
            buttonInvalid.setActionType("custom");
            buttonInvalid.setLabel("删除");
            buttonInvalid.setName("PriceBookProductObj_button_custom__c");
            buttonList.add(buttonInvalid.getContainerDocument());
        }
        if (functionMap.get(functionAdd)) {
            Button buttonAdd = new Button();
            buttonAdd.setAction("AddProduct");
            buttonAdd.setActionType("custom");
            buttonAdd.setLabel("添加产品");
            buttonAdd.setName("PriceBookProductObj_button_custom__c");
            buttonList.add(buttonAdd.getContainerDocument());
        }
        return buttonList;
    }

    private void packData(ObjectDataDocument dataDocument) {
        String tenantId = this.controllerContext.getTenantId();
        Object accountRange = dataDocument.get("account_range");
        if (accountRange != null && StringUtils.isNotBlank(accountRange.toString())) {
            JSONObject accountRangeObj = null;
            if (accountRange instanceof Map) {
                accountRangeObj = new JSONObject((Map) accountRange);
            } else if (accountRange instanceof String) {
                accountRangeObj = JSON.parseObject(accountRange.toString());
            } else {
                log.error("fillFieldUserScope error,accountRange type instanceof error");
                return;
            }
            if (accountRangeObj != null && !"noCondition".equals(accountRangeObj.get("type").toString())) {
                IObjectDescribe describe = this.serviceFacade.findObject(tenantId, Utils.ACCOUNT_API_NAME);
                packAccountRange(describe, accountRangeObj.getJSONObject("value"));
                dataDocument.put("account_range", accountRangeObj.toJSONString());
            }
        }
    }

    private void packAccountRange(IObjectDescribe describe, JSONObject rangeObj) {
        JSONArray conditionArr = rangeObj.getJSONArray("conditions");
        for (Object o : conditionArr) {
            JSONObject obj = (JSONObject) o;
            if (obj.containsKey("conditions")) {
                packAccountRange(describe, obj);
            } else {
                JSONObject left = obj.getJSONObject("left");
                String fieldName = left.getString("expression");
                JSONObject right = obj.getJSONObject("right");
                Object fieldValue = right.get("value");
                right.put("label", "--");
                left.put("label", "--");
                IFieldDescribe fieldDescribe = describe.getFieldDescribe(fieldName);
                if (fieldDescribe == null || !fieldDescribe.isActive()) {
                    continue;
                }
                //左侧label处理
                left.put("label", fieldDescribe.getLabel());
                if (fieldValue == null || StringUtils.isBlank(fieldValue.toString())) {
                    continue;
                }
                //右侧label处理
                Object finalVal = ObjectDataFieldConvert.me().transformDataField(this.getControllerContext().getUser(), fieldValue, fieldDescribe);
                if (finalVal != null && StringUtils.isNotBlank(finalVal.toString())) {
                    right.put("label", finalVal);
                }
            }
        }
    }


}
