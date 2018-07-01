package com.facishare.crm.promotion.predefine.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.promotion.constants.AdvertisementConstants;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.PromotionTypeEnum;
import com.facishare.crm.promotion.exception.PromotionBusinessException;
import com.facishare.crm.promotion.exception.PromotionErrorCode;
import com.facishare.crm.promotion.predefine.manager.TenantConfigManager;
import com.facishare.crm.promotion.predefine.service.PromotionInitService;
import com.facishare.crm.promotion.predefine.service.PromotionService;
import com.facishare.crm.promotion.predefine.service.dto.BatchGetProductQuotaByProductIdsModel;
import com.facishare.crm.promotion.predefine.service.dto.PromotionType;
import com.facishare.crm.promotion.util.HeaderUtil;
import com.facishare.crm.rest.CrmRestApi;
import com.facishare.crm.rest.dto.BatchGetPromotionProductQuantity;
import com.facishare.crm.rest.dto.SyncTenantSwitchModel;
import com.facishare.crm.util.RangeVerify;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.rest.proxy.util.JsonUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private TenantConfigManager tenantConfigManager;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private PromotionInitService promotionInitService;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private CrmRestApi crmRestApi;

    @Override
    public PromotionType.EnableResult enablePromotion(ServiceContext serviceContext) {
        PromotionType.EnableResult enableResult = new PromotionType.EnableResult();
        String tenantId = serviceContext.getUser().getTenantId();
        Set<String> existDispalyNames = checkDisplayName(tenantId);
        if (CollectionUtils.isNotEmpty(existDispalyNames)) {
            enableResult.setEnableStatus(PromotionType.PromotionSwitchEnum.PROMOTION_FAIL.status);
            enableResult.setMessage(Joiner.on(",").join(existDispalyNames).concat("名称已存在"));
            return enableResult;
        }
        enableResult.setEnableStatus(PromotionType.PromotionSwitchEnum.OPENED.status);
        enableResult.setMessage(PromotionType.PromotionSwitchEnum.OPENED.message);
        boolean success = false;
        try {
            success = promotionInitService.init(serviceContext.getUser());
        } catch (Exception e) {
            enableResult.setEnableStatus(PromotionType.PromotionSwitchEnum.PROMOTION_FAIL.status);
            enableResult.setMessage(PromotionType.PromotionSwitchEnum.PROMOTION_FAIL.message);
            log.warn("enablePromotion error,user:{}", serviceContext.getUser(), e);
            tenantConfigManager.updatePromotionStatus(serviceContext.getUser(), PromotionType.PromotionSwitchEnum.PROMOTION_FAIL);
        }
        tenantConfigManager.updatePromotionStatus(serviceContext.getUser(), PromotionType.PromotionSwitchEnum.OPENED);
        if (success) {
            SyncTenantSwitchModel.Arg arg = new SyncTenantSwitchModel.Arg();
            arg.setKey("31");
            arg.setValue("1");
            Map<String, String> headers = new HashMap<>();
            headers.put("x-fs-ei", tenantId);
            headers.put("x-fs-userInfo", serviceContext.getUser().getUserId());
            SyncTenantSwitchModel.Result result = crmRestApi.syncTenantSwitch(arg, headers);
            log.info("sync promotion status ,tenantId:{},result:{}", tenantId, result);
            if (result.getSuccess() == null || !result.getSuccess() || result.getErrorCode() != 0) {
                tenantConfigManager.updatePromotionStatus(serviceContext.getUser(), PromotionType.PromotionSwitchEnum.SALESORDER_FAIL);
                enableResult.setEnableStatus(PromotionType.PromotionSwitchEnum.SALESORDER_FAIL.status);
                enableResult.setMessage(PromotionType.PromotionSwitchEnum.SALESORDER_FAIL.message);
            }
        }
        return enableResult;
    }

    @Override
    public PromotionType.IsEnableResult isPromotionEnable(ServiceContext serviceContext) {
        PromotionType.PromotionSwitchEnum promotionStatus = tenantConfigManager.getPromotionStatus(serviceContext.getTenantId());
        PromotionType.IsEnableResult isEnableResult = new PromotionType.IsEnableResult();
        isEnableResult.setEnable(promotionStatus.status == PromotionType.PromotionSwitchEnum.OPENED.status);
        return isEnableResult;
    }

    @Override
    public PromotionType.DetailResult getById(ServiceContext serviceContext, PromotionType.IdModel idModel) {
        PromotionType.DetailResult detailResult = new PromotionType.DetailResult();
        IObjectDescribe objectDescribe = serviceFacade.findObject(serviceContext.getUser().getTenantId(), PromotionConstants.API_NAME);
        IObjectData promotionObjectData = serviceFacade.findObjectData(serviceContext.getUser(), idModel.getId(), objectDescribe);
        List<IFilter> iFilters = new ArrayList<>();
        SearchUtil.fillFilterEq(iFilters, PromotionProductConstants.Field.Promotion.apiName, idModel.getId());
        List<IObjectData> promotionProducts = searchQuery(serviceContext.getUser(), PromotionProductConstants.API_NAME, iFilters, new ArrayList(), 0, 500).getData();
        List<IObjectData> promotionRules = searchQuery(serviceContext.getUser(), PromotionRuleConstants.API_NAME, iFilters, new ArrayList(), 0, 500).getData();
        if (CollectionUtils.isNotEmpty(promotionProducts)) {
            detailResult.setPromotionProducts(ObjectDataDocument.ofList(promotionProducts));
        }
        detailResult.setPromotionRules(ObjectDataDocument.ofList(promotionRules));
        detailResult.setPromotion(ObjectDataDocument.of(promotionObjectData));
        return detailResult;
    }

    @Override
    public List<PromotionType.DetailResult> getByIds(ServiceContext serviceContext, PromotionType.IdsModel idsModel) {
        List<IObjectData> promotionDatas = serviceFacade.findObjectDataByIds(serviceContext.getTenantId(), idsModel.getIds(), PromotionConstants.API_NAME);
        Map<String, IObjectData> promotionMap = promotionDatas.stream().collect(Collectors.toMap(o -> o.getId(), Function.identity()));
        List<String> promotionIds = promotionDatas.stream().map(o -> o.getId()).collect(Collectors.toList());
        List<IFilter> filters = new ArrayList<>();
        SearchUtil.fillFilterIn(filters, PromotionRuleConstants.Field.Promotion.apiName, promotionIds);
        List<IObjectData> promotionProducts = searchQuery(serviceContext.getUser(), PromotionProductConstants.API_NAME, filters, new ArrayList(), 0, 500).getData();
        Map<String, List<IObjectData>> promotionProductsMap = promotionProducts.stream().collect(Collectors.groupingBy(o -> o.get(PromotionProductConstants.Field.Promotion.apiName, String.class)));
        List<IObjectData> promotionRules = searchQuery(serviceContext.getUser(), PromotionRuleConstants.API_NAME, filters, new ArrayList(), 0, 500).getData();
        Map<String, List<IObjectData>> promotionRuleMap = promotionRules.stream().collect(Collectors.groupingBy(o -> o.get(PromotionRuleConstants.Field.Promotion.apiName, String.class)));
        List<PromotionType.DetailResult> detailResults = new ArrayList<>();
        promotionIds.forEach(o -> {
            if (promotionMap.containsKey(o)) {
                PromotionType.DetailResult detailResult = new PromotionType.DetailResult();
                detailResult.setPromotion(ObjectDataDocument.of(promotionMap.get(o)));
                detailResult.setPromotionRules(ObjectDataDocument.ofList(promotionRuleMap.get(o)));
                detailResult.setPromotionProducts(ObjectDataDocument.ofList(promotionProductsMap.get(o)));
                detailResults.add(detailResult);
            }
        });
        return detailResults;
    }

    @Override
    public PromotionType.ProductToPromotionId listPromotionByProductIds(ServiceContext serviceContext, PromotionType.ProductPromotionListArg productPromotionListArg) {
        List<IObjectData> objectDataList = findPromotionsByCustomerId(serviceContext, productPromotionListArg.getCustomerId(), true).getData();
        List<String> promotionIds = objectDataList.stream().map(iObjectData -> iObjectData.getId()).collect(Collectors.toList());
        List<IFilter> filters = new ArrayList<>();
        List<IObjectData> products = null;
        if (CollectionUtils.isNotEmpty(promotionIds)) {
            SearchUtil.fillFilterIn(filters, PromotionProductConstants.Field.Promotion.apiName, promotionIds);
            products = searchQuery(serviceContext.getUser(), PromotionProductConstants.API_NAME, filters, new ArrayList(), 0, 500).getData();
        }
        Map<String, List<String>> tempMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(products)) {
            for (IObjectData objectData : products) {
                String productId = objectData.get(PromotionProductConstants.Field.Product.apiName, String.class);
                String promotionId = objectData.get(PromotionProductConstants.Field.Promotion.apiName, String.class);
                if (tempMap.containsKey(productId)) {
                    List<String> tmp = tempMap.get(productId);
                    tmp.add(promotionId);
                    tempMap.put(productId, tmp);
                } else {
                    tempMap.put(productId, Lists.newArrayList(promotionId));
                }
            }
        }
        Map<String, List<String>> productIdToPromotionId = new HashMap<>();
        for (String productId : productPromotionListArg.getProductIds()) {
            if (tempMap.containsKey(productId)) {
                productIdToPromotionId.put(productId, tempMap.get(productId));
            }
        }
        PromotionType.ProductToPromotionId productToPromotionId = new PromotionType.ProductToPromotionId();
        productToPromotionId.setProductToPromotionMap(productIdToPromotionId);
        return productToPromotionId;
    }

    @Override
    public PromotionType.ProductPromotionResult listByProductIds(ServiceContext serviceContext, PromotionType.ProductPromotionListArg productPromotionListArg) {
        List<IObjectData> objectDataList = findPromotionsByCustomerId(serviceContext, productPromotionListArg.getCustomerId(), true).getData();
        List<String> promotionIds = objectDataList.stream().map(iObjectData -> iObjectData.getId()).collect(Collectors.toList());
        List<IFilter> filters = new ArrayList<>();
        List<IObjectData> allProductPromotions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(promotionIds)) {
            SearchUtil.fillFilterIn(filters, PromotionProductConstants.Field.Promotion.apiName, promotionIds);
            allProductPromotions = searchQuery(serviceContext.getUser(), PromotionProductConstants.API_NAME, filters, new ArrayList(), 0, 500).getData();
        }
        //促销id  to  促销产品列表
        Map<String, List<IObjectData>> allPromotionId2ProductPromotionsMap = allProductPromotions.stream().collect(Collectors.groupingBy(o -> o.get(PromotionProductConstants.Field.Promotion.apiName, String.class)));

        //去除不再查询产品范围的促销产品
        List<IObjectData> products = allProductPromotions.stream().filter(iObjectData -> {
            String productId = iObjectData.get(PromotionProductConstants.Field.Product.apiName, String.class);
            return productPromotionListArg.getProductIds().contains(productId);
        }).collect(Collectors.toList());
        Set<String> ids = products.stream().map(iObjectData -> iObjectData.get(PromotionProductConstants.Field.Promotion.apiName, String.class)).collect(Collectors.toSet());
        List<String> nonDuplicationPromotionIds = new ArrayList<>();
        nonDuplicationPromotionIds.addAll(ids);
        List<IObjectData> promotions = objectDataList.stream().filter(iObjectData -> nonDuplicationPromotionIds.contains(iObjectData.getId())).collect(Collectors.toList());
        List<IObjectData> promotionRules = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(nonDuplicationPromotionIds)) {
            filters.clear();
            SearchUtil.fillFilterIn(filters, PromotionRuleConstants.Field.Promotion.apiName, nonDuplicationPromotionIds);
            promotionRules = searchQuery(serviceContext.getUser(), PromotionRuleConstants.API_NAME, filters, new ArrayList(), 0, 500).getData();
        }
        Map<String, IObjectData> promotionMap = promotions.stream().collect(Collectors.toMap(o -> o.getId(), Function.identity()));
        Map<String, List<IObjectData>> productId2productPromotionsMap = products.stream().collect(Collectors.groupingBy(o -> o.get(PromotionProductConstants.Field.Product.apiName, String.class)));
        //promotionId 2 List<promotionRules>
        Map<String, List<IObjectData>> promotionRuleMap = promotionRules.stream().collect(Collectors.groupingBy(o -> o.get(PromotionRuleConstants.Field.Promotion.apiName, String.class)));
        Map<String, PromotionType.WrapProductPromotion> wrapProductPromotionMap = new HashMap<>();
        List<PromotionType.WrapProductPromotion> wrapProductPromotions = new ArrayList<>();
        for (Map.Entry<String, List<IObjectData>> entry : productId2productPromotionsMap.entrySet()) {
            List<PromotionType.DetailResult> detailResults = new ArrayList<>();
            List<IObjectData> productPromotions = entry.getValue();
            List<String> promotionIds1 = productPromotions.stream().map(o -> o.get(PromotionProductConstants.Field.Promotion.apiName, String.class)).collect(Collectors.toList());
            for (String promotionId : promotionIds1) {
                PromotionType.DetailResult detailResult = new PromotionType.DetailResult();
                detailResult.setPromotionProducts(ObjectDataDocument.ofList(allPromotionId2ProductPromotionsMap.get(promotionId)));
                detailResult.setPromotion(ObjectDataDocument.of(promotionMap.get(promotionId)));
                detailResult.setPromotionRules(ObjectDataDocument.ofList(promotionRuleMap.get(promotionId)));
                if (wrapProductPromotionMap.containsKey(entry.getKey())) {
                    PromotionType.WrapProductPromotion wrapProductPromotion1 = wrapProductPromotionMap.get(entry.getKey());
                    List<PromotionType.DetailResult> tmp = wrapProductPromotion1.getPromotions();
                    tmp.add(detailResult);
                    wrapProductPromotion1.setPromotions(tmp);
                    wrapProductPromotionMap.put(entry.getKey(), wrapProductPromotion1);
                } else {
                    detailResults.add(detailResult);
                    PromotionType.WrapProductPromotion wrapProductPromotion = new PromotionType.WrapProductPromotion();
                    wrapProductPromotion.setPromotions(detailResults);
                    wrapProductPromotion.setProductId(entry.getKey());
                    wrapProductPromotionMap.put(entry.getKey(), wrapProductPromotion);
                }
            }
        }
        wrapProductPromotionMap.entrySet().forEach(o -> wrapProductPromotions.add(o.getValue()));
        PromotionType.ProductPromotionResult productPromotionResult = new PromotionType.ProductPromotionResult();
        productPromotionResult.setPromotions(wrapProductPromotions);
        return productPromotionResult;
    }

    //6.3 订货通首页促销列表
    @Override
    public PromotionType.PromotionListResult listPromotions(ServiceContext serviceContext, PromotionType.CustomerIdArg customerIdArg) {
        List<IObjectData> objectDataList = findPromotionsByCustomerId(serviceContext, customerIdArg.getCustomerId(), false).getData();
        List<ObjectDataDocument> objectDataDocumentList = ObjectDataDocument.ofList(objectDataList);
        PromotionType.PromotionListResult promotionListResult = new PromotionType.PromotionListResult();
        promotionListResult.setPromotions(objectDataDocumentList);
        return promotionListResult;
    }

    @Override
    public PromotionType.PromotionRuleResult listByCustomerId(ServiceContext serviceContext, PromotionType.CustomerIdArg customerIdArg) {
        List<IObjectData> objectDataList = findPromotionsByCustomerId(serviceContext, customerIdArg.getCustomerId(), true).getData();
        Iterator iterator = objectDataList.iterator();
        while (iterator.hasNext()) {
            IObjectData objectData = (IObjectData) iterator.next();
            String type = objectData.get(PromotionConstants.Field.Type.apiName, String.class);
            if (PromotionTypeEnum.DerateMoney.value.equals(type) || PromotionTypeEnum.FixedPrice.value.equals(type) || PromotionTypeEnum.NumberReachedGift.value.equals(type) || PromotionTypeEnum.PriceDiscount.value.equals(type)) {
                iterator.remove();
            }
        }
        List<PromotionType.DetailResult> orderRuleResults = new ArrayList<>();
        List<String> promotionIds = objectDataList.stream().map(o -> o.getId()).collect(Collectors.toList());
        Map<String, IObjectData> promotionMap = objectDataList.stream().collect(Collectors.toMap(o -> o.getId(), Function.identity()));
        List<IFilter> iFilters = new ArrayList<>();
        List<IObjectData> promotionRules = null;
        if (CollectionUtils.isNotEmpty(promotionIds)) {
            SearchUtil.fillFilterIn(iFilters, PromotionRuleConstants.Field.Promotion.apiName, promotionIds);
            promotionRules = searchQuery(serviceContext.getUser(), PromotionRuleConstants.API_NAME, iFilters, new ArrayList<>(), 0, 500).getData();
        }
        Map<String, List<IObjectData>> promotionRuleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(promotionRules)) {
            promotionRuleMap = promotionRules.stream().collect(Collectors.groupingBy(o -> o.get(PromotionRuleConstants.Field.Promotion.apiName, String.class)));
        }
        for (Map.Entry o : promotionMap.entrySet()) {
            if (promotionRuleMap.containsKey(o.getKey())) {
                PromotionType.DetailResult detailResult = new PromotionType.DetailResult();
                detailResult.setPromotionRules(ObjectDataDocument.ofList(promotionRuleMap.get(o.getKey())));
                detailResult.setPromotion(ObjectDataDocument.of((IObjectData) o.getValue()));
                orderRuleResults.add(detailResult);
            }
        }
        PromotionType.PromotionRuleResult promotionRuleResult = new PromotionType.PromotionRuleResult();
        promotionRuleResult.setPromotions(orderRuleResults);
        return promotionRuleResult;
    }

    @Override
    public PromotionType.ListProductResult listProductsByCustomerId(ServiceContext serviceContext, PromotionType.ListProductsArg productsArg) {
        List<IObjectData> objectDataList = findPromotionsByCustomerId(serviceContext, productsArg.getCustomerId(), true).getData();
        Iterator iterator = objectDataList.iterator();
        while (iterator.hasNext()) {
            IObjectData objectData = (IObjectData) iterator.next();
            String type = objectData.get(PromotionConstants.Field.Type.apiName, String.class);
            if (PromotionTypeEnum.OrderDerateMoney.value.equals(type) || PromotionTypeEnum.OrderDiscount.value.equals(type) || PromotionTypeEnum.OrderMoneyReachedGift.value.equals(type)) {
                iterator.remove();
            }
        }
        List<IObjectData> productList = new ArrayList<>();
        for (IObjectData objectData : objectDataList) {
            List<IFilter> filters = new ArrayList<>();
            SearchUtil.fillFilterEq(filters, PromotionProductConstants.Field.Promotion.apiName, objectData.getId());
            List<IObjectData> promotionProducts = searchQuery(serviceContext.getUser(), PromotionProductConstants.API_NAME, filters, new ArrayList<>(), 0, 500).getData();
            productList.addAll(promotionProducts);
        }
        HashSet<String> hashSet = new HashSet<>();
        Iterator iterator1 = productList.iterator();
        while (iterator1.hasNext()) {
            IObjectData objectData = (IObjectData) iterator1.next();
            String productId = objectData.get(PromotionProductConstants.Field.Product.apiName, String.class);
            if (!hashSet.contains(productId)) {
                hashSet.add(productId);
            } else {
                iterator1.remove();
            }
        }
        PromotionType.ListProductResult listProductResult = new PromotionType.ListProductResult();
        listProductResult.setPromotionProducts(ObjectDataDocument.ofList(productList));
        return listProductResult;
    }

    @Override
    public BatchGetProductQuotaByProductIdsModel.Result batchGetProductQuotaByProductIds(ServiceContext serviceContext, BatchGetProductQuotaByProductIdsModel.Arg arg) {
        List<BatchGetProductQuotaByProductIdsModel.PromotionProductIdArg> promotionProductIdArgs = arg.getPromotionProductIdArgs();
        Set<String> promotionIds = Sets.newHashSet();
        Set<String> productIds = Sets.newHashSet();
        List<BatchGetPromotionProductQuantity.PromotionProductArg> promotionProductArgList = Lists.newArrayList();
        Map<String, Double> amountMap = Maps.newHashMap();
        promotionProductIdArgs.forEach(x -> {
            BatchGetPromotionProductQuantity.PromotionProductArg promotionProductArg = new BatchGetPromotionProductQuantity.PromotionProductArg();
            promotionProductArg.setPromotionId(x.getPromotionId());
            promotionProductArg.setProductId(x.getProductId());
            promotionProductArgList.add(promotionProductArg);
            productIds.add(x.getProductId());
            promotionIds.add(x.getPromotionId());
            amountMap.put(x.getPromotionId() + "." + x.getProductId(), x.getAmount());
        });
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, PromotionProductConstants.Field.Promotion.apiName, Lists.newArrayList(promotionIds));
        SearchUtil.fillFilterIn(filters, PromotionProductConstants.Field.Product.apiName, Lists.newArrayList(productIds));
        QueryResult<IObjectData> queryResult = searchQuery(serviceContext.getUser(), PromotionProductConstants.API_NAME, filters, Lists.newArrayList(), 0, productIds.size() * promotionIds.size());
        List<IObjectData> objectDataList = queryResult.getData();
        log.debug("promotionProductDataList:{}", JsonUtil.toJson(objectDataList));
        Map<String, Integer> promotionProductQuotaMap = objectDataList.stream().collect(Collectors.toMap(x -> {
            String promotionId = getReferenceId(x, PromotionProductConstants.Field.Promotion.apiName);
            String productId = getReferenceId(x, PromotionProductConstants.Field.Product.apiName);
            return promotionId + "." + productId;
        }, x -> {
            Integer quota = x.get(PromotionProductConstants.Field.Quota.apiName, Integer.class);
            if (quota == null) {
                quota = 0;
            }
            return quota;
        }));
        log.info("promotionProductQuotaMap:{},amountMap:{}", promotionProductQuotaMap, amountMap);
        List<BatchGetPromotionProductQuantity.PromotionProductQuantity> promotionProductQuantityLisList = getPromotionQuantity(serviceContext.getUser(), promotionProductArgList);
        log.debug("promotionProductQuantityLisList:{}", JsonUtil.toJson(promotionProductQuantityLisList));
        Map<String, Integer> promotionProductQuantityMap = promotionProductQuantityLisList.stream().collect(Collectors.toMap(x -> x.getPromotionId() + "." + x.getProductId(), y -> Objects.isNull(y) ? 0 : y.getQuantity()));
        log.info("promotionProductQuantityMap:{}", promotionProductQuantityMap);
        List<BatchGetProductQuotaByProductIdsModel.PromotionProductQuota> promotionProductQuotaList = promotionProductIdArgs.stream().map(promotionProductIdArg -> {
            BatchGetProductQuotaByProductIdsModel.PromotionProductQuota promotionProductQuota = new BatchGetProductQuotaByProductIdsModel.PromotionProductQuota();
            String promotionId = promotionProductIdArg.getPromotionId();
            String productId = promotionProductIdArg.getProductId();
            String key = promotionId + "." + productId;
            boolean sales = false;
            Integer quota = promotionProductQuotaMap.getOrDefault(key, 0);
            Integer usedNum = promotionProductQuantityMap.getOrDefault(key, 0);
            Double amount = amountMap.get(key);
            Integer leftQuota = -1;//表示没限额
            if (quota == 0) {
                sales = true;
            } else {
                leftQuota = quota - usedNum;
                if (leftQuota < 0) {
                    log.warn("user:{},promotionId:{},productId:{},quota:{},usedNum:{}", serviceContext.getUser(), promotionId, productId, quota, usedNum);
                }
                if (quota - usedNum >= amount) {
                    sales = true;
                }
            }
            promotionProductQuota.setPromotionId(promotionId);
            promotionProductQuota.setProductId(productId);
            promotionProductQuota.setQuota(quota);
            promotionProductQuota.setSales(sales);
            promotionProductQuota.setLeftQuota(leftQuota);
            return promotionProductQuota;
        }).collect(Collectors.toList());
        BatchGetProductQuotaByProductIdsModel.Result result = new BatchGetProductQuotaByProductIdsModel.Result();
        result.setPromotionProductQuotas(promotionProductQuotaList);
        return result;
    }

    private List<BatchGetPromotionProductQuantity.PromotionProductQuantity> getPromotionQuantity(User user, List<BatchGetPromotionProductQuantity.PromotionProductArg> promotionProductArgs) {
        BatchGetPromotionProductQuantity.Result result = crmRestApi.getPromotionQuantiy(promotionProductArgs, HeaderUtil.getCrmHeader(user.getTenantId(), user.getUserId()));
        if (!result.isSuccess()) {
            throw new PromotionBusinessException(PromotionErrorCode.QUERY_PROMOTION_QUANTITY, result.getMessage());
        }
        return result.getValue();
    }

    private String getReferenceId(IObjectData data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            return String.valueOf(((Map) value).get(IObjectData.ID));
        }
        return String.valueOf(value);
    }

    public QueryResult<IObjectData> findPromotionsByCustomerId(ServiceContext serviceContext, String customerId, Boolean activeTime) {
        List<IFilter> filters = new ArrayList<>();
        if (activeTime) {
            SearchUtil.fillFilterLT(filters, PromotionConstants.Field.StartTime.apiName, new Date().getTime());
            //endTime存储的当天起始时间，需要加上24小时，才是真正的结束时间，所以这里比较做特殊处理
            SearchUtil.fillFilterGT(filters, PromotionConstants.Field.EndTime.apiName, new Date().getTime() - 86400000L);
        }
        SearchUtil.fillFilterEq(filters, SystemConstants.Field.LifeStatus.apiName, SystemConstants.LifeStatus.Normal.value);
        SearchUtil.fillFilterEq(filters, SystemConstants.Field.TennantID.apiName, serviceContext.getTenantId());
        SearchUtil.fillFilterEq(filters, PromotionConstants.Field.Status.apiName, true);
        QueryResult<IObjectData> result = searchQuery(serviceContext.getUser(), PromotionConstants.API_NAME, filters, new ArrayList<>(), 0, 1000);
        List<IObjectData> datas = result.getData();
        IObjectDescribe customerDescribe = serviceFacade.findObject(serviceContext.getTenantId(), ObjectAPINameMapping.Account.getApiName());
        IObjectData customerData = serviceFacade.findObjectData(serviceContext.getTenantId(), customerId, customerDescribe);
        List<IObjectData> filteredData = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(datas)) {
            Iterator iterator = datas.iterator();
            while (iterator.hasNext()) {
                IObjectData objectData = (IObjectData) iterator.next();
                if (validatePromotion(customerDescribe, customerData, objectData)) {
                    filteredData.add(objectData);
                }
            }
        }
        QueryResult<IObjectData> dataQueryResult = new QueryResult<>();
        dataQueryResult.setData(filteredData);
        return dataQueryResult;
    }

    public QueryResult<IObjectData> searchQuery(User user, String objectApiName, List<IFilter> filters, List<OrderBy> orders, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOrders(orders);
        searchTemplateQuery.setWheres(Lists.newArrayList());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult;
    }

    private boolean validatePromotion(IObjectDescribe customerDescribe, IObjectData customerObjData, IObjectData promotionData) {
        // 根据当前客户的适用范围过滤符合条件的促销表
        Object customerRange = promotionData.get("customer_range");
        if (Objects.nonNull(customerRange) && StringUtils.isNotBlank(customerRange.toString())) {
            JSONObject accountRangeObj = JSON.parseObject(customerRange.toString());
            if (accountRangeObj != null && !"noCondition".equals(accountRangeObj.get("type").toString())) {
                return RangeVerify.verifyConditions(customerDescribe, customerObjData, accountRangeObj.getJSONObject("value"));
            }
        }
        return true;
    }

    private Set<String> checkDisplayName(String tenantId) {
        try {
            Set<String> existDisplayNames = Sets.newHashSet();
            List<String> existPromotionApiNames = objectDescribeService.checkDisplayNameExist(tenantId, PromotionConstants.DISPLAY_NAME, "CRM");
            existPromotionApiNames.forEach(x -> {
                if (!PromotionConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(PromotionConstants.DISPLAY_NAME);
                }
            });
            List<String> existPromotionProductApiNames = objectDescribeService.checkDisplayNameExist(tenantId, PromotionProductConstants.DISPLAY_NAME, "CRM");
            existPromotionProductApiNames.forEach(x -> {
                if (!PromotionProductConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(PromotionProductConstants.DISPLAY_NAME);
                }
            });

            List<String> existPromotionRuleApiNames = objectDescribeService.checkDisplayNameExist(tenantId, PromotionRuleConstants.DISPLAY_NAME, "CRM");
            existPromotionRuleApiNames.forEach(x -> {
                if (!PromotionRuleConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(PromotionRuleConstants.DISPLAY_NAME);
                }
            });

            List<String> existAdvertisementApiNames = objectDescribeService.checkDisplayNameExist(tenantId, AdvertisementConstants.DISPLAY_NAME, "CRM");
            existAdvertisementApiNames.forEach(x -> {
                if (!AdvertisementConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(AdvertisementConstants.API_NAME);
                }
            });
            log.info("checkDisplayName tenantId:{},Result:{}", tenantId, existDisplayNames);
            return existDisplayNames;
        } catch (MetadataServiceException e) {
            log.warn("checkDisplayName error,tenantId:{}", tenantId, e);
            throw new PromotionBusinessException(() -> e.getErrorCode().getCode(), e.getMessage());
        }
    }
}
