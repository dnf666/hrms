package com.facishare.crm.erpstock.predefine.manager;

import com.facishare.common.proxy.helper.StringUtils;
import com.facishare.crm.erpstock.constants.ErpStockConstants;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author linchf
 * @date 2018/5/10
 */
@Service
@Slf4j(topic = "erpStockAccess")
public class ErpStockApiManager extends CommonManager {
    @Resource
    private ServiceFacade serviceFacade;

    public IObjectData query(User user, String id, Boolean includeDeleted) {
        if (includeDeleted) {
            return findObjectDataIncludeDeleted(user, id, ErpStockConstants.API_NAME);
        }
        return findById(user, id, ErpStockConstants.API_NAME);
    }

    public ObjectDataDocument save(User user, IObjectData saveStock) {
        checkSaveArg(saveStock);
        String productId = saveStock.get(ErpStockConstants.Field.Product.apiName, String.class);
        String erpWarehouseId = saveStock.get(ErpStockConstants.Field.ErpWarehouse.apiName, String.class);
        String realStock = saveStock.get(ErpStockConstants.Field.RealStock.apiName, String.class);
        String availableStock = saveStock.get(ErpStockConstants.Field.AvailableStock.apiName, String.class);

        Optional<String> ownerId = ObjectDataExt.of(saveStock).getOwnerId();

        IObjectData objectData = buildStockByAmount(user, erpWarehouseId, productId, realStock, availableStock);
        if (ownerId.isPresent()) {
            objectData.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(ownerId.get()));
        }

        return ObjectDataDocument.of(saveObjectData(user, objectData));
    }

    public ObjectDataDocument update(User user, IObjectData existStock, IObjectData newObjectData) {
        String realStock = newObjectData.get(ErpStockConstants.Field.RealStock.apiName, String.class);
        String availableStock = newObjectData.get(ErpStockConstants.Field.AvailableStock.apiName, String.class);

        boolean isChange = false;
        if (!StringUtils.isBlank(realStock)) {
            if (new BigDecimal(realStock).compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidateException("实际库存不能小于0");
            }
            existStock.set(ErpStockConstants.Field.RealStock.apiName, realStock);
            isChange = true;
        }
        if (!StringUtils.isBlank(availableStock)) {
            existStock.set(ErpStockConstants.Field.AvailableStock.apiName, availableStock);
            isChange = true;
        }
        if (isChange) {
            existStock = updateObjectData(user, existStock);
        }
        return ObjectDataDocument.of(existStock);
    }

    public ObjectDataDocument invalid(User user, IObjectData stock) {
        return ObjectDataDocument.of(invalidObjectData(user, stock));
    }

    private IObjectData buildStockByAmount(User user, String warehouseId, String productId, String realStock, String availableStock) {
        IObjectDescribe stockDescribe = serviceFacade.findObject(user.getTenantId(), ErpStockConstants.API_NAME);

        IObjectData stock = new ObjectData();
        stock.set(ErpStockConstants.Field.Product.apiName, productId);
        stock.set(ErpStockConstants.Field.ErpWarehouse.apiName, warehouseId);
        stock.set(ErpStockConstants.Field.RealStock.apiName, realStock);
        stock.set(ErpStockConstants.Field.AvailableStock.apiName, availableStock);

        stock.setTenantId(user.getTenantId());
        stock.setCreatedBy(user.getUserId());
        stock.setLastModifiedBy(user.getUserId());
        stock.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        stock.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        stock.set(IObjectData.DESCRIBE_ID, stockDescribe.getId());
        stock.set(IObjectData.DESCRIBE_API_NAME, ErpStockConstants.API_NAME);
        stock.set(IObjectData.PACKAGE, "CRM");
        stock.set(IObjectData.VERSION, stockDescribe.getVersion());

        //相关团队
        ObjectDataExt objectDataExt = ObjectDataExt.of(stock);
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);
        objectDataExt.setTeamMembers(Lists.newArrayList(teamMember));

        return objectDataExt.getObjectData();
    }

    private void checkSaveArg(IObjectData objectData) {
        if (StringUtils.isBlank(objectData.get(ErpStockConstants.Field.RealStock.apiName, String.class))) {
            throw new ValidateException("实际库存不能为空");
        }
        if (StringUtils.isBlank(objectData.get(ErpStockConstants.Field.AvailableStock.apiName, String.class))) {
            throw new ValidateException("可用库存不能为空");
        }
        if (new BigDecimal(objectData.get(ErpStockConstants.Field.RealStock.apiName, String.class)).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidateException("实际库存不能小于0");
        }
    }
}
