package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.openapi.Utils;
import com.facishare.crm.sfa.predefine.service.model.BatchGetProductModel;
import com.facishare.crm.sfa.predefine.service.model.PagingGetProductsModel;
import com.facishare.crm.sfa.predefine.service.model.SameGroupProductModel;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.proxy.ProductProxy;
import com.facishare.crm.sfa.utilities.proxy.model.BatchGetProductInfoModel;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.exception.APPException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.metadata.api.DBRecord;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.service.impl.ObjectDataServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by luxin on 2017/11/24.
 */
@ServiceModule("dht_pricebook")
@Component
@Slf4j
public class DhtPriceBookService {
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private ProductProxy productProxy;
    @Autowired
    private ObjectDataServiceImpl objectDataService;


    @ServiceMethod("batch_get_product")
    public BatchGetProductModel.Result batchGetProduct(BatchGetProductModel.Arg arg, ServiceContext context) {
        return new BatchGetProductModel.Result(ObjectDataDocument.ofList(getRichProducts(arg.getPriceBookId(), arg.getProductIds(), context)));
    }

    @ServiceMethod("paging_get_products")
    public PagingGetProductsModel.Result pagingGetProducts1(PagingGetProductsModel.Arg arg, ServiceContext context) {
        if (StringUtils.isNotBlank(arg.getCategory())) {
            return getResultByCategory(arg, context);
        } else {
            List<IObjectData> productDataList = getProductQueryResult(context, arg).getData();

            if (CollectionUtils.isNotEmpty(productDataList)) {
                List<String> noGroupIdProductIds = Lists.newArrayList();
                List<IObjectData> haveGroupIdProductInfoList = Lists.newArrayList();
                preprocessingProductRequestParams(productDataList, noGroupIdProductIds, haveGroupIdProductInfoList);

                List<IObjectData> resultTmp = Lists.newArrayList();
                for (IObjectData objectData : haveGroupIdProductInfoList) {
                    resultTmp.addAll(getSameGroupProductsByGroupId(context, arg.getPriceBookId(), objectData.get("product_group_id").toString()));
                }
                List<IObjectData> noGroupIdProducts = getRichProducts(arg.getPriceBookId(), noGroupIdProductIds, context);

                resultTmp.addAll(noGroupIdProducts);
                if (StringUtils.isNotBlank(arg.getBarCode())) {
                    List<IObjectData> iObjectDataList = Lists.newArrayList();
                    resultTmp.forEach(o -> {
                        if (arg.getBarCode().equals(o.get("barcode"))) {
                            iObjectDataList.add(o);
                        }
                    });
                    return new PagingGetProductsModel.Result(ObjectDataDocument.ofList(iObjectDataList), getProductCount(context, arg));
                }
                if (StringUtils.isNotBlank(arg.getName())) {
                    List<IObjectData> iObjectDataList = Lists.newArrayList();
                    resultTmp.forEach(o -> {
                        if (o.get("name").toString().toUpperCase().contains(arg.getName().toUpperCase())) {
                            iObjectDataList.add(o);
                        }
                    });

                    return getResult(arg, iObjectDataList);
                }
                return new PagingGetProductsModel.Result(ObjectDataDocument.ofList(resultTmp), getProductCount(context, arg));
            }
            return new PagingGetProductsModel.Result(Lists.newArrayList(), 0);

        }

    }

    @NotNull
    private PagingGetProductsModel.Result getResult(PagingGetProductsModel.Arg arg, List<IObjectData> iObjectDataList) {
        Map<String, List<IObjectData>> iObjectDataMapByGroupID = Maps.newHashMap();
        iObjectDataList.forEach(o -> {
            if (Strings.isNullOrEmpty(o.get("product_group_id").toString())) {
                List<IObjectData> objectDataList = Lists.newArrayList();
                objectDataList.add(o);
                iObjectDataMapByGroupID.put(o.get("_id").toString(), objectDataList);
            } else if (iObjectDataMapByGroupID.containsKey(o.get("product_group_id").toString())) {
                iObjectDataMapByGroupID.get(o.get("product_group_id").toString()).add(o);
            } else {
                List<IObjectData> objectDataList = Lists.newArrayList();
                objectDataList.add(o);
                iObjectDataMapByGroupID.put(o.get("product_group_id").toString(), objectDataList);
            }
        });
        Set keys = iObjectDataMapByGroupID.keySet();
        int size = keys.size();
        if (arg.getOffset() == 0 && arg.getLimit() > size) {
            return new PagingGetProductsModel.Result(ObjectDataDocument.ofList(iObjectDataList), size);
        } else {
            List<IObjectData> iObjectDataListResult = Lists.newArrayList();
            int i = 0;
            for (Map.Entry<String, List<IObjectData>> entry : iObjectDataMapByGroupID.entrySet()) {
                if (i++ >= arg.getOffset() && iObjectDataListResult.size() < arg.getLimit()) {
                    iObjectDataListResult.addAll(entry.getValue());
                }
            }
            return new PagingGetProductsModel.Result(ObjectDataDocument.ofList(iObjectDataListResult), size);
        }
    }

