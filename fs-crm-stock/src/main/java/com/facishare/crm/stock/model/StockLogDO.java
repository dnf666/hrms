package com.facishare.crm.stock.model;

import com.facishare.crm.stock.constants.StockConstants;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.YesOrNoEnum;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

/**
 * @author linchf
 * @date 2018/3/6
 */
@Data
@Entity(value = "stockLog", noClassnameStored = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLogDO implements Serializable {
    private static final long serialVersionUID = -4351207143982610706L;

    @Id
    private ObjectId id;
    /**
     * 库存id
     */
    private String stockId;
    /**
     * 库存对应产品id
     */
    private String productId;
    /**
     * 库存对应仓库id
     */
    private String warehouseId;
    /**
     * 企业id
     */
    private String tenantId;
    /**
     * 操作者id
     */
    private String userId;
    /**
     * 修改实际库存数量  10 表示增加10实际库存  -10 表示减少10实际库存
     */
    private String modifiedRealStockNum;
    /**
     * 修改冻结库存数量
     */
    private String modifiedBlockedStockNum;
    /**
     * 操作后实际库存
     */
    private String afterRealStock;
    /**
     * 操作后冻结库存
     */
    private String afterBlockedStock;

    /**
     * 操作对象类型
     *
     * @see StockOperateObjectTypeEnum
     */
    private Integer operateObjectType;
    /**
     * 操作对象id
     */
    private String operateObjectId;
    /**
     * 操作类型
     *
     * @see com.facishare.crm.stock.enums.StockOperateTypeEnum
     */
    private Integer operateType;
    /**
     * 操作结果
     *
     * @see com.facishare.crm.stock.enums.StockOperateResultEnum
     */
    private Integer operateResult;
    /**
     * 修改前生命状态
     */
    private String beforeLifeStatus;
    /**
     * 修改后生命状态
     */
    private String afterLifeStatus;
    /**
     * 修改时间
     */
    private Long modifiedTime;

    /**
     * 是否完成更新
     * @see com.facishare.crm.stock.enums.YesOrNoEnum
     */
    private Integer hasFinished;

    public static StockLogDO buildLog(User user, IObjectData newStock, StockOperateInfo operateInfo) {
        return StockLogDO.builder().tenantId(user.getTenantId()).userId(user.getUserId())
                .stockId(newStock.getId() != null ? newStock.getId() : "")
                .warehouseId(newStock.get(StockConstants.Field.Warehouse.apiName, String.class))
                .productId(newStock.get(StockConstants.Field.Product.apiName, String.class))
                .afterRealStock(newStock.get(StockConstants.Field.RealStock.apiName, String.class))
                .afterBlockedStock(newStock.get(StockConstants.Field.BlockedStock.apiName, String.class))
                .operateObjectId(operateInfo.getOperateObjectId())
                .operateObjectType(operateInfo.getOperateObjectType())
                .operateType(operateInfo.getOperateType())
                .operateResult(operateInfo.getOperateResult())
                .beforeLifeStatus(operateInfo.getBeforeLifeStatus())
                .afterLifeStatus(operateInfo.getAfterLifeStatus())
                .modifiedTime(System.currentTimeMillis()).build();
    }
}
