package com.facishare.crm.sfa.predefine.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.model.PickOnePriceBookModel;
import com.facishare.crm.sfa.predefine.service.model.PriceBookProdResult;
import com.facishare.crm.sfa.predefine.service.model.PriceBookResult;
import com.facishare.crm.sfa.predefine.service.model.ProductResult;
import com.facishare.crm.sfa.predefine.service.model.ValidImportSalesOrder;
import com.facishare.crm.sfa.predefine.service.model.ValidImportSalesOrderProduct;
import com.facishare.crm.sfa.predefine.service.model.ValidateAccountPriceBook;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.crm.sfa.utilities.constant.ProductConstants;
import com.facishare.crm.userdefobj.DefObjConstants;
import com.facishare.paas.appframework.common.util.ObjectAPINameMapping;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDataService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.SelectOneFieldDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 对象描述服务接口 <p> Created by liyiguang on 2017/6/17.
 */
@ServiceModule("pricebook")
@Component
@Slf4j
public class PriceBookService {
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private PriceBookCommonService priceBookCommonService;
    @Autowired
    private IObjectDataService objectDataService;

    /**
     * 创建对象业务类型
     */
    @ServiceMethod("list")
    public PriceBookResult.Result findByAccountId(PriceBookResult.Arg arg, ServiceContext context) {
        log.info("arg:{},context{", arg, context);
        String accountId = arg.getAccount_id();
        if (StringUtils.isBlank(accountId)) {
            log.error("accountId is blank,tenantId {}", context.getTenantId());
            return PriceBookResult.Result.builder().build();
        }
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(arg.getLimit());
        searchQuery.setOffset(arg.getOffset());
        searchQuery.setFilters(Lists.newLinkedList());
        QueryResult<IObjectData> queryResult = findPriceBookByAccountId(context.getUser(), searchQuery, accountId);
        return PriceBookResult.Result.builder().dataList(ObjectDataDocument.ofList(queryResult.getData())).total(queryResult.getTotalNumber()).limit(searchQuery.getLimit()).offset(searchQuery.getOffset()).build();
    }


    public QueryResult<IObjectData> findPriceBookByAccountId(User user, SearchTemplateQuery searchQuery, String accountId) {
        if (searchQuery.getOffset() != 0) {
            QueryResult<IObjectData> queryResult = new QueryResult<>();
            queryResult.setTotalNumber(0);
            queryResult.setData(Lists.newArrayList());
            return queryResult;
        }
        searchQuery.setLimit(2000);
        String tenantId = user.getTenantId();
        IObjectData accountObjData = serviceFacade.findObjectData(user, accountId, ObjectAPINameMapping.Account.getApiName());
        if (accountObjData == null) {
            log.error("accountObjData is blank,tenantId {},accountId {}", tenantId, accountId);
            throw new ValidateException("当前客户不存在！");
        }
        List<IFilter> filters = searchQuery.getFilters();
        SearchUtil.fillFiltersWithUser(user, filters);
        //启用状态
        SearchUtil.fillFilterEq(filters, PriceBookConstants.Field.ACTIVESTATUS.getApiName(), PriceBookConstants.ActiveStatus.ON.getStatus());
        //正常生命状态
        SearchUtil.fillFilterEq(filters, UdobjConstants.LIFE_STATUS_API_NAME, UdobjConstants.LIFE_STATUS_VALUE_NORMAL);
        //没有排序时，使用默认
        if (CollectionUtils.isEmpty(searchQuery.getOrders())) {
            searchQuery.setOrders(Lists.newArrayList(SearchUtil.orderByLastModifiedTime()));
        }
        searchQuery.setPermissionType(0);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, PriceBookConstants.API_NAME, searchQuery);

