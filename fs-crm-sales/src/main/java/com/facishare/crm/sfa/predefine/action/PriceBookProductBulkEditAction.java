package com.facishare.crm.sfa.predefine.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.constant.PriceBookConstants;
import com.facishare.paas.appframework.common.util.ObjectAction;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardAction;
import com.facishare.paas.appframework.log.ActionType;
import com.facishare.paas.appframework.log.EventType;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.common.util.BulkOpResult;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.IRule;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceBookProductBulkEditAction extends BaseObjectBulkSaveAction {
    @Override
    protected String getIRule() {
        return IRule.UPDATE;
    }

    @Override
    protected ObjectAction getObjectAction() {
        return ObjectAction.UPDATE;
    }

    public void validate() {
        stopWatch.lap("validate start");
        super.validate();
        //验证是否价目表产品是否重复
        stopWatch.lap("super validate ");
        validateRepeat();
        stopWatch.lap("validateRepeat ");
        //根据objectData的生命状态和锁定状态来判断数据是否有权限进行该操作
        batchCheckActionByLockStatusAndLifeStatus(this.objectDataList, objectDescribe);
        stopWatch.lap("objectDataList checkActionByLockStatusAndLifeStatus ");
    }

    protected List<String> getFuncPrivilegeCodes() {
        return StandardAction.Edit.getFunPrivilegeCodes();
    }

    @Override
    protected List<String> getDataPrivilegeIds(Arg arg) {
        return arg.dataList.stream().map(k -> k.get(IObjectData.ID).toString()).collect(Collectors.toList());
    }

    @Override
    protected Result doAct(Arg arg) {
        //设置默认业务类型
        ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(objectDescribe);
        batchSetDefaultRecordType(objectDataList, objectDescribeExt);

        // TODO: 2017/11/11 核心逻辑等郑磊实现底层后补充
        stopWatch.lap("parallelBulkUpdateObjectData start");
        List<IObjectData> resultList = Lists.newLinkedList();
        if (CollectionUtils.isNotEmpty(objectDataList)) {
            BulkOpResult bulkOpResult = serviceFacade.parallelBulkUpdateObjectData(this.getActionContext().getUser(), objectDataList, false, null);
            bulkOpResult.getSuccessObjectDataList().forEach(k -> resultList.add(k));
        }
        stopWatch.lap("parallelBulkUpdateObjectData finished");
        return Result.builder().dataList(resultList.stream().map(k -> ObjectDataDocument.of(k)).collect(Collectors.toList())).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        doAfter();
        return result;
    }

    public void doAfter() {
        log.debug("PriceBookProductBulkCreateAction after ,result.getDataToUpdate:{}", this.objectDataList);
        //批量处理记录审计日志
        recordLog();
    }

    private void validateRepeat() {
        List<String> productIds = Lists.newLinkedList();
        Map<String, IObjectData> dataProductMap = Maps.newHashMap();
        String priceBookId = objectDataList.get(0).get("pricebook_id").toString();
        // TODO: 2017/11/10 首先需要判断参数列表中，productid是否重复
        for (IObjectData objData : objectDataList) {
            String productId = objData.get("product_id").toString();
            Object objPriceBookId = objData.get("pricebook_id");
            if (dataProductMap.containsKey(productId)) {
                throw new ValidateException("产品重复添加！");
            }
            if (objPriceBookId == null || StringUtils.isBlank(objPriceBookId.toString())) {
                throw new ValidateException("价目表不存在！");
            }
            if (!priceBookId.equals(objPriceBookId.toString())) {
                throw new ValidateException("不能添加产品在不同的价目表中！");
            }
            dataProductMap.put(productId, objData);
            productIds.add(productId);
            if (Objects.isNull(objData.getId())) {
                throw new ValidateException("当前是更新操作，主键不存在！");
            }
        }

        // TODO: 2017/11/10 判断参数列表中，和数据库中产品做比对，如果有，则必须是更新且更新的主键id必须相同
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(productIds.size());
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFiltersWithUser(actionContext.getUser(), filters);
        SearchUtil.fillFilterEq(filters, "pricebook_id", priceBookId);
        SearchUtil.fillFilterIn(filters, "product_id", productIds);
        searchQuery.setFilters(filters);

        QueryResult<IObjectData> queryResult = this.serviceFacade.findBySearchQuery(this.getActionContext().getUser(), PriceBookConstants.API_NAME_PRODUCT, searchQuery);
        for (IObjectData objData : queryResult.getData()) {
            IObjectData dataProduct = dataProductMap.get(objData.get("product_id").toString());
            if (dataProduct != null && !objData.getId().equals(dataProduct.getId())) {
                throw new ValidateException(String.format("当前价目表下，产品[%s]已存在", objData.get(PriceBookConstants.ProductField.PRODUCTNAME.getApiName())));
            }
        }
    }

    private void batchCheckActionByLockStatusAndLifeStatus(List<IObjectData> objectDataList, IObjectDescribe objectDescribe) {
        CountDownLatch latch = new CountDownLatch(objectDataList.size());
        objectDataList.forEach(objectData -> {
            serviceFacade.checkActionByLockStatusAndLifeStatus(objectData, ObjectAction.UPDATE, actionContext.getUser(), objectDescribe
                    .getApiName(), false);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("checkActionByLockStatusAndLifeStatus list,CountDownLatch error.", e);
        }
    }

    protected void recordLog() {
        Map<String, IObjectDescribe> objectDescribes = Maps.newHashMap();
        objectDescribes.put(objectDescribe.getApiName(), objectDescribe);
        if (CollectionUtils.isNotEmpty(objectDataList)) {
            logAsync(objectDescribes, objectDataList, EventType.MODIFY, ActionType.Modify);
        }
    }
}
