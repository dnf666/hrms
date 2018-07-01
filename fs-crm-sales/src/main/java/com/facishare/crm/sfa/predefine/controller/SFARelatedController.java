package com.facishare.crm.sfa.predefine.controller;

import com.facishare.crm.sfa.predefine.exception.SFAErrorCode;
import com.facishare.crm.sfa.utilities.util.PreDefLayoutUtil;
import com.facishare.crm.sfa.utilities.util.VersionUtil;
import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.core.predef.controller.StandardRelatedController;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.exception.MetaDataBusinessException;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.ILayout;

public class SFARelatedController extends StandardRelatedController {

    @Override
    protected Result after(Arg arg, Result result) {
        Result newResult = super.after(arg, result);

        promptUpgrade(result);

        ILayout layout = new Layout(newResult.getLayout());
        PreDefLayoutUtil.invisibleRefObjectListAddButton(arg.getObjectDescribeApiName(),layout);
        PreDefLayoutUtil.invisibleRefObjectListRelationButton(arg.getObjectDescribeApiName(),layout);
        PreDefLayoutUtil.invisibleReferenceObject(arg.getObjectDescribeApiName(),layout);
        PreDefLayoutUtil.invisibleRefObjectListAllButtonForSpecifiedRelatedObject(layout);
        return newResult;
    }

    private void promptUpgrade(Result result){
        if(VersionUtil.isVersionEarlierEqualThan610(controllerContext.getRequestContext())){
            Boolean haveCustomRefField = ObjectDescribeExt.of(result.getDescribe().toObjectDescribe())
                    .getFieldDescribesSilently().stream()
                    .anyMatch(
                            it->it.getType().equals(IFieldType.OBJECT_REFERENCE)
                                    && it.getDefineType().equals(IFieldDescribe.DEFINE_TYPE_CUSTOM));
            if(haveCustomRefField){
                throw new MetaDataBusinessException(SFAErrorCode.CLIENT_UPGRADE_PROMPT.getMessage());
            }
        }
    }
}
