package com.facishare.crm.sfa.predefine.controller;

import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.sfa.predefine.exception.SFAErrorCode;
import com.facishare.crm.sfa.predefine.manager.SfaCustomerAccountManager;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.crm.sfa.utilities.util.VersionUtil;
import com.facishare.crm.stock.predefine.manager.StockManager;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.FormComponentExt;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.dto.DescribeDetailResult;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IFieldSection;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by lilei on 2017/11/9.
 */
@Component
@Slf4j
public class SalesOrderDescribeLayoutController extends SFADescribeLayoutController {


    private SfaCustomerAccountManager sfaCustomerAccountManager;
    private StockManager stockManager;
    private DeliveryNoteManager deliveryNoteManager;

    public SalesOrderDescribeLayoutController() {
        super();
        this.sfaCustomerAccountManager = SpringUtil.getContext().getBean(SfaCustomerAccountManager.class);
        this.stockManager = SpringUtil.getContext().getBean(StockManager.class);
        this.deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    }

    @Override
    protected void handleLayout(Arg arg, Result result) {
        super.handleLayout(arg, result);
        if (arg.getLayout_type() == null) {
            return;
        }
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                //特殊处理销售订单中的客户字段(仅支持订单)--LAYOUT_TYPE_EDIT的时候需要处理

                //主对象中需要设置为只读的字段名称
                List<String> readonlyFieldNames = new ArrayList<String>();
                readonlyFieldNames.add("account_id");
                //因为拿不到订单状态，所以禁用数量字段的逻辑放到终端和web端去做了
                //发货单启用，设置数量字段只读
                //if(IsDeliveryNoteEnabled()){
                //readonlyFieldNames.add("quantity");
                //}

                PreDefLayoutUtil.setFormComponentFieldsReadOnly(formComponent, readonlyFieldNames);
                removeSettleTypeOptionsForEditSalesOrder(result.getObjectDescribe().toObjectDescribe());

                //因为拿不到订单状态，所以禁用数量字段的逻辑放到终端和web端去做了
                //detail对象中需要设置为只读的字段名称
                //List<String> detailLayoutReadonlyFieldNames = new ArrayList<String>();
                //if(IsStockEnabled()){
                //detailLayoutReadonlyFieldNames.add("quantity");
                //}
                //setFieldsInDetailLayoutReadonly(result,detailLayoutReadonlyFieldNames);

                break;
        }
    }

    @Override
    protected void promptUpgrade(Arg arg, Result result) {
        super.promptUpgrade(arg, result);
        //低于6.2版本且开启发货单或者库存的终端编辑销售订单时提示升级
        if (VersionUtil.isVersionEarlierEqualThan620(controllerContext.getRequestContext()) &&
                arg.getLayout_type() != null && arg.getLayout_type().equals(LAYOUT_TYPE_EDIT) &&
                (IsDeliveryNoteEnabled() || IsStockEnabled())) {
            throw new MetaDataBusinessException(SFAErrorCode.CLIENT_UPGRADE_PROMPT.getMessage());
        }
    }

    /**
     * 发货单是否启用
     *
     * @return
     */
    private boolean IsDeliveryNoteEnabled() {
        return deliveryNoteManager.isDeliveryNoteEnable(getControllerContext().getRequestContext().getTenantId());
    }

    /**
     * 库存是否启用
     *
     * @return
     */
    private boolean IsStockEnabled() {

        return stockManager.isStockEnable(getControllerContext().getRequestContext().getTenantId());
    }


    private void removeSettleTypeOptionsForEditSalesOrder(IObjectDescribe describe) {
        IFieldDescribe fieldDescribe = describe.getFieldDescribe("settle_type");

        if (fieldDescribe == null) {
            return;
        }
        ArrayList options = fieldDescribe.get("options", ArrayList.class);
        ArrayList result = new ArrayList();
        for (Object option : options) {
            String v = ((JSONObject) option).get("value").toString();
            if (!v.equals("1") && !v.equals("3")) {
                result.add(option);
            }
        }
        fieldDescribe.set("options", result);
    }

    private void setFieldsInDetailLayoutReadonly(Result result, List<String> readonlyFields) {

        if (readonlyFields == null || readonlyFields.size() <= 0) {
            return;
        }

        Map<String, Object> map = result.getDetailObjectList().get(0).getLayoutList().get(0).getDetail_layout();
        List<Map> components = (List<Map>) map.get("components");
        components.stream().forEach(component -> {
            List<Map> fieldSections = (List<Map>) component.get("field_section");
            Map<String, Object> fieldSection = fieldSections.get(0);
            List<Map> fields = (List<Map>) fieldSection.get("form_fields");
            fields.stream().filter(field -> readonlyFields.contains(field.get("field_name").toString())).forEach(field -> field.put("is_readonly", true));
        });
    }

    @Override
    protected DescribeDetailResult findDescribeDetailResult() {
        DescribeDetailResult result = super.findDescribeDetailResult();
        ILayout layout = new Layout(result.getLayout());
        try {
            //if the customer account is not enabled, remove the settle_type field from layout.
            boolean isCustomerAccountEnabled = sfaCustomerAccountManager.isCustomerAccountEnable(getControllerContext().getRequestContext());
            if (!isCustomerAccountEnabled) {
                handleSettleType(layout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void handleSettleType(ILayout layout) {
        Optional<FormComponentExt> formComponent = LayoutExt.of(layout).getFormComponent();
        if (!formComponent.isPresent()) {
            return;
        }

        List<IFieldSection> fieldSectionsResult = Lists.newArrayList();
        for (IFieldSection fieldSection : formComponent.get().getFieldSections()) {
            List<IFormField> fields = fieldSection.getFields();
            for (IFormField formField : fields) {
                if ("settle_type".equals(formField.getFieldName())) {
                    fields.remove(formField);
                    break;
                }
            }
            fieldSection.setFields(fields);
            fieldSectionsResult.add(fieldSection);
        }
        formComponent.get().setFieldSections(fieldSectionsResult);
    }
}