    @NotNull
    private PagingGetProductsModel.Result getResultByCategory(PagingGetProductsModel.Arg arg, ServiceContext context) {
        List<IObjectData> sameCategoryProducts = getProductsByCategory(context, arg.getPriceBookId(), arg);

        Map<String, List<IObjectData>> groupId2Products = Maps.newLinkedHashMap();
        int flag = 0;

        for (IObjectData product : sameCategoryProducts) {
            String productGroupId = product.get("product_group_id", String.class);
            if (StringUtils.isBlank(productGroupId)) {
                groupId2Products.put("no_group_id" + flag++, Lists.newArrayList(product));
            } else {
                if (groupId2Products.get(productGroupId) != null) {
                    groupId2Products.get(productGroupId).add(product);
                } else {
                    groupId2Products.put(productGroupId, Lists.newArrayList(product));
                }
            }
        }
        List<String> groupId2ProductsKeyList = Lists.newArrayList(groupId2Products.keySet());

        List<IObjectData> resultProducts = Lists.newArrayList();
        for (int i = arg.getOffset(); i<arg.getOffset()+arg.getLimit()&&i < groupId2ProductsKeyList.size(); i++) {
            resultProducts.addAll(groupId2Products.get(groupId2ProductsKeyList.get(i)));
        }

        List<String> productIds = Lists.newArrayList();
        resultProducts.forEach(o -> productIds.add(o.getId()));
        List<IObjectData> noGroupIdProducts = getRichProducts(arg.getPriceBookId(), productIds, context);
        return new PagingGetProductsModel.Result(ObjectDataDocument.ofList(noGroupIdProducts), groupId2ProductsKeyList.size());
    }


    private List<IObjectData> getProductsByCategory(ServiceContext context, @NotNull String priceBookId, @NotNull PagingGetProductsModel.Arg arg) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setPermissionType(0);
        searchQuery.setOffset(0);
        searchQuery.setLimit(1000);
        List<IFilter> filters = Lists.newArrayList();

        if (StringUtils.isNotBlank(arg.getBarCode())) {
            SearchUtil.fillFilterEq(filters, "barcode", arg.getBarCode());
        }
        SearchUtil.fillFilterEq(filters, "category", arg.getCategory());
        searchQuery.addFilters(filters);
        IActionContext iActionContext = ActionContextExt.of(context.getUser()).pgDbType().getContext();
        iActionContext.setDbType("rest");
        iActionContext.setPrivilegeCheck(Boolean.FALSE);
        List<IObjectData> products = serviceFacade.findBySearchQuery(iActionContext, Utils.PRODUCT_API_NAME, searchQuery).getData();

        if (StringUtils.isNotBlank(arg.getName())) {
            products = products.stream().filter(o -> o.getName().toUpperCase().contains(arg.getName().toUpperCase())).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(products)) {
            List<String> productIds = products.stream().map(DBRecord::getId).collect(Collectors.toList());
            return getRichProducts(priceBookId, products, productIds, context);
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }


    /**
     * 预处理获取产品信息的请求采参数
     */
    private void preprocessingProductRequestParams(List<IObjectData> productDataList, List<String> noGroupIdProductIds, List<IObjectData> haveGroupIdProductInfoList) {
        productDataList.forEach(o -> {
            if (o.get("max") != null) {
                Object groupId = o.get("product_group_id");
                if (groupId != null && StringUtils.isNotBlank(groupId.toString())) {
                    haveGroupIdProductInfoList.add(o);
                } else {
                    noGroupIdProductIds.add(o.get("max").toString());
                }
            }
        });
    }


