package com.facishare.crm.stock.model;

import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author linchf
 * @date 2018/3/7
 */
@Data
@Builder
public class StockOperateInfo {
    /**
     * 操作对象类型
     * @see StockOperateObjectTypeEnum
     */
    private Integer operateObjectType;
    /**
     * 操作对象id
     */
    private String operateObjectId;
    /**
     * 操作类型
     * @see com.facishare.crm.stock.enums.StockOperateTypeEnum
     */
    private Integer operateType;
    /**
     * 操作结果
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
}