        CountDownLatch latch = new CountDownLatch(queryResult.getData().size());
        IObjectDescribe accountDescribe = this.serviceFacade.findObject(tenantId, Utils.ACCOUNT_API_NAME);
        //验证需要的
        boolean isAdmin = serviceFacade.isAdmin(user);
        List<String> departList = serviceFacade.queryAllSuperDeptByUserId(user.getTenantId(), User.SUPPER_ADMIN_USER_ID, user.getUserId());
        List<String> objectDataIds = Lists.newCopyOnWriteArrayList();
        for (IObjectData priceBookData : queryResult.getData()) {
            executor.execute(() -> {
                try {
                    if (priceBookCommonService.validateAccountPriceBookWithData(user, accountDescribe, priceBookData, accountObjData, departList, isAdmin)) {
                        objectDataIds.add(priceBookData.getId());
                    }
                } catch (Exception e) {
                    log.error("list queryResult.getData() error ,tenantId {}", tenantId, e);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("handleReferences list,CountDownLatch error.", e);
        }
        queryResult.getData().removeIf(k -> !objectDataIds.contains(k.getId()));
        queryResult.setTotalNumber(queryResult.getData().size());
        return queryResult;
    }

    @ServiceMethod("validate_account_pricebook")
    public ValidateAccountPriceBook.Result validateAccountPriceBook(ValidateAccountPriceBook.Arg arg, ServiceContext context) {
        log.info("arg:{},context{", arg, context);
        String accountId = arg.getAccount_id();
        String priceBookId = arg.getPriceBookId();
        return ValidateAccountPriceBook.Result.builder().result(priceBookCommonService.validateAccountPriceBook(context.getUser(), priceBookId, accountId)).build();
    }

    @ServiceMethod("productlist")
    public PriceBookProdResult.Result findPriceBookProdListByQueryCondition(PriceBookProdResult.Arg arg, ServiceContext context) {
        log.info("arg:{},context{", arg, context);


        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(arg.getLimit());
        searchQuery.setOffset(arg.getOffset());

        List filters = StringUtils.isNotBlank(arg.getFilters()) ? JSON.parseArray(arg.getFilters(), Filter.class) : Lists.newLinkedList();
        SearchUtil.fillFiltersWithUser(context.getUser(), filters);
        SearchUtil.fillFilterEq(filters, "pricebook_id", arg.getPricebook_id());
        //正常生命状态的
        SearchUtil.fillFilterEq(filters, UdobjConstants.LIFE_STATUS_API_NAME, UdobjConstants.LIFE_STATUS_VALUE_NORMAL);
        //查询产品状态等于已上架的
        SearchUtil.fillFilterEq(filters, ProductConstants.Field.STATUS.getApiName(), ProductConstants.Status.ON.getStatus());
        searchQuery.setFilters(filters);
        searchQuery.setPermissionType(0);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(context.getUser(), PriceBookConstants.API_NAME_PRODUCT, searchQuery);
        return PriceBookProdResult.Result.builder().dataList(ObjectDataDocument.ofList(queryResult.getData())).total(queryResult.getTotalNumber()).limit(arg.getLimit()).offset(arg.getOffset()).build();
    }

    @ServiceMethod("addprod_prodlist")
    public ProductResult.Result addProdProdlist(ProductResult.Arg arg, ServiceContext context) {
        log.info("arg:{},context{", arg, context);
        if (StringUtils.isBlank(arg.getPricebook_id())) {
            return ProductResult.Result.builder().build();
        }
        List filters = StringUtils.isNotBlank(arg.getFilters()) ? JSON.parseArray(arg.getFilters(), Filter.class) : Lists.newLinkedList();
        QueryResult<IObjectData> queryResult = findProductNotInPriceBook(context.getUser(), arg.getPricebook_id(), filters, arg.getOffset(), arg.getLimit());
        return ProductResult.Result.builder().dataList(ObjectDataDocument.ofList(queryResult.getData())).total(queryResult.getTotalNumber()).limit(arg.getLimit()).offset(arg.getOffset()).build();
    }


    @ServiceMethod("valid_import_salesorderprod")
    public ValidImportSalesOrderProduct.Result validImportSalesOrderProduct(ValidImportSalesOrderProduct.Arg arg, ServiceContext context) {
        List<ValidImportSalesOrderProduct.PriceBookImportInfo> priceBookImportInfoList = arg.getPriceBookProductImportInfoList();

        if (CollectionUtils.isNotEmpty(priceBookImportInfoList)) {
            Map<String, IObjectData> key2PriceBookProductData = Maps.newHashMap();

            List<List<ValidImportSalesOrderProduct.PriceBookImportInfo>> priceBookImportInfoGroup = Lists.partition(priceBookImportInfoList, 300);
            for (List<ValidImportSalesOrderProduct.PriceBookImportInfo> priceBookImportInfoPart : priceBookImportInfoGroup) {
                List<IObjectData> priceBookProducts = getPriceBookProductData(context.getUser(), priceBookImportInfoPart);
                fillPriceBookProductIdToPriceBookImportInfo(priceBookImportInfoList, key2PriceBookProductData, priceBookProducts);
            }
        }
        return new ValidImportSalesOrderProduct.Result(priceBookImportInfoList);
    }


    @ServiceMethod("valid_import_salesorder")
    public ValidImportSalesOrder.Result validImportSalesOrder(ValidImportSalesOrder.Arg arg, ServiceContext context) {
        List<ValidImportSalesOrder.PriceBookImportInfo> priceBookImportInfoList = arg.getPriceBookImportInfoList();

        if (CollectionUtils.isNotEmpty(priceBookImportInfoList)) {
            Set<String> priceBookNameSet = Sets.newHashSet();
            Set<String> customerIdSet = Sets.newHashSet();

            priceBookImportInfoList.forEach(o -> {
                if (o.getPriceBookName() != null && o.getCustomerId() != null) {
                    priceBookNameSet.add(o.getPriceBookName());
                    customerIdSet.add(o.getCustomerId());
                }
            });

            List<String> priceBookNames = Lists.newArrayList(priceBookNameSet);
            List<String> customerIds = Lists.newArrayList(customerIdSet);

            List<List<String>> priceBookNamesGroup = Lists.partition(priceBookNames, 300);
            List<List<String>> customerIdsGroup = Lists.partition(customerIds, 300);

            Map<String, IObjectData> name2PriceBookData = Maps.newHashMap();
            Map<String, IObjectData> accountId2AccountData = Maps.newHashMap();

            for (List<String> priceBookNameList : priceBookNamesGroup) {
                name2PriceBookData.putAll(getPriceBookName2PriceBookData(context.getUser(), priceBookNameList));
            }

            for (List<String> customerIdsList : customerIdsGroup) {
                accountId2AccountData.putAll(getAccountId2AccountData(context.getTenantId(), customerIdsList));
            }
            validAndSetPriceBookId(context.getTenantId(), priceBookImportInfoList, name2PriceBookData, accountId2AccountData);
        } else {
            return new ValidImportSalesOrder.Result(Lists.newArrayListWithCapacity(0));
        }
        return new ValidImportSalesOrder.Result(priceBookImportInfoList);
    }


    @ServiceMethod("get_standard_pricebook_id")
    public String getStandardPriceBookId(ServiceContext context) {
        IObjectData standardPriceBook = getStandardPriceBook(context.getUser());
        if (standardPriceBook != null) {
            return standardPriceBook.getId();
        } else {
            return "";
        }
    }


    @ServiceMethod("pickone_for_sales_order")
    public PickOnePriceBookModel.Result getOnePriceBook(PickOnePriceBookModel.Arg arg, ServiceContext context) {
        List<IObjectData> queryResult = getPriceBooks(arg, context);
        if (CollectionUtils.isNotEmpty(queryResult)) {
            String priceBookId = arg.getPriceBookId();
            if (StringUtils.isNotBlank(priceBookId)) {

                IObjectData standardPriceBook = null;

                for (IObjectData objectData : queryResult) {
                    if (priceBookId.equals(objectData.getId())) {
                        return new PickOnePriceBookModel.Result(ObjectDataDocument.of(objectData));
                    }

                    if (objectData.get("is_standard", Boolean.class)) {
                        standardPriceBook = objectData;
                    }
                }

                if (standardPriceBook != null) {
                    return new PickOnePriceBookModel.Result(ObjectDataDocument.of(standardPriceBook));
                }

                return new PickOnePriceBookModel.Result(ObjectDataDocument.of(queryResult.get(0)));
            } else {
                for (IObjectData objectData : queryResult) {
                    if (objectData.get("is_standard", Boolean.class)) {
                        return new PickOnePriceBookModel.Result(ObjectDataDocument.of(objectData));
                    }
                }
                return new PickOnePriceBookModel.Result(ObjectDataDocument.of(queryResult.get(0)));
            }
        } else {
            return new PickOnePriceBookModel.Result(Maps.newHashMapWithExpectedSize(0));
        }
    }

    private List<IObjectData> getPriceBooks(PickOnePriceBookModel.Arg arg, ServiceContext context) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setOffset(0);
        searchQuery.setFilters(Lists.newLinkedList());
        try {
            return findPriceBookByAccountId(context.getUser(), searchQuery, arg.getAccountId()).getData();
        } catch (Throwable e) {
            return Lists.newArrayListWithExpectedSize(0);
        }
    }


    private void handleDataSetPriceBookId(String tenantId, Map<String, IObjectData> name2PriceBookData
            , Map<String, IObjectData> accountId2AccountData, IObjectDescribe accountDescribe, ValidImportSalesOrder.PriceBookImportInfo o) {

        IObjectData accountData = accountId2AccountData.get(o.getCustomerId());
        IObjectData priceBookData = name2PriceBookData.get(o.getPriceBookName());
        String priceBookId = priceBookData.getId();
        if (accountData != null && priceBookId != null && o.getUserId() != null) {
            if (priceBookCommonService.validateAccountPriceBookWithData(new User(tenantId, o.getUserId().toString()), accountDescribe, priceBookData, accountData)) {
                o.setPriceBookId(priceBookId);
            }
        }
    }


    private void fillPriceBookProductIdToPriceBookImportInfo(List<ValidImportSalesOrderProduct.PriceBookImportInfo> priceBookImportInfoList, Map<String, IObjectData> key2PriceBookProductData, List<IObjectData> priceBookProducts) {
        if (CollectionUtils.isNotEmpty(priceBookProducts)) {
            priceBookProducts.forEach(o -> key2PriceBookProductData.put(o.get("product_id", String.class) + o.get("pricebook_id", String.class), o));
            priceBookImportInfoList.forEach(o -> {
                IObjectData tmp = key2PriceBookProductData.get(o.getProductId() + o.getPriceBookId());
                if (tmp != null) {
                    o.setPriceBookProductId(tmp.getId());
                }
            });
        }
    }


    private void validAndSetPriceBookId(String tenantId, List<ValidImportSalesOrder.PriceBookImportInfo> priceBookImportInfoList, Map<String, IObjectData> name2PriceBookData, Map<String, IObjectData> accountId2AccountData) {
        IObjectDescribe accountDescribe = this.serviceFacade.findObject(tenantId, Utils.ACCOUNT_API_NAME);
        CountDownLatch countDownLatch = new CountDownLatch(priceBookImportInfoList.size());
        priceBookImportInfoList.forEach(o -> executor.execute(() -> {
            try {
                handleDataSetPriceBookId(tenantId, name2PriceBookData, accountId2AccountData, accountDescribe, o);
            } catch (Exception e) {
                log.error("handleDataSetPriceBookId error.", e);
            } finally {
                countDownLatch.countDown();
            }
        }));

        try {
            countDownLatch.await(27, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("运行超时");
        }
    }

    private Map<String, IObjectData> getAccountId2AccountData(String tenantId, List<String> customerIds) {
        List<IObjectData> accountDataList = serviceFacade.findObjectDataByIds(tenantId, customerIds, Utils.ACCOUNT_API_NAME);

        Map<String, IObjectData> accountId2AccountData = Maps.newHashMap();
        accountDataList.forEach(o -> {
            if (o.getId() != null)
                accountId2AccountData.put(o.getId().toString(), o);
        });
        return accountId2AccountData;
    }


    private Map<String, IObjectData> getPriceBookName2PriceBookData(User user, List<String> priceBookNames) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(priceBookNames.size());
        List<IFilter> filters = Lists.newArrayList();

        String sql = getQueryPriceBookByNamesSql(priceBookNames);
        SearchUtil.fillFilterInBySql(filters, IObjectData.ID, SearchUtil.FIELD_VALUE_TYPE_SQL, sql);
        searchQuery.setFilters(filters);
        // TODO: 2018/5/16 SA 需要价目表过滤
        QueryResult<IObjectData> priceBookQueryResult = serviceFacade.findBySearchQuery(user, Utils.PRICE_BOOK_API_NAME, searchQuery);
        List<IObjectData> priceBooks = priceBookQueryResult.getData();

        Map<String, IObjectData> name2PriceBookData = Maps.newHashMap();
        priceBooks.forEach(o -> name2PriceBookData.put(o.getName(), o));
        return name2PriceBookData;
    }

    private String getQueryPriceBookByNamesSql(List<String> priceBookNames) {
        StringBuilder sql = new StringBuilder("select id from price_book where ");
        for (int i = 0; i < priceBookNames.size(); i++) {
            sql.append(String.format(" name='%s' ", priceBookNames.get(i)));
            if (i + 1 < priceBookNames.size()) {
                sql.append(" or ");
            }
        }
        return sql.toString();
    }


    private List<IObjectData> getPriceBookProductData(User user, List<ValidImportSalesOrderProduct.PriceBookImportInfo> priceBookImportInfoList) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(priceBookImportInfoList.size());
        List<IFilter> filters = Lists.newArrayList();
        StringBuilder patternStr = new StringBuilder();
        int index = 0;

        patternStr.append("(");
        for (int i = 0; i < priceBookImportInfoList.size(); i++) {
            patternStr.append("( ").append(++index).append(" and ").append(++index).append(")");
            if (i + 2 <= priceBookImportInfoList.size()) {
                patternStr.append("or");
            }
            SearchUtil.fillFilterEq(filters, "pricebook_id", priceBookImportInfoList.get(i).getPriceBookId());
            SearchUtil.fillFilterEq(filters, "product_id", priceBookImportInfoList.get(i).getProductId());
        }

        patternStr.append(") and ").append(index + 1);

        SearchUtil.fillFilterEq(filters, "is_deleted", "0");
        searchQuery.setFilters(filters);
        searchQuery.setPattern(patternStr.toString());
        try {
            return objectDataService.findBySearchQuery(user.getTenantId(), Utils.PRICE_BOOK_PRODUCT_API_NAME, searchQuery, ActionContextExt.of(user).getContext()).getData();
        } catch (MetadataServiceException e) {
            log.error("metadata error", e);
            return Lists.newArrayListWithCapacity(0);
        }
    }


