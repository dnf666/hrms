package com.facishare.crm.erpstock.predefine.service.impl;

import com.facishare.common.proxy.helper.StringUtils;
import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.crm.erpstock.predefine.manager.ErpStockApiManager;
import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.crm.erpstock.predefine.service.ErpStockService;
import com.facishare.crm.erpstock.predefine.service.model.ErpStockInvalidOrQueryModel;
import com.facishare.crm.erpstock.predefine.service.model.IsErpStockEnableModel;
import com.facishare.crm.erpstock.predefine.service.model.base.BaseInvalidModel;
import com.facishare.crm.erpstock.predefine.service.model.base.BaseModel;
import com.facishare.crm.erpstock.predefine.service.model.base.BaseSaveModel;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author linchf
 * @date 2018/5/8
 */
@Slf4j(topic = "erpStockAccess")
@Component
public class ErpStockServiceImpl implements ErpStockService {
    @Resource
    private ErpStockManager erpStockManager;

    @Resource
    private ErpStockApiManager erpStockApiManager;

    @Override
    public BaseSaveModel.Result save(ServiceContext serviceContext, BaseSaveModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());

        checkCreateOrUpdateArg(arg);
        BaseSaveModel.Result result = new BaseSaveModel.Result();
        IObjectData objectData = arg.getObjectData().toObjectData();
        IObjectData existStock = queryBySaveOrUpdateArg(serviceContext.getUser(), arg, false);
        if (existStock != null) {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "库存记录已存在");
        } else {
            result.setObjectData(erpStockApiManager.save(serviceContext.getUser(), objectData));
        }
        return result;
    }

    @Override
    public BaseSaveModel.Result update(ServiceContext serviceContext, BaseSaveModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        checkCreateOrUpdateArg(arg);
        BaseSaveModel.Result result = new BaseSaveModel.Result();
        IObjectData objectData = arg.getObjectData().toObjectData();
        IObjectData existStock = queryBySaveOrUpdateArg(serviceContext.getUser(), arg, false);
        if (existStock != null) {
            result.setObjectData(erpStockApiManager.update(serviceContext.getUser(), existStock, objectData));
        } else {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "库存记录不存在");
        }

        return result;
    }

    @Override
    public BaseInvalidModel.Result invalid(ServiceContext serviceContext, ErpStockInvalidOrQueryModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        checkInvalidOrQueryArg(arg);
        BaseInvalidModel.Result result = new BaseModel.Result();

        IObjectData existStock = queryByInvalidOrQueryArg(serviceContext.getUser(), arg, false);
        if (existStock == null) {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "库存对象不存在");
        }
        result.setObjectData(erpStockApiManager.invalid(serviceContext.getUser(), existStock));
        return result;
    }

    @Override
    public BaseInvalidModel.Result query(ServiceContext serviceContext, ErpStockInvalidOrQueryModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        checkInvalidOrQueryArg(arg);
        BaseInvalidModel.Result result = new BaseModel.Result();
        IObjectData existStock = queryByInvalidOrQueryArg(serviceContext.getUser(), arg, true);
        result.setObjectData(ObjectDataDocument.of(existStock));
        return result;
    }

    @Override
    public IsErpStockEnableModel.Result isErpStockEnable(ServiceContext serviceContext) {
        IsErpStockEnableModel.Result result = new IsErpStockEnableModel.Result();
        result.setIsEnable(erpStockManager.isStockEnable(serviceContext.getUser()));
        return result;
    }

    private IObjectData queryBySaveOrUpdateArg(User user, BaseModel.Arg arg, Boolean includeDeleted) {
        IObjectData existStock = null;
        //通过id
        if (!StringUtils.isBlank(arg.getObjectDataId())) {
            existStock = erpStockApiManager.query(user, arg.getObjectDataId(), includeDeleted);
        }

        //通过id
        if (existStock == null) {
            if (arg.getObjectData() == null) {
                throw new ValidateException("ERP库存对象不能为空");
            }

            IObjectData objectData = arg.getObjectData().toObjectData();
            if (objectData.getId() != null && !StringUtils.isBlank(objectData.getId())) {
                existStock = erpStockApiManager.query(user, objectData.getId(), includeDeleted);
            }

            //通过产品id和仓库id
            if (existStock == null) {
                String productId = objectData.get(ErpStockConstants.Field.Product.apiName, String.class);
                String erpWarehouseId = objectData.get(ErpStockConstants.Field.ErpWarehouse.apiName, String.class);
                existStock = erpStockManager.queryByProductIdAndWarehouseId(user, productId, erpWarehouseId, includeDeleted);
            }
        }

        return existStock;
    }

    private IObjectData queryByInvalidOrQueryArg(User user, ErpStockInvalidOrQueryModel.Arg arg, Boolean includeDeleted) {
        IObjectData existStock = null;
        //通过id
        if (!StringUtils.isBlank(arg.getObjectDataId())) {
            existStock = erpStockApiManager.query(user, arg.getObjectDataId(), includeDeleted);
        }

        //通过产品id和仓库id
        if (existStock == null) {
            existStock = erpStockManager.queryByProductIdAndWarehouseId(user, arg.getProductId(), arg.getErpWarehouseId(), includeDeleted);
        }

        return existStock;
    }

    private void checkCreateOrUpdateArg(BaseModel.Arg arg) {
        if (arg.getObjectData() == null) {
            throw new ValidateException("ERP库存对象不能为空");
        }
        IObjectData objectData = arg.getObjectData().toObjectData();
        //存在id 只校验id是否存在
        if (!StringUtils.isBlank(arg.getObjectDataId()) || (objectData.getId() != null && !StringUtils.isBlank(objectData.getId()))) {
            return;
        }

        //id不存在
        if (StringUtils.isBlank(objectData.get(ErpStockConstants.Field.Product.apiName, String.class))) {
            throw new ValidateException("产品id不能为空");
        }
        if (StringUtils.isBlank(objectData.get(ErpStockConstants.Field.ErpWarehouse.apiName, String.class))) {
            throw new ValidateException("仓库id不能为空");
        }
    }

    private void checkInvalidOrQueryArg(ErpStockInvalidOrQueryModel.Arg arg) {
        if (!StringUtils.isBlank(arg.getObjectDataId())) {
            return;
        }
        if (StringUtils.isBlank(arg.getProductId())) {
            throw new ValidateException("产品id不能为空");
        }
        if (StringUtils.isBlank(arg.getErpWarehouseId())) {
            throw new ValidateException("仓库id不能为空");
        }
    }
}
