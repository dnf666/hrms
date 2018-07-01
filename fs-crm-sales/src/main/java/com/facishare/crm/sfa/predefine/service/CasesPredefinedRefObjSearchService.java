package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.sfa.predefine.service.model.CasesPredefinedRefObjSearchModel;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * Created by luxin on 2018/3/27.
 */
public interface CasesPredefinedRefObjSearchService {

    /**
     * 搜索客户信息
     *
     * @param context 企业及用户上下文信息
     * @param arg
     * @return 客户信息
     */
    CasesPredefinedRefObjSearchModel.Result search(ServiceContext context, CasesPredefinedRefObjSearchModel.Arg arg);


}
