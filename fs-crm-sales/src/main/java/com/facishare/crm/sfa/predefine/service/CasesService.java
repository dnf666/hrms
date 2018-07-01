package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.sfa.predefine.service.model.HistoryCasesModel;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * Created by luxin on 2018/4/3.
 */
public interface CasesService {

    /**
     * 获取历史工单
     *
     * @param context
     * @param arg
     * @return
     */
    HistoryCasesModel.Result getHistoryCases(ServiceContext context, HistoryCasesModel.Arg arg);

}
