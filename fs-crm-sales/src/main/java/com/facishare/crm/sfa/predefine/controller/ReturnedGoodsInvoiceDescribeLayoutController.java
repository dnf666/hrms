package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by luxin on 2018/1/2.
 */
@Component
public class ReturnedGoodsInvoiceDescribeLayoutController extends SFADescribeLayoutController {

    @Override
    protected void handleLayout(Arg arg, Result result) {
        if (arg.getLayout_type() == null) {
            return;
        }

        User user = getControllerContext().getUser();
        ILayout layout = new Layout(result.getLayout());

        try {
            if (CollectionUtils.empty(layout.getComponents())) {
                return;
            }
        } catch (MetadataServiceException e) {
            throw new MetaDataBusinessException(e.getMessage());
        }
        IObjectDescribe describe = result.getObjectDescribe().toObjectDescribe();

        formComponent = (FormComponent) LayoutExt.of(layout).getFormComponent().get().getFormComponent();
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                PreDefLayoutUtil.specialDealAccountObjAccountName(describe.getApiName(), this.serviceFacade, formComponent,
                        user.getTenantId());
                PreDefLayoutUtil.removeAutoNumberOfPreDefineObj(formComponent, describe);
                PreDefLayoutUtil.removeSpecialFieldNameFromFormComponent(formComponent, describe,
                        LAYOUT_TYPE_EDIT);
                PreDefLayoutUtil.removeSpecialFieldsFromDetailObjectList(result.getDetailObjectList(),
                        LAYOUT_TYPE_EDIT);

                //编辑的时候,客户是readOnly的
                if (LayoutExt.of(layout).getField("account_id").get() != null) {
                    LayoutExt.of(layout).getField("account_id").get().setReadOnly(true);
                }
                //编辑的时候,订单编号是readOnly的
                if (LayoutExt.of(layout).getField("account_id").get() != null) {
                    LayoutExt.of(layout).getField("order_id").get().setReadOnly(true);
                }
                break;
            case LAYOUT_TYPE_ADD:
                PreDefLayoutUtil.specialDealOldObjRequiredReadOnly(formComponent, describe);
                PreDefLayoutUtil.removeAutoNumberOfPreDefineObj(formComponent, describe);
                PreDefLayoutUtil.removeSpecialFieldNameFromFormComponent(formComponent, describe,
                        LAYOUT_TYPE_ADD);
                PreDefLayoutUtil.removeSpecialFieldsFromDetailObjectList(result.getDetailObjectList(),
                        LAYOUT_TYPE_ADD);
                break;
            default:
                break;
        }
        //result.setLayout(LayoutDocument.of(layout));
    }

}
