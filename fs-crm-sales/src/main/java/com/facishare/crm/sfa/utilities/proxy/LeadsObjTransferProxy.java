package com.facishare.crm.sfa.utilities.proxy;

import com.facishare.crm.sfa.utilities.proxy.model.LeadsObjTransferResult;
import com.facishare.rest.proxy.annotation.*;
import java.util.Map;

/**
 * Created by yuanjl on 2018/04/24.
 */
@RestResource(value = "CRM_SFA", desc = "CRM Rest API Call", contentType = "application/json")
public interface LeadsObjTransferProxy {

    @POST(value = "/crm/salesclue/transfer", desc = "线索一转三接口")
    LeadsObjTransferResult.Result transfer(@Body LeadsObjTransferResult.Arg arg, @HeaderMap Map<String, String> headers);
}
