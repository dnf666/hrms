package com.facishare.crm.promotion.predefine.action;

import com.facishare.paas.appframework.core.predef.action.StandardBulkDeleteAction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PromotionBulkDeleteAction extends StandardBulkDeleteAction {

    /*@Override
    protected Result doAct(Arg arg) {
        List<IObjectDescribe> detailObjectDescribeList = serviceFacade.findDetailDescribes(actionContext.getTenantId(), PromotionConstants.API_NAME);
        Map<String, IObjectDescribe> detailDescribeMap = detailObjectDescribeList.stream().collect(Collectors.toMap(IObjectDescribe::getApiName, objdescribe -> objdescribe));
        Map<String, List<IObjectData>> detailObjectDataMap = Maps.newHashMap();
        dataList.forEach(masterData -> {
            Map<String, List<IObjectData>> dataMap = findDetailObjectDataList(detailDescribeMap, masterData);
            dataMap.forEach((key, value) -> {
                if (detailObjectDataMap.containsKey(key)) {
                    detailObjectDataMap.get(key).addAll(value);
                } else {
                    detailObjectDataMap.put(key, value);
                }
            });
        });
        detailObjectDataMap.forEach((key, value) -> {
            List<IObjectData> deletedDatas = serviceFacade.bulkDelete(value, actionContext.getUser());
            this.serviceFacade.log(this.actionContext.getUser(), EventType.DELETE, ActionType.Delete, detailDescribeMap.get(key), deletedDatas);
        });
        super.doAct(arg);
        return StandardBulkDeleteAction.Result.builder().success(true).build();
    }
    
    private Map<String, List<IObjectData>> findDetailObjectDataList(Map<String, IObjectDescribe> detailObjectDescribeMap, IObjectData masterObjectData) {
        Map<String, List<IObjectData>> detailObjectDataMap = Maps.newHashMap();
        if (!detailObjectDescribeMap.isEmpty()) {
            detailObjectDescribeMap.forEach((key, value) -> {
                ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(value);
                Optional<MasterDetailFieldDescribe> masterDetailFieldDescribeOptional = objectDescribeExt.getMasterDetailFieldDescribe();
                TermConditions condition = new TermConditions();
                condition.addCondition(masterDetailFieldDescribeOptional.get().getApiName(), masterObjectData.getId());
                SearchQuery searchQuery = new SearchQuery();
                searchQuery.addCondition(condition);
                QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(actionContext.getUser(), key, searchQuery);
                if (CollectionUtils.isNotEmpty(queryResult.getData())) {
                    detailObjectDataMap.put(key, queryResult.getData());
                } else {
                    log.info("detail ApiName:{},no datas", key);
                }
            });
        }
        return detailObjectDataMap;
    }*/
}
