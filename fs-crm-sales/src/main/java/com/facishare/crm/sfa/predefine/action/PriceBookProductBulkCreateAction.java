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
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.IRule;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceBookProductBulkCreateAction extends BaseObjectBulkSaveAction {
    @Override
    protected String getIRule() {
        return IRule.CREATE;
    }

    @Override
    protected ObjectAction getObjectAction() {
        return ObjectAction.CREATE;
    }

    @Override
    public void validate() {
        stopWatch.lap("validate start");
        super.validate();
        stopWatch.lap("super validate ");
        //验证是否价目表产品是否重复
        validateRepeat();
        stopWatch.lap("validateRepeat ");
    }


    protected List<String> getFuncPrivilegeCodes() {
        return StandardAction.Add.getFunPrivilegeCodes();
    }

    @Override
    protected List<String> getDataPrivilegeIds(BaseObjectBulkSaveAction.Arg arg) {
        return null;
    }


    @Override
    protected Result doAct(Arg arg) {
        //在正式更新从对象之前,修改从对象的objectData中的一些值,比如修改时间、业务类型、signIn字段的默认值等。
        batchModifyObjectDataBeforeCreate(this.objectDataList, objectDescribe);

        // TODO: 2017/11/11 核心逻辑等郑磊实现底层后补充
        stopWatch.lap("bulkSaveObjectData start");
        List<IObjectData> resultList = Lists.newLinkedList();
        if (CollectionUtils.isNotEmpty(objectDataList)) {
            objectDataList = serviceFacade.bulkSaveObjectData(objectDataList, this.getActionContext().getUser());
            objectDataList.forEach(k -> resultList.add(k));
        }
        stopWatch.lap("bulkSaveObjectData finished");
        return Result.builder().dataList(resultList.stream().map(k -> ObjectDataDocument.of(k)).collect(Collectors.toList())).build();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        doAfter();
        return result;
    }

    public void doAfter() {
        log.debug("PriceBookProductBulkCreateAction after result.getDataToAdd():{}", this.objectDataList);
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
        if (queryResult.getTotalNumber() > 0) {
            throw new ValidateException(String.format("当前价目表下，产品已存在"));
        }
    }

    protected void recordLog() {
        Map<String, IObjectDescribe> objectDescribes = Maps.newHashMap();
        objectDescribes.put(objectDescribe.getApiName(), objectDescribe);
        if (CollectionUtils.isNotEmpty(objectDataList)) {
            logAsync(objectDescribes, objectDataList, EventType.ADD, ActionType.Add);
        }
    }
}
