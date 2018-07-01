package com.facishare.crm.erpstock.predefine.service;

import com.facishare.crm.erpstock.predefine.service.model.base.*;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

/**
 * openApi 只支持内部调用，前端不支持
 * @author liangk
 * @date 09/05/2018
 */
@ServiceModule("ErpWarehouseObj")
public interface ErpWarehouseService {

    /**
     * 新建仓库
     */
    @ServiceMethod("save")
    BaseSaveModel.Result save(ServiceContext serviceContext, BaseSaveModel.Arg arg);


    /**
     * 更新仓库
     */
    @ServiceMethod("update")
    BaseSaveModel.Result update(ServiceContext serviceContext, BaseSaveModel.Arg arg);

    /**
     * 作废仓库
     */
    @ServiceMethod("invalid")
    BaseInvalidModel.Result invalid(ServiceContext serviceContext, BaseInvalidModel.Arg arg);

    /**
     * 查询仓库
     */
    @ServiceMethod("query")
    BaseQueryModel.Result query(ServiceContext serviceContext, BaseQueryModel.Arg arg);

    /**
     * 删除仓库
     */
    @ServiceMethod("delete")
    BaseDeleteModel.Result delete(ServiceContext serviceContext, BaseDeleteModel.Arg arg);

    /**
     * 恢复仓库
     */
    @ServiceMethod("recover")
    BaseRecoverModel.Result recover(ServiceContext serviceContext, BaseRecoverModel.Arg arg);
}
