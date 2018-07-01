package com.facishare.crm.sfa.predefine.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.facishare.crm.sfa.predefine.exception.SFABusinessException;
import com.facishare.crm.sfa.predefine.exception.SFAErrorCode;
import com.facishare.crm.sfa.utilities.constant.PartnerConstants;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.crm.sfa.utilities.util.VersionUtil;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.DBRecord;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.impl.ui.layout.component.FormComponent;
import com.facishare.paas.metadata.ui.layout.ILayout;

import lombok.extern.slf4j.Slf4j;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_ADD;
import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by zhaopx on 2017/11/14.
 */
@Slf4j
public class SFADescribeLayoutController extends StandardDescribeLayoutController {

    protected FormComponent formComponent;

    @Override
    protected Result after(Arg arg, Result result) {

        promptUpgrade(arg, result);

        handelDescribe(arg, result);

        handleLayout(arg, result);

        return super.after(arg, result);

    }

    protected void promptUpgrade(Arg arg, Result result) {
        if (VersionUtil.isVersionEarlierEqualThan610(controllerContext.getRequestContext())) {
            Boolean haveCustomRefField = ObjectDescribeExt.of(result.getObjectDescribe().toObjectDescribe())
                    .getFieldDescribesSilently().stream()
                    .anyMatch(it -> it.getType().equals(IFieldType.OBJECT_REFERENCE)
                            && it.getDefineType().equals(IFieldDescribe.DEFINE_TYPE_CUSTOM));
            if (haveCustomRefField) {
                log.info("CLIENT_UPGRADE_PROMPT clientInfo:{}", controllerContext.getRequestContext().getClientInfo());
                throw new SFABusinessException(SFAErrorCode.CLIENT_UPGRADE_PROMPT.getMessage(),
                        SFAErrorCode.CLIENT_UPGRADE_PROMPT);
            }
        }
    }

    protected void handelDescribe(Arg arg, Result result) {
    }

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
        LayoutExt layoutExt = LayoutExt.of(layout);
        formComponent = (FormComponent) layoutExt.getFormComponent().get().getFormComponent();
        switch (arg.getLayout_type()) {
            case LAYOUT_TYPE_EDIT:
                PreDefLayoutUtil.specialDealOldObjRequiredReadOnly(formComponent, describe);
                PreDefLayoutUtil.specialDealAccountObjAccountName(describe.getApiName(), this.serviceFacade, formComponent,
                        user.getTenantId());
                PreDefLayoutUtil.removeAutoNumberOfPreDefineObj(formComponent, describe);
                PreDefLayoutUtil.removeSpecialFieldNameFromFormComponent(formComponent, describe,
                        LAYOUT_TYPE_EDIT);
                PreDefLayoutUtil.removeSpecialFieldsFromDetailObjectList(result.getDetailObjectList(),
                        LAYOUT_TYPE_EDIT);
                //合作伙伴和外部负责人编辑布局中只读
                PreDefLayoutUtil.setFormComponentFieldReadOnly(formComponent, Lists.newArrayList(PartnerConstants.FIELD_PARTNER_ID));
                //移除外部来源、外部企业字段
                PreDefLayoutUtil.removeSomeFields(formComponent, Sets.newHashSet(PartnerConstants.FIELD_OUT_RESOURCES, DBRecord.OUT_TENANT_ID, DBRecord.OUT_OWNER));
                break;
            case LAYOUT_TYPE_ADD:
                PreDefLayoutUtil.specialDealOldObjRequiredReadOnly(formComponent, describe);
                PreDefLayoutUtil.removeAutoNumberOfPreDefineObj(formComponent, describe);
                PreDefLayoutUtil.removeSpecialFieldNameFromFormComponent(formComponent, describe,
                        LAYOUT_TYPE_ADD);
                PreDefLayoutUtil.removeSpecialFieldsFromDetailObjectList(result.getDetailObjectList(),
                        LAYOUT_TYPE_ADD);
                //移除外部来源、外部企业字段
                PreDefLayoutUtil.removeSomeFields(formComponent, Sets.newHashSet(PartnerConstants.FIELD_OUT_RESOURCES, DBRecord.OUT_TENANT_ID, DBRecord.OUT_OWNER));
                break;
            default:
                break;
        }

        //result.setLayout(LayoutDocument.of(layout));
    }
}
