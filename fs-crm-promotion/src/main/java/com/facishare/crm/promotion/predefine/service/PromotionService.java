package com.facishare.crm.promotion.predefine.service;

import java.util.List;

import com.facishare.crm.promotion.predefine.service.dto.BatchGetProductQuotaByProductIdsModel;
import com.facishare.crm.promotion.predefine.service.dto.PromotionType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("promotion")
public interface PromotionService {

    @ServiceMethod("enable_promotion")
    PromotionType.EnableResult enablePromotion(ServiceContext serviceContext);

    @ServiceMethod("is_promotion_enable")
    PromotionType.IsEnableResult isPromotionEnable(ServiceContext serviceContext);

    @ServiceMethod("get_by_id")
    PromotionType.DetailResult getById(ServiceContext serviceContext, PromotionType.IdModel idModel);

    @ServiceMethod("get_by_ids")
    List<PromotionType.DetailResult> getByIds(ServiceContext serviceContext, PromotionType.IdsModel idsModel);

    /**
     * 根据客户id查询订单促销列表
     * @param serviceContext
     * @param customerIdArg
     * @return
     */
    @ServiceMethod("list_by_customer_id")
    PromotionType.PromotionRuleResult listByCustomerId(ServiceContext serviceContext, PromotionType.CustomerIdArg customerIdArg);

    @ServiceMethod("list_products_by_customer_id")
    PromotionType.ListProductResult listProductsByCustomerId(ServiceContext serviceContext, PromotionType.ListProductsArg productsArg);

    /**
     * 查询产品对应促销情况
     * @param serviceContext
     * @param productPromotionListArg
     * @return
     */
    @ServiceMethod("list_promotion_by_product_ids")
    PromotionType.ProductToPromotionId listPromotionByProductIds(ServiceContext serviceContext, PromotionType.ProductPromotionListArg productPromotionListArg);

    /**
     * 根据客户id和产品id列表，查询产品促销列表
     * @param serviceContext
     * @param productPromotionListArg
     * @return
     */
    @ServiceMethod("list_by_product_ids")
    PromotionType.ProductPromotionResult listByProductIds(ServiceContext serviceContext, PromotionType.ProductPromotionListArg productPromotionListArg);

    @ServiceMethod("batch_get_product_quota")
    BatchGetProductQuotaByProductIdsModel.Result batchGetProductQuotaByProductIds(ServiceContext serviceContext, BatchGetProductQuotaByProductIdsModel.Arg arg);

    /**
     * 查询促销列表
     * @param serviceContext
     * @param customerIdArg
     * @return
     */
    @ServiceMethod("list_promotions")
    PromotionType.PromotionListResult listPromotions(ServiceContext serviceContext, PromotionType.CustomerIdArg customerIdArg);

}
