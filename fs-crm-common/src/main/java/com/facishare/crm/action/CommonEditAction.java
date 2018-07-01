package com.facishare.crm.action;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.core.predef.action.StandardEditAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by linchf on 2018/1/25.
 */
@Slf4j
public class CommonEditAction extends StandardEditAction {
    protected List<IObjectData> resultDataList = Lists.newArrayList();

    @Override
    protected void bulkUpdateObjectDataListInApproval(List<IObjectData> objectDataList, List<String> fieldsProjection) {
        if (!fieldsProjection.isEmpty()) {
            resultDataList = this.serviceFacade.parallelBulkUpdateObjectData(this.actionContext.getUser(), objectDataList, true, fieldsProjection).getSuccessObjectDataList();
        }
    }

    @Override
    protected BaseObjectSaveAction.Result after(BaseObjectSaveAction.Arg arg, BaseObjectSaveAction.Result result) {
        result = super.after(arg, result);

        IObjectData objectData = result.getObjectData().toObjectData();
        objectData = serviceFacade.findObjectData(this.actionContext.getUser(), objectData.getId(), objectData.getDescribeApiName());
        result.setObjectData(ObjectDataDocument.of(objectData));
        return result;
    }
}
