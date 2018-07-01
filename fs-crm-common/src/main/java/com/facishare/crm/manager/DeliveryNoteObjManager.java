package com.facishare.crm.manager;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.ObjectReferenceFieldDescribeBuilder;
import com.facishare.crm.describebuilder.QuoteFieldDescribeBuilder;
import com.facishare.crm.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.enums.DeliveryNoteSwitchEnum;
import com.facishare.crm.exception.DeliveryNoteBusinessException;
import com.facishare.crm.exception.DeliveryNoteErrorCode;
import com.facishare.crm.util.SearchUtil;
import com.facishare.crm.util.StockUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.describe.QuoteFieldDescribe;
import com.facishare.paas.metadata.impl.search.OrderBy;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.facishare.paas.metadata.ui.layout.IFormField;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.facishare.paas.metadata.ui.layout.ITableColumn;
import com.fxiaoke.bizconf.api.BizConfApi;
import com.fxiaoke.bizconf.arg.QueryConfigArg;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 发货单
 * Created by chenzs on 2018/1/17.
 */
@Service
@Slf4j
public class DeliveryNoteObjManager {
    private static final int MAX_LIMIT_FOR_QUERY_ALL = 10000000;

    @Autowired
    protected ServiceFacade serviceFacade;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private DeliveryNoteLayoutManager deliveryNoteLayoutManager;
    @Resource
    private BizConfApi bizConfApi;

