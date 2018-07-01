package com.facishare.crm.stock.predefine.service.model;

import com.facishare.open.common.storage.mysql.dao.Pager;
import lombok.Data;

/**
 * @author linchf
 * @date 2018/5/31
 */
public class QueryStockLogByTemplateModel {
    @Data
    public static class Arg {
        private StockLogVO template;
    }

    @Data
    public static class Result {
        Pager<StockLogVO> pager;
        String result = "success";
    }

    @Data
    public static class StockLogVO {
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
         */
        private Integer operateObjectType;
        /**
         * 操作对象id
         */
        private String operateObjectId;
        /**
         * 操作类型
         *
         */
        private Integer operateType;
        /**
         * 操作结果
         *
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
         */
        private Integer hasFinished = 1;

        private Boolean isModifyBlockedStock = false;

        private Boolean isModifyRealStock = false;

        private Boolean isModifyTimeDesc = false;

        private Integer currentPage;

        private Integer pageSize;

    }
}