    private QueryResult<IObjectData> getProductQueryResult(ServiceContext context, PagingGetProductsModel.Arg arg) {
        String sql = getSql(arg, context.getTenantId());
        QueryResult<IObjectData> productQueryResult;
        try {
            productQueryResult = objectDataService.findBySql(sql, context.getTenantId(), Utils.PRODUCT_API_NAME);
        } catch (MetadataServiceException e) {
            log.error("metadata findBySql error. sql ", sql);
            throw new APPException("system error.");
        }
        return productQueryResult;
    }

    private int getProductCount(ServiceContext context, PagingGetProductsModel.Arg arg) {
        String sql = getCountSql(arg, context.getTenantId());
        QueryResult<IObjectData> productQueryResult;
        try {
            productQueryResult = objectDataService.findBySql(sql, context.getTenantId(), Utils.PRODUCT_API_NAME);
        } catch (MetadataServiceException e) {
            log.error("metadata findBySql error. sql ", sql);
            throw new APPException("system error.");
        }
        return productQueryResult.getData().size();
    }


    private String getSql(PagingGetProductsModel.Arg arg, String tenantId) {
        return "SELECT MAX (A.ID),product_group_id FROM product A RIGHT JOIN price_book_product b ON A . ID = b.product_id" +
                String.format(" and b.pricebook_id='%s' ", arg.getPriceBookId()) + "AND A .package = b.package AND A .tenant_id = b.tenant_id where" +
                String.format(" A .tenant_id ='%s' ", tenantId) + String.format(" and A .package='%s' ", "CRM") +
                (arg.getBarCode() == null ? "" : String.format(" and A .barcode='%s' ", arg.getBarCode())) +
                (arg.getCategory() == null ? "" : String.format(" and A . category='%s' ", arg.getCategory())) +
                //(arg.getName() == null ? "" : String.format(" and A . name like '%s%s%s' ", "%",arg.getName(),"%")) +
                "and A .is_deleted = 0 and b.is_deleted = 0 and A .product_status='1'" +
                "GROUP BY\n" +
                "\tREPLACE (\n" +
                "\t\tA . NAME,\n" +
                "\t\tCOALESCE ('[' || product_spec || ']', ''),\n" +
                "\t\t''\n" +
                "\t),\n" +
                "\tA .product_group_id\n" +
                "LIMIT " + (arg.getName() == null ? arg.getLimit() : "10000") +
                "OFFSET " + arg.getOffset() + ";";
    }


    private String getCountSql(PagingGetProductsModel.Arg arg, String tenantId) {
        return "SELECT COUNT(1) OVER() FROM product A RIGHT JOIN price_book_product b ON A . ID = b.product_id" +
                String.format(" and b.pricebook_id='%s' ", arg.getPriceBookId()) + "AND A .package = b.package AND A .tenant_id = b.tenant_id where" +
                String.format(" A .tenant_id ='%s' ", tenantId) + String.format(" and A .package='%s' ", "CRM") +
                (arg.getBarCode() == null ? "" : String.format(" and A .barcode='%s' ", arg.getBarCode())) +
                (arg.getName() == null ? "" : String.format(" and A . NAME like '%s%s%s' ", "%", arg.getName(), "%")) +
                (arg.getCategory() == null ? "" : String.format(" and A . category='%s' ", arg.getCategory())) +
                "and A .is_deleted = 0 and b.is_deleted = 0 and A .product_status='1'" +
                "GROUP BY\n" +
                "\tREPLACE (\n" +
                "\t\tA . NAME,\n" +
                "\t\tCOALESCE ('[' || product_spec || ']', ''),\n" +
                "\t\t''\n" +
                "\t),\n" +
                "\tA .product_group_id\n" + ";";
    }