    /**
     * 所有的发货单是否处于statusList状态（statusList要包含'已作废'状态）（因为下面使用的方法，无法查询'已作废'的数据）
     */
    public boolean isAllDeliveryNoteUnderStatus(User user, List<String> statusList) {
        if (CollectionUtils.isEmpty(statusList) || !statusList.contains(DeliveryNoteObjStatusEnum.INVALID.getStatus())) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.PARAMETER_ERROR, "参数必须包含invalid状态");
        }
        List<String> otherStatusList = DeliveryNoteObjStatusEnum.getAllStatus();
        otherStatusList.removeAll(statusList);
        List<IObjectData> deliveryNoteVOs = queryDeliveryNotesByTenantId(user, user.getTenantId(), otherStatusList);
        return CollectionUtils.isEmpty(deliveryNoteVOs);
    }

    /**
     * 订单所有的发货单是否处于statusList状态（statusList要包含'已作废'状态）（因为下面使用的方法，无法查询'已作废'的数据）
     */
    public boolean isAllDeliveryNoteUnderStatus(User user, String salesOrderId, List<String> statusList) {
        Preconditions.checkArgument(org.apache.commons.lang3.StringUtils.isNotEmpty(salesOrderId));

        if (CollectionUtils.isEmpty(statusList) || !statusList.contains(DeliveryNoteObjStatusEnum.INVALID.getStatus())) {
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.PARAMETER_ERROR, "参数必须包含invalid状态");
        }

        List<String> otherStatusList = DeliveryNoteObjStatusEnum.getAllStatus();
        otherStatusList.removeAll(statusList);

        List<IObjectData> deliveryNoteVOs = queryDeliveryNotesByTenantId(user, user.getTenantId(), salesOrderId, otherStatusList);
        return CollectionUtils.isEmpty(deliveryNoteVOs);
    }

    /**
     * 查询企业所有状态在statusList里面的发货单
     */
    public List<IObjectData> queryDeliveryNotesByTenantId(User user, String tenantId, List<String> statusList) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, com.facishare.crm.constants.SystemConstants.Field.TennantID.apiName, tenantId);
        SearchUtil.fillFilterIn(filters,  "status", statusList);
        return searchQuery(user, DeliveryNoteObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    /**
     * 查询订单所有状态在statusList里面的发货单
     */
    public List<IObjectData> queryDeliveryNotesByTenantId(User user, String tenantId, String salesOrderId, List<String> statusList) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, com.facishare.crm.constants.SystemConstants.Field.TennantID.apiName, tenantId);
        SearchUtil.fillFilterIn(filters,  DeliveryNoteObjConstants.Field.Status.apiName, statusList);
        SearchUtil.fillFilterEq(filters,  DeliveryNoteObjConstants.Field.SalesOrderId.apiName, salesOrderId);
        return searchQuery(user, DeliveryNoteObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
    }

    private QueryResult<IObjectData> searchQuery(User user, String objectApiName, List<IFilter> filters, List<OrderBy> orders, int offset, int limit) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        searchTemplateQuery.setOffset(offset);
        searchTemplateQuery.setLimit(limit);
        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setOrders(orders);
        searchTemplateQuery.setWheres(Lists.newArrayList());
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, objectApiName, searchTemplateQuery);
        return queryResult;
    }

    /**
     * 是否开启了"发货单"开关
     */
    public boolean isDeliveryNoteEnable(String tenantId) {
        try {
            String config = bizConfApi.queryConfig(QueryConfigArg.builder().key("delivery_note_status").tenantId(tenantId).pkg("CRM").build());
            if (StringUtils.isBlank(config)) {
                return false;
            }
            DeliveryNoteSwitchEnum deliveryNoteSwitchEnum = DeliveryNoteSwitchEnum.get(Integer.valueOf(config)).orElseThrow(() -> new ValidateException("DeliveryNoteStatus不合法"));
            return deliveryNoteSwitchEnum.getStatus() == DeliveryNoteSwitchEnum.OPENED.getStatus();
        } catch (Exception e) {
            log.warn("isDeliveryNoteEnable getConfig error,tenantId:{}", tenantId, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.QUERY_CONFIG_FAILED, e.getMessage());
        }
    }

    /**
     * 开启库存，添加字段
     */
    public void addFieldForOpenStock(User user) {
        //1、查询信息
        //1.1、查找"发货单"的describe
        IObjectDescribe deliveryNoteObjectDescribe = findByTenantIdAndDescribeApiName(user.getTenantId(), DeliveryNoteObjConstants.API_NAME);
        //1.2、查找"发货单产品"的describe
        IObjectDescribe deliveryNoteProductObjectDescribe = findByTenantIdAndDescribeApiName(user.getTenantId(), DeliveryNoteProductObjConstants.API_NAME);
        //1.3、查找"发货单"的layout
        List<ILayout> deliveryNoteLayouts = deliveryNoteLayoutManager.findByObjectDescribeApiNameAndTenantId(DeliveryNoteObjConstants.API_NAME, user.getTenantId());
        //1.4、查找"发货单产品"的layout
        List<ILayout> deliveryNoteProductLayouts = deliveryNoteLayoutManager.findByObjectDescribeApiNameAndTenantId(DeliveryNoteProductObjConstants.API_NAME, user.getTenantId());


        //2、describe+layout：添加字段（添加之前，判断是否已存在对应的字段）
        ILayout deliveryNoteObjDefaultLayout = null;
        ILayout deliveryNoteObjListLayout = null;
        for (ILayout layout : deliveryNoteLayouts) {
            if (Objects.equals(layout.getLayoutType(), SystemConstants.LayoutType.Detail.layoutType)) {
                deliveryNoteObjDefaultLayout = layout;
            } else if (Objects.equals(layout.getLayoutType(), SystemConstants.LayoutType.List.layoutType)) {
                deliveryNoteObjListLayout = layout;
            }
        }

        ILayout deliveryNoteProductObjDefaultLayout = null;
        ILayout deliveryNoteProductObjListLayout = null;
        for (ILayout layout : deliveryNoteProductLayouts) {
            if (Objects.equals(layout.getLayoutType(), SystemConstants.LayoutType.Detail.layoutType)) {
                deliveryNoteProductObjDefaultLayout = layout;
            } else if (Objects.equals(layout.getLayoutType(), SystemConstants.LayoutType.List.layoutType)) {
                deliveryNoteProductObjListLayout = layout;
            }
        }

        if (deliveryNoteObjDefaultLayout == null || deliveryNoteObjListLayout == null || deliveryNoteProductObjDefaultLayout == null || deliveryNoteProductObjListLayout == null) {
            log.warn("deliveryNoteObjDefaultLayout:{}, deliveryNoteObjListLayout:{}, deliveryNoteProductObjDefaultLayout:{}, deliveryNoteProductObjListLayout:{}",
                    deliveryNoteObjDefaultLayout, deliveryNoteObjListLayout, deliveryNoteProductObjDefaultLayout, deliveryNoteProductObjListLayout);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.LAYOUT_INFO_ERROR, DeliveryNoteErrorCode.LAYOUT_INFO_ERROR.getMessage());
        }

        //2.1、"发货单"添加"发货仓库"字段(describe)
        deliveryNoteAddField(deliveryNoteObjectDescribe);
        //2.2、"发货单产品"添加"库存"、"实际库存"字段(describe)
        deliveryNoteProductAddField(deliveryNoteProductObjectDescribe);

        //2.3、"发货单"添加"发货仓库"字段(layout)
        deliveryNoteLayoutAddField(deliveryNoteObjDefaultLayout, deliveryNoteObjListLayout);

        //2.4、"发货单产品"添加"实际库存"字段(layout)
        deliveryNoteProductLayoutAddField(deliveryNoteProductObjDefaultLayout, deliveryNoteProductObjListLayout);
    }

    private void deliveryNoteLayoutAddField(ILayout defaultLayout, ILayout listLayout) {
        String addFieldApiName = DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName;
        String afterFieldApiName = DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName;

        //detailLayout
        IFormField deliveryWarehouseIdFormField = deliveryNoteLayoutManager.getDeliveryNoteFormField(addFieldApiName);
        detailLayoutAddField(defaultLayout, addFieldApiName, deliveryWarehouseIdFormField, afterFieldApiName);

        //listLayout
        ITableColumn deliveryWarehouseIdTableColumn = deliveryNoteLayoutManager.getDeliveryNoteTableColumn(addFieldApiName);
        listLayoutAddField(listLayout, addFieldApiName, deliveryWarehouseIdTableColumn, afterFieldApiName);
    }

    private void deliveryNoteProductLayoutAddField(ILayout defaultLayout, ILayout listLayout) {
        String addFieldApiName = DeliveryNoteProductObjConstants.Field.RealStock.apiName;
        String afterFieldApiName = DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName;

        //detailLayout
        IFormField realStockFormField = deliveryNoteLayoutManager.getDeliveryNoteProductFormField(addFieldApiName);
        detailLayoutAddField(defaultLayout, addFieldApiName, realStockFormField, afterFieldApiName);

        //listLayout
        ITableColumn realStockTableColumn = deliveryNoteLayoutManager.getDeliveryNoteProductTableColumn(addFieldApiName);
        listLayoutAddField(listLayout, addFieldApiName, realStockTableColumn, afterFieldApiName);
    }

    private void detailLayoutAddField(ILayout defaultLayout, String addFieldApiName, IFormField addFormField, String afterFieldApiName) {
        Map<String, IFormField> addFieldApiName2FormFieldMap = new HashMap<>();
        addFieldApiName2FormFieldMap.put(addFieldApiName, addFormField);

        Map<String, String> addFieldApiName2afterFieldApiNameMap = new HashMap<>();
        addFieldApiName2afterFieldApiNameMap.put(addFieldApiName, afterFieldApiName);

        deliveryNoteLayoutManager.detailLayoutAddField(defaultLayout, Lists.newArrayList(addFieldApiName), addFieldApiName2FormFieldMap, addFieldApiName2afterFieldApiNameMap);
    }

    private void listLayoutAddField(ILayout defaultLayout, String addFieldApiName, ITableColumn addTableColumn, String afterFieldApiName) {
        Map<String, ITableColumn> addFieldApiName2TableColumnMap = new HashMap<>();
        addFieldApiName2TableColumnMap.put(addFieldApiName, addTableColumn);

        Map<String, String> addFieldApiName2afterFieldApiNameMap = new HashMap<>();
        addFieldApiName2afterFieldApiNameMap.put(addFieldApiName, afterFieldApiName);

        deliveryNoteLayoutManager.listLayoutAddField(defaultLayout, Lists.newArrayList(addFieldApiName), addFieldApiName2TableColumnMap, addFieldApiName2afterFieldApiNameMap);
    }

    /**
     * "发货单"添加"发货仓库"字段(describe)
     */
    public void deliveryNoteAddField(IObjectDescribe objectDescribe) {
        //获取字段定义
        List<IFieldDescribe> fieldDescribes = objectDescribe.getFieldDescribes();

        //判断是否有"发货仓库"字段
        for (IFieldDescribe f : fieldDescribes) {
            if (Objects.equals(f.getApiName(), DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName)) {
                return;
            }
        }

        ObjectReferenceFieldDescribe deliveryWarehouseId = ObjectReferenceFieldDescribeBuilder.builder().apiName(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.apiName)
                .label(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.label).targetApiName("WarehouseObj")
                .targetRelatedListLabel(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.targetRelatedListLabel)
                .targetRelatedListName(DeliveryNoteObjConstants.Field.DeliveryWarehouseId.targetRelatedListName).unique(false).required(false).wheres(StockUtil.getEnableWarehouseWheres()).build();
        objectDescribe.addFieldDescribe(deliveryWarehouseId);

        //replace
        replace(objectDescribe);
    }

    /**
     * "发货单产品"添加"库存"、"实际库存"字段(describe)
     */
    public void deliveryNoteProductAddField(IObjectDescribe objectDescribe) {
        //获取字段定义
        List<IFieldDescribe> fieldDescribes = objectDescribe.getFieldDescribes();

        //判断是否有"库存"字段
        boolean hasStockIdField = false;
        for (IFieldDescribe f : fieldDescribes) {
            if (Objects.equals(f.getApiName(), DeliveryNoteProductObjConstants.Field.StockId.apiName)) {
                hasStockIdField = true;
                break;
            }
        }

        //判断是否有"实际库存"字段
        boolean hasRealStockField = false;
        for (IFieldDescribe f : fieldDescribes) {
            if (Objects.equals(f.getApiName(), DeliveryNoteProductObjConstants.Field.RealStock.apiName)) {
                hasRealStockField = true;
                break;
            }
        }

        if (!hasStockIdField) {
            ObjectReferenceFieldDescribe stockId = ObjectReferenceFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.StockId.apiName).label(DeliveryNoteProductObjConstants.Field.StockId.label).targetApiName("StockObj").targetRelatedListLabel(DeliveryNoteProductObjConstants.Field.StockId.targetRelatedListLabel).targetRelatedListName(DeliveryNoteProductObjConstants.Field.StockId.targetRelatedListName).unique(false).required(false).build();
            objectDescribe.addFieldDescribe(stockId);
        }

        if (!hasRealStockField) {
            QuoteFieldDescribe realStock = QuoteFieldDescribeBuilder.builder().apiName(DeliveryNoteProductObjConstants.Field.RealStock.apiName).label(DeliveryNoteProductObjConstants.Field.RealStock.label).unique(false).required(false).quoteField(DeliveryNoteProductObjConstants.Field.StockId.apiName.concat("__r.real_stock")).quoteFieldType("number").build();
            objectDescribe.addFieldDescribe(realStock);
        }

        //replace
        if (!hasStockIdField || !hasRealStockField) {
            replace(objectDescribe);
        }
    }

    public IObjectDescribe findByTenantIdAndDescribeApiName(String tenantId, String describeApiName) {
        IObjectDescribe objectDescribe;
        try {
            IObjectDescribe result = objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, describeApiName);
            log.info("objectDescribeService.findByTenantIdAndDescribeApiName success, tenantId:{}, objectDescribeApiName:{}, result:{}", tenantId, describeApiName, result);
        } catch (MetadataServiceException e) {
            log.warn("objectDescribeService.findByTenantIdAndDescribeApiName failed ,tenantId:{}, objectDescribeApiName:{}", tenantId, describeApiName, e);
            throw new DeliveryNoteBusinessException(() -> e.getErrorCode().getCode(), "查询定义信息失败，" + e.getMessage());
        }
        return objectDescribe;
    }

    /**
     * 更新定义
     */
    public void replace(IObjectDescribe objectDescribe) {
        try {
            IObjectDescribe result = objectDescribeService.replace(objectDescribe,  false);
            log.info("objectDescribeService.replace success, objectDescribe:{}, active:{}, isAllowLabelRepeat:{}, result:{}", objectDescribe, true, false, result);
        } catch (MetadataServiceException e) {
            log.warn("objectDescribeService.replace failed, objectDescribe:{} ,active:{} ,isAllowLabelRepeat:{}", objectDescribe, true, false, e);
            throw new DeliveryNoteBusinessException(() -> e.getErrorCode().getCode(), "更新对象定义信息失败，" + e.getMessage());
        } catch (Exception e) {
            log.warn("objectDescribeService.replace failed, objectDescribe:{} ,active:{} ,isAllowLabelRepeat:{}", objectDescribe, true, false, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.REPLACE_DESCRIBE_FAILED, "更新对象定义信息失败，" + e.getMessage());
        }
    }
}