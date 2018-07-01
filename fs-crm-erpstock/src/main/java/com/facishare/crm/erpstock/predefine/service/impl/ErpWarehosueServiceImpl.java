package com.facishare.crm.erpstock.predefine.service.impl;

import com.facishare.crm.erpstock.constants.ErpWarehouseConstants;
import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.crm.erpstock.predefine.manager.ErpWarehouseManager;
import com.facishare.crm.erpstock.predefine.service.ErpWarehouseService;
import com.facishare.crm.erpstock.predefine.service.model.base.*;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * @author liangk
 * @date 09/05/2018
 */
@Slf4j(topic = "erpStockAccess")
@Component
public class ErpWarehosueServiceImpl implements ErpWarehouseService {

    @Resource
    private ServiceFacade serviceFacade;

    @Resource
    private ErpStockManager erpStockManager;

    @Resource
    private ErpWarehouseManager erpWarehouseManager;

    @Override
    public BaseSaveModel.Result save(ServiceContext serviceContext, BaseSaveModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());

        //1、校验参数
        User user = serviceContext.getUser();
        IObjectData objectData= arg.getObjectData().toObjectData();

        if (StringUtils.isEmpty(objectData.get(ErpWarehouseConstants.Field.Name.apiName, String.class))) {
            throw new ValidateException("仓库名称不能为空");
        }

        Optional<String> ownerOpt = ObjectDataExt.of(objectData).getOwnerId();
        if (!ownerOpt.isPresent()) {
            objectData.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        } else {
            objectData.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(ownerOpt.get()));
        }

        //2、保存仓库数据
        objectData = erpWarehouseManager.buildObjectData(user, objectData);
        objectData = erpWarehouseManager.saveObjectData(user, objectData);

        BaseSaveModel.Result result = new BaseSaveModel.Result();
        result.setObjectData(ObjectDataDocument.of(objectData));
        return result;
    }

    @Override
    public BaseSaveModel.Result update(ServiceContext serviceContext, BaseSaveModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        BaseSaveModel.Result result = new BaseSaveModel.Result();

        //1、校验参数
        User user = serviceContext.getUser();

        ObjectDataDocument objectDataDoc = arg.getObjectData();

        if (null == objectDataDoc) {
            return result;
        }
        String id = objectDataDoc.toObjectData().getId();

        if (StringUtils.isEmpty(id)) {
            throw new ValidateException("仓库id不能为空");
        }

        //增量更新
        IObjectData currentObjectData = erpWarehouseManager.findById(user, id, ErpWarehouseConstants.API_NAME);

        IObjectData newObjectData = erpWarehouseManager.merge(currentObjectData, objectDataDoc.toObjectData());

        //2、更新仓库数据
        newObjectData = erpWarehouseManager.updateObjectData(user, newObjectData);

        result.setObjectData(ObjectDataDocument.of(newObjectData));
        return result;
    }

    @Override
    public BaseInvalidModel.Result invalid(ServiceContext serviceContext, BaseInvalidModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        //1、查询仓库记录
        User user = serviceContext.getUser();
        String id = arg.getObjectDataId();

        if (StringUtils.isEmpty(id)) {
            throw new ValidateException("仓库id不能为空");
        }
        //2、作废数据
        IObjectData objectData = erpWarehouseManager.findById(user, id, ErpWarehouseConstants.API_NAME);

        IObjectData invalidObjectData = erpWarehouseManager.invalidObjectData(user, objectData);

        BaseInvalidModel.Result result = new BaseInvalidModel.Result();
        result.setObjectData(ObjectDataDocument.of(invalidObjectData));
        return result;
    }
    @Override
    public BaseQueryModel.Result query(ServiceContext serviceContext, BaseQueryModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        //1、校验参数
        User user = serviceContext.getUser();
        String id = arg.getObjectDataId();

        if (StringUtils.isEmpty(id)) {
            throw new ValidateException("仓库id不能为空");
        }

        //2、查询仓库数据
        IObjectData objectData = erpWarehouseManager.findById(user, id, ErpWarehouseConstants.API_NAME);

        BaseQueryModel.Result result = new BaseQueryModel.Result();
        result.setObjectData(ObjectDataDocument.of(objectData));
        return result;
    }

    @Override
    public BaseDeleteModel.Result delete(ServiceContext serviceContext, BaseDeleteModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        //1、校验参数
        User user = serviceContext.getUser();
        String id = arg.getObjectDataId();

        if (StringUtils.isEmpty(id)) {
            throw new ValidateException("仓库id不能为空");
        }

        IObjectData objectData = serviceFacade.findObjectDataIncludeDeleted(user, id, ErpWarehouseConstants.API_NAME);
        if (Objects.isNull(objectData)) {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "删除仓库数据失败");
        }
        objectData = serviceFacade.bulkDelete(Collections.singletonList(objectData), user).get(0);

        BaseDeleteModel.Result result = new BaseModel.Result();
        result.setObjectData(ObjectDataDocument.of(objectData));

        return result;
    }

    @Override
    public BaseRecoverModel.Result recover(ServiceContext serviceContext, BaseRecoverModel.Arg arg) {
        erpStockManager.checkErpStockEnable(serviceContext.getTenantId());
        //1、校验参数
        User user = serviceContext.getUser();
        String id = arg.getObjectDataId();

        if (StringUtils.isEmpty(id)) {
            throw new ValidateException("仓库id不能为空");
        }

        IObjectData objectData = serviceFacade.findObjectDataIncludeDeleted(user, id, ErpWarehouseConstants.API_NAME);
        if (Objects.isNull(objectData)) {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "恢复仓库数据失败");
        }
        objectData = serviceFacade.bulkRecover(Collections.singletonList(objectData), user).get(0);

        BaseDeleteModel.Result result = new BaseModel.Result();
        result.setObjectData(ObjectDataDocument.of(objectData));

        return result;
    }
}