    public QueryResult<IObjectData> findProductNotInPriceBook(User user, String priceBookId, List<IFilter> filters, Integer offset, Integer limit) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(limit);
        searchQuery.setOffset(offset);

        SearchUtil.fillFiltersWithUser(user, filters);
        String sqlVal = getPriceBookProductSql(user, priceBookId);
        SearchUtil.fillFilterBySql(filters, IObjectData.ID, Operator.NIN, sqlVal);
        //查询产品状态等于已上架的
//        SearchUtil.fillFilterEq(filters, ProductConstants.Field.STATUS.getApiName(), ProductConstants.Status.ON.getStatus());
        //查询产品状态不等于已作废的
        SearchUtil.fillFilterNotEq(filters, ProductConstants.Field.STATUS.getApiName(), ProductConstants.Status.HIDE.getStatus());
        specialProductFilters(user, filters);
        searchQuery.setFilters(filters);
        //默认按状态升序，已上架排前面
        searchQuery.setOrders(Lists.newArrayList(new OrderBy(ProductConstants.Field.STATUS.getApiName(), true), SearchUtil.orderByLastModifiedTime()));
        //用pg的查询方式
        return serviceFacade.findBySearchQuery(ActionContextExt.of(user).pgDbType().getContext(), Utils.PRODUCT_API_NAME, searchQuery);
    }


    private String getPriceBookProductSql(User user, String pricebookId) {
        //        sql.append(String.format(" and %s='%s' ", UdobjConstants.LIFE_STATUS_API_NAME, UdobjConstants.LIFE_STATUS_VALUE_NORMAL));
        return "select product_id from price_book_product where 1=1 " + String.format(" and %s='%s' ", "tenant_id", user.getTenantId()) +
                String.format(" and package='%s' ", "CRM") +
                String.format(" and pricebook_id='%s' ", pricebookId) +
                String.format(" and %s='%s' ", IObjectData.IS_DELETED, "0") +
                " and price_book_product.product_id = ProductObj.id ";
    }

    /**
     * 根据产品分类获取所有的子分类，包括自身
     */
    public List setProductCategoryChildrenList(User user, String category, List categoryList) {
        String fieldApiName = "category";
        categoryList.add(category);
        IObjectDescribe describe = this.serviceFacade.findObject(user.getTenantId(), Utils.PRODUCT_API_NAME);
        String fieldLabel = "";
        SelectOneFieldDescribe fieldDescribe = (SelectOneFieldDescribe) describe.getFieldDescribe(fieldApiName);
        for (ISelectOption selectOption : fieldDescribe.getSelectOptions()) {
            if (selectOption.getValue().equals(category)) {
                fieldLabel = selectOption.getLabel();
                break;
            }
        }
        if (StringUtils.isNotBlank(fieldLabel)) {
            for (ISelectOption selectOption : fieldDescribe.getSelectOptions()) {
                if (selectOption.getLabel().startsWith(fieldLabel) && !category.contains(selectOption.getValue())) {
                    categoryList.add(selectOption.getValue());
                }
            }
        }
        return categoryList;
    }

    public void specialProductFilters(User user, List<IFilter> filters) {
        for (IFilter filter : filters) {
            String fieldApiName = filter.getFieldName();
            List fieldValue = filter.getFieldValues();
            if (CollectionUtils.isEmpty(fieldValue)) {
                break;
            }
            if ("category".equals(fieldApiName)) {
                List newFieldValueList = Lists.newArrayList();
                fieldValue.forEach(k -> setProductCategoryChildrenList(user, String.valueOf(k), newFieldValueList));
                filter.setFieldValues(newFieldValueList);
                filter.setOperator(Operator.IN);
                break;
            }
        }
    }


    @Nullable
    public IObjectData getStandardPriceBook(User user) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        List<IFilter> filters = Lists.newLinkedList();
        SearchUtil.fillFiltersWithUser(user, filters);
        SearchUtil.fillFilterEq(filters, PriceBookConstants.Field.ISSTANDARD.getApiName(), "true");
        searchQuery.setFilters(filters);
        // TODO: 2018/5/16 SA 需要价目表过滤
        QueryResult<IObjectData> result = serviceFacade.findBySearchQuery(user, PriceBookConstants.API_NAME, searchQuery);
        return result != null && CollectionUtils.isNotEmpty(result.getData()) ? result.getData().get(0) : null;
    }

    public boolean isStandardProduct(String tenantId, String priceBookId) {
        User user = new User(tenantId, DefObjConstants.SUPER_PRIVILEGE_USER_ID);
        IObjectData objectData = getStandardPriceBook(user);
        return objectData != null && priceBookId.equals(objectData.getId());
    }


    public IObjectData getPriceBookProduct(User user, String priceBookId, String productId) {
        List<IObjectData> list = getPriceBookProducts(user, priceBookId, Collections.singletonList(productId));
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据价目表id和产品id列表获取价目表产品列表
     */
    private List<IObjectData> getPriceBookProducts(User user, String priceBookId, List<String> productIds) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(productIds.size());
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFiltersWithUser(user, filters);
        SearchUtil.fillFilterEq(filters, "pricebook_id", priceBookId);
        if (CollectionUtils.isNotEmpty(productIds)) {
            SearchUtil.fillFilterIn(filters, "product_id", productIds);
        }
        searchQuery.addFilters(filters);

        QueryResult<IObjectData> priceBookProducts = serviceFacade.findBySearchQuery(
                user, Utils.PRICE_BOOK_PRODUCT_API_NAME, searchQuery);
        return priceBookProducts.getData();
    }
}
