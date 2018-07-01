package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.BuyRecordType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * 购买记录
 */
@ServiceModule("elec_sign_buy_record")
public interface BuyRecordService {
    /**
     * 为管理平台分页查询购买记录
     * @param serviceContext 参数
     * @param data 查询条件
     * @return 查询结果
     */
    @ServiceMethod("query_by_page_for_fs_manage")
    BuyRecordType.QueryByPage.Result queryByPageForFsManage(ServiceContext serviceContext, BuyRecordType.QueryByPage.Arg data);

    /**
     * 查租户配额
     */
    @ServiceMethod("query_by_page")
    BuyRecordType.QueryByPage.Result queryByPage(ServiceContext serviceContext, BuyRecordType.QueryByPage.Arg data);

}