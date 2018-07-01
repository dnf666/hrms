package com.facishare.crm.sfa.predefine.controller;

import com.facishare.paas.appframework.core.predef.controller.StandardDescribeLayoutController;
import com.facishare.paas.appframework.metadata.LayoutExt;
import com.facishare.paas.metadata.impl.ui.layout.Layout;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.facishare.paas.common.util.UdobjConstants.LAYOUT_TYPE_EDIT;

/**
 * Created by quzf on 2018/4/12.
 */
public class PartnerDescribeLayoutController extends StandardDescribeLayoutController {
    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        if (StringUtils.isNotBlank(arg.getLayout_type())) {
            ILayout layout = new Layout(result.getLayout());
            LayoutExt layoutExt = LayoutExt.of(layout);
            switch (arg.getLayout_type()) {
                case LAYOUT_TYPE_EDIT:
                    Optional<IFormField> nameField = layoutExt.getField("name");
                    if (nameField.isPresent()) {
                        nameField.get().setReadOnly(true);
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }
}