    @ServiceMethod("get_same_group_products")
    public SameGroupProductModel.Result getSameGroupProducts(SameGroupProductModel.Arg arg, ServiceContext context) {

        String priceBookId = arg.getPriceBookId();
        String productId = arg.getProductId();
        IObjectData product = serviceFacade.findObjectData(getActionContext4Rest(context), productId, Utils.PRODUCT_API_NAME);
        if (product != null) {
            if (product.get("product_group_id") != null && StringUtils.isNotBlank(product.get("product_group_id").toString())) {
                return new SameGroupProductModel.Result(ObjectDataDocument.ofList(getSameGroupProductsByGroupId(context, priceBookId, product.get("product_group_id").toString())));
            } else {
                return new SameGroupProductModel.Result(ObjectDataDocument.ofList(getRichProducts(priceBookId, Lists.newArrayList(product), Lists.newArrayList(productId), context)));
            }
        }
        return new SameGroupProductModel.Result(Lists.newArrayList());
    }

    /**
     * 根据一个产品查询出同一个组的所有产品,返回值限制为1000个,并将价目表产品的信息增加到对应的产品上
     *
     * @param priceBookId    not null
     * @param productGroupId not null
     * @return 添加了价目表产品信息的产品
     */
    private List<IObjectData> getSameGroupProductsByGroupId(ServiceContext context, @NotNull String priceBookId, @NotNull String productGroupId) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setPermissionType(0);
        searchQuery.setOffset(0);
        searchQuery.setLimit(1000);
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, "product_group_id", productGroupId);
        searchQuery.addFilters(filters);
        IActionContext iActionContext = ActionContextExt.of(context.getUser()).getContext();
        iActionContext.setDbType("rest");
        iActionContext.setPrivilegeCheck(Boolean.FALSE);
        List<IObjectData> products = serviceFacade.findBySearchQuery(iActionContext, Utils.PRODUCT_API_NAME, searchQuery).getData();

        if (CollectionUtils.isNotEmpty(products)) {
            List<String> productIds = products.stream().map(DBRecord::getId).collect(Collectors.toList());
            return getRichProducts(priceBookId, products, productIds, context);
        } else {
            return Lists.newArrayListWithCapacity(0);
        }
    }


    @NotNull
    private List<IObjectData> getRichProducts(String priceBookId, List<IObjectData> products, List<String> productIds, ServiceContext context) {
        List<IObjectData> priceBookProducts = getPriceBookProducts(context.getUser(), priceBookId, productIds);
        if (CollectionUtils.isNotEmpty(priceBookProducts)) {
            return getRichProductsByProductsAndPriceBookProducts(products, priceBookProducts, context);
        }
        return Lists.newArrayList();
    }


    @NotNull
    private List<IObjectData> getRichProducts(String priceBookId, List<String> productIds, ServiceContext context) {
        List<IObjectData> priceBookProducts = getPriceBookProducts(context.getUser(), priceBookId, productIds);
        if (CollectionUtils.isNotEmpty(priceBookProducts)) {
            return getRichProductsByPriceBookProducts(productIds, priceBookProducts, context);
        }
        return Lists.newArrayList();
    }


    /**
     * 获取加上价目表产品信息的产品对象列表
     */
    @NotNull
    private List<IObjectData> getRichProductsByProductsAndPriceBookProducts(List<IObjectData> products, List<IObjectData> priceBookProducts, ServiceContext context) {
        Map<String, IObjectData> productId2PriceBookProduct = Maps.newHashMap();

        List<String> productIds = Lists.newArrayList();
        fillParam(priceBookProducts, productId2PriceBookProduct, productIds);

        Map<Object, Object> productInfoResult = null;

        List<Map<String, Object>> productInfoList = getProductInfoList(context, productIds);
        if (CollectionUtils.isNotEmpty(productInfoList)) {
            productInfoResult = productInfoList.stream().collect(Collectors.toMap(o -> o.get("ProductID"), o -> o.get("ProductSpecKVs")));
        }

        fillProducts(products, productId2PriceBookProduct, productInfoResult);
        return products;
    }


    private List<Map<String, Object>> getProductInfoList(ServiceContext context, List<String> productIds) {
        List<Map<String, Object>> tmp = Lists.newArrayList();

        for (int i = 0; i < productIds.size(); i = i + 200) {
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");
            headers.put("x-fs-ei", context.getTenantId());
            headers.put("x-fs-userInfo", context.getUser().getUserId());

            BatchGetProductInfoModel.Result batchGetProductInfo = productProxy.batchGetProductInfo(productIds.subList(i, i + 200 > productIds.size() ? productIds.size() : i + 200), headers);
            if (batchGetProductInfo != null && batchGetProductInfo.getSuccess() && batchGetProductInfo.getValue() != null) {
                tmp.addAll(batchGetProductInfo.getValue());
            }
        }
        return tmp;
    }


    private void fillProducts(List<IObjectData> products, Map<String, IObjectData> productId2PriceBookProduct, Map<Object, Object> productInfoResult) {
        Iterator<IObjectData> productIterator = products.iterator();
        while (productIterator.hasNext()) {
            IObjectData product = productIterator.next();
            if (product.getId() != null) {
                String productId = product.getId();
                IObjectData priceBookProduct = productId2PriceBookProduct.get(productId);
                if (priceBookProduct == null) {
                    productIterator.remove();
                } else {
                    if (productInfoResult != null && productInfoResult.get(productId) != null) {
                        product.set("ProductSpecKVs", productInfoResult.get(productId));
                    }
                    product.set("pricebookprod_code", priceBookProduct.get("pricebookprod_code"));
                    product.set("pricebook_price", priceBookProduct.get("pricebook_price"));
                    product.set("pricebook_product_id", priceBookProduct.getId());
                    product.set("pricebook_product_discount", priceBookProduct.get("discount"));
                }
            }
        }
    }


    /**
     * 获取加上价目表产品信息的产品对象列表
     */
    @NotNull
    private List<IObjectData> getRichProductsByPriceBookProducts(List<String> productIds, List<IObjectData> priceBookProducts, ServiceContext context) {
        Map<String, IObjectData> productId2PriceBookProduct = Maps.newHashMap();
        List<String> productIds1 = Lists.newArrayList();
        fillParam(priceBookProducts, productId2PriceBookProduct, productIds1);

        Map<Object, Object> productInfoResult = null;
        List<Map<String, Object>> productInfoList = getProductInfoList(context, productIds1);
        if (CollectionUtils.isNotEmpty(productInfoList)) {
            productInfoResult = productInfoList.stream().collect(Collectors.toMap(o -> o.get("ProductID"), o -> o.get("ProductSpecKVs")));
        }

        List<IObjectData> products = serviceFacade.findObjectDataByIds(getActionContext4Rest(context), productIds, Utils.PRODUCT_API_NAME);
        fillProducts(products, productId2PriceBookProduct, productInfoResult);
        return products;
    }

    private void fillParam(List<IObjectData> priceBookProducts, Map<String, IObjectData> productId2PriceBookProduct, List<String> productIds) {
        priceBookProducts.forEach(o -> {
            String productId = o.get("product_id", String.class);

            if (StringUtils.isNotBlank(productId)) {
                productId2PriceBookProduct.put(productId, o);
                productIds.add(productId);
            }
        });
    }


    /**
     * 根据价目表id和产品id列表获取价目表产品列表
     */
    @SuppressWarnings("Duplicates")
    private List<IObjectData> getPriceBookProducts(User user, String priceBookId, List<String> productIds) {
        // TODO: 2018/5/16 SA 需要价目表过滤
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(productIds.size());
        List<IFilter> filters = Lists.newArrayList();
        searchQuery.setPermissionType(0);
        SearchUtil.fillFiltersWithUser(user, filters);
        SearchUtil.fillFilterEq(filters, "pricebook_id", priceBookId);
        if (CollectionUtils.isNotEmpty(productIds)) {
            SearchUtil.fillFilterIn(filters, "product_id", productIds);
        }
        searchQuery.addFilters(filters);

        QueryResult<IObjectData> priceBookProducts = serviceFacade.findBySearchQuery(getActionContext(user)
                , Utils.PRICE_BOOK_PRODUCT_API_NAME, searchQuery);
        return priceBookProducts.getData();
    }


    private IActionContext getActionContext(User user) {
        IActionContext actionContext = ActionContextExt.of(user).getContext();
        actionContext.setDbType("pg");
        //不验证功能权限和数据权限
        actionContext.setPrivilegeCheck(Boolean.FALSE);
        return actionContext;
    }


    private IActionContext getActionContext4Rest(ServiceContext context) {
        IActionContext actionContext = ActionContextExt.of(context.getUser()).getContext();
        actionContext.setDbType("rest");
        //不验证功能权限和数据权限
        actionContext.setPrivilegeCheck(Boolean.FALSE);
        return actionContext;
    }


}
