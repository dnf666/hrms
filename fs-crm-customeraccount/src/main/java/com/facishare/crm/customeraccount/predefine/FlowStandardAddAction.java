package com.facishare.crm.customeraccount.predefine;

import java.util.List;

import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardAddAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.rest.proxy.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlowStandardAddAction extends StandardAddAction {
    private List<IObjectData> resultObjectList;

    @Override
    protected void before(Arg arg) {
        super.before(arg);
    }

    @Override
    protected void bulkUpdateObjectDataListInApproval(List<IObjectData> objectDataList, List<String> fieldsProjection) {
        if (!fieldsProjection.isEmpty()) {
            resultObjectList = serviceFacade.parallelBulkUpdateObjectData(actionContext.getUser(), objectDataList, true, fieldsProjection).getSuccessObjectDataList();
        }
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);
        log.debug("FlowStandardAddAction result:{},resultObjectList:{}", result, JsonUtil.toJson(resultObjectList));
        if (resultObjectList != null) {
            String id = (String) result.getObjectData().get(IObjectData.ID);
            for (IObjectData data : resultObjectList) {
                if (data.getId().equals(id)) {
                    result.setObjectData(ObjectDataDocument.of(data));
                    break;
                }
            }
        }
        return result;
    }

}
