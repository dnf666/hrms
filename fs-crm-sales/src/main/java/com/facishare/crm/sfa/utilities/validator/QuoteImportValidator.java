package com.facishare.crm.sfa.utilities.validator;

import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.predefine.service.model.ValidImportSalesOrder;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.paas.appframework.core.model.ActionContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseImportAction;
import com.facishare.paas.appframework.core.predef.action.BaseImportDataAction;
import com.facishare.paas.appframework.metadata.ActionContextExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.action.IActionContext;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class QuoteImportValidator {

    /**
     * 校验价目表是否适用当前客户
     *
     * @param errorList
     * @param dataList
     */
    public static void validateAccountPriceBook(ActionContext actionContext,
                                                PriceBookCommonService priceBookCommonService,
                                                List<BaseImportAction.ImportError> errorList,
                                                List<BaseImportDataAction.ImportData> dataList) {

        List<ValidImportSalesOrder.ValidPriceBookImportInfo> importInfos = new ArrayList<>();
        dataList.forEach(data -> {
            IObjectData objectData = data.getData();
            if (objectData.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName()) != null) {
                ValidImportSalesOrder.ValidPriceBookImportInfo importInfo = new ValidImportSalesOrder.ValidPriceBookImportInfo();
                importInfo.setRowNo(data.getRowNo());
                importInfo.setCustomerId(String.valueOf(objectData.get(QuoteConstants.QuoteField.ACCOUNTID.getApiName())));
                importInfo.setPriceBookId(String.valueOf(objectData.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName())));
                importInfos.add(importInfo);
            }
        });

        log.debug("validateAccountPriceBook.arg:{}", importInfos);
        if (!importInfos.isEmpty()) {
            List<ValidImportSalesOrder.ValidPriceBookImportInfo> validateResult = priceBookCommonService
                    .validateAccountPriceBook(actionContext.getUser(), importInfos);
            log.debug("validateAccountPriceBook.result:{}", validateResult);
            if (!validateResult.isEmpty()) {
                validateResult.forEach(it -> {
                    if (!it.getIsApply()) {
                        errorList.add(new BaseImportAction.ImportError(it.getRowNo(), "价目表不适用当前客户"));
                    }
                });
            }
        }
    }

    /**
     * 校验商机是否在当前客户下
     *
     * @param errorList
     * @param dataList
     */
    public static void validateOpportunityInAccount(ActionContext actionContext,
                                                    ServiceFacade serviceFacade,
                                                    List<BaseImportAction.ImportError> errorList,
                                                    List<BaseImportDataAction.ImportData> dataList) {
        List<IObjectData> objectDataList = dataList.stream().map(BaseImportDataAction.ImportData::getData).collect(Collectors.toList());
        Set<String> opportunityIds = objectDataList.stream()
                .filter(it -> it.get(QuoteConstants.QuoteField.OPPORTUNITYID.getApiName()) != null)
                .map(it -> String.valueOf(it.get(QuoteConstants.QuoteField.OPPORTUNITYID.getApiName())))
                .collect(Collectors.toSet());

        if (!opportunityIds.isEmpty()) {
            List<IObjectData> opportunitys = getOpportunitys(serviceFacade, actionContext.getTenantId(), opportunityIds);
            dataList.forEach(data -> {
                IObjectData objectData = data.getData();

                if (objectData.get(QuoteConstants.QuoteField.OPPORTUNITYID.getApiName()) != null) {
                    String opportunityId = String.valueOf(objectData.get(QuoteConstants.QuoteField.OPPORTUNITYID.getApiName()));
                    Optional<IObjectData> opportunity = opportunitys.stream()
                            .filter(it -> it.getId().equals(opportunityId))
                            .findAny();
                    if (opportunity.isPresent()) {
                        if (!String.valueOf(opportunity.get().get(QuoteConstants.QuoteField.ACCOUNTID.getApiName()))
                                .equals(String.valueOf(objectData.get(QuoteConstants.QuoteField.ACCOUNTID.getApiName())))) {
                            errorList.add(new BaseImportAction.ImportError(data.getRowNo(), "商机不在当前客户下"));
                        }
                    }
                }
            });
        }

    }

    private static List<IObjectData> getOpportunitys(ServiceFacade serviceFacade, String tenantId, Set<String> opportunityIds) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1000);

        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, "_id", opportunityIds);
        searchQuery.setFilters(filters);
        searchQuery.setPermissionType(0);
        IActionContext context = ActionContextExt.of(new User(tenantId, "-10000")).dbType("rest").getContext();

        return serviceFacade.findObjectDataByIds(context, Lists.newArrayList(opportunityIds), SFAPreDefineObject.Opportunity.getApiName());
    }

    /**
     * 批量校验当前价目表产品是否在已选的价目表范围内
     *
     * @param actionContext
     * @param priceBookCommonService
     * @param serviceFacade
     * @param errorList
     * @param dataList
     */
    public static void validateProductInPriceBook(ActionContext actionContext,
                                                  PriceBookCommonService priceBookCommonService,
                                                  ServiceFacade serviceFacade,
                                                  List<BaseImportAction.ImportError> errorList,
                                                  List<BaseImportDataAction.ImportData> dataList) {

        Map<String, List<String>> priceBookId2priceBookProductIdsMap = validateProductInPriceBook(
                actionContext,
                priceBookCommonService,
                serviceFacade,
                dataList.stream().map(BaseImportDataAction.ImportData::getData).collect(Collectors.toList()));

        log.debug("validateProductInPriceBook->priceBookId2priceBookProductIdsMap:{}", priceBookId2priceBookProductIdsMap);

        if (!priceBookId2priceBookProductIdsMap.isEmpty()) {
            priceBookId2priceBookProductIdsMap.forEach((priceBookId, priceBookProductIds) -> priceBookProductIds.forEach(priceBookProductId -> {
                List<BaseImportDataAction.ImportData> datas = dataList.stream().filter(x ->
                        String.valueOf(x.getData().get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName()))
                                .equals(priceBookId)
                                && String.valueOf(x.getData().get(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName()))
                                .equals(priceBookProductId))
                        .collect(Collectors.toList());
                datas.forEach(data ->
                        errorList.add(new BaseImportAction.ImportError(data.getRowNo(), "价目表产品不在当前价目表范围内"))
                );
            }));
        }
    }

    /**
     * 批量校验当前价目表产品是否在已选的价目表范围内
     *
     * @param context
     * @param priceBookCommonService
     * @param serviceFacade
     * @param objectDatas
     * @return 返回价目表Id和价目表产品Id map
     */
    private static Map<String, List<String>> validateProductInPriceBook(
            ActionContext context,
            PriceBookCommonService priceBookCommonService,
            ServiceFacade serviceFacade,
            List<IObjectData> objectDatas) {

        List<String> quoteIds = objectDatas.stream()
                .map(it -> String.valueOf(it.get(QuoteConstants.QuoteField.QUOTEID.getApiName())))
                .distinct()
                .collect(Collectors.toList());

        //批量获取报价单
        List<IObjectData> quoteDatas = serviceFacade.findObjectDataByIds(context.getTenantId(),
                quoteIds, SFAPreDefineObject.Quote.getApiName());

        Map<String, List<String>> priceBookId2priceBookProductIdsMap = new HashMap<>();
        quoteDatas.forEach(it -> {
            List<IObjectData> objectDataPerQuote = objectDatas.stream()
                    .filter(x -> x.get(QuoteConstants.QuoteField.QUOTEID.getApiName())
                            .equals(it.getId()))
                    .collect(Collectors.toList());

            List<String> priceBookProductIds = new ArrayList<>();
            objectDataPerQuote.forEach(x -> {
                //将价目表Id回填到objectDatas中，方便定位
                x.set(QuoteConstants.QuoteField.PRICEBOOKID.getApiName()
                        , String.valueOf(it.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName())));
                String priceBookProductId = String.valueOf(x.get(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName()));
                if (!priceBookProductIds.contains(priceBookProductId)) {
                    priceBookProductIds.add(priceBookProductId);
                }
            });
            priceBookId2priceBookProductIdsMap.put(
                    String.valueOf(it.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName()))
                    , priceBookProductIds);

        });

        log.debug("getNotInPriceBookPriceBookProductIds.arg:{}", priceBookId2priceBookProductIdsMap);
        return priceBookCommonService
                .getNotInPriceBookPriceBookProductIds(context.getTenantId(), priceBookId2priceBookProductIdsMap);

    }

    /**
     * 开启价目表时，根据价目表产品获取产品ID
     *
     * @param validList
     */
    public static void setProductId(ActionContext actionContext, ServiceFacade serviceFacade, List<IObjectData> validList) {
        List<String> priceBookProductIds = validList.stream()
                .map(it -> String.valueOf(it.get(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName())))
                .distinct()
                .collect(Collectors.toList());

        List<IObjectData> priceBookProducts = serviceFacade.findObjectDataByIds(actionContext.getTenantId(),
                priceBookProductIds, SFAPreDefineObject.PriceBookProduct.getApiName());
        validList.forEach(data -> {
            Optional<IObjectData> priceBookProduct = priceBookProducts.stream().
                    filter(it -> it.getId().equals(data.get(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName())))
                    .findAny();
            if (priceBookProduct.isPresent()) {
                data.set(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()
                        , priceBookProduct.get().get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()));
            }
        });
    }

    public static void validateProductIsRepeated(ActionContext actionContext,
                                                 ServiceFacade serviceFacade,
                                                 List<BaseImportAction.ImportError> errorList,
                                                 List<BaseImportDataAction.ImportData> dataList) {

        List<String> quoteIds = dataList.stream()
                .map(BaseImportDataAction.ImportData::getData)
                .map(it -> String.valueOf(it.get(QuoteConstants.QuoteField.QUOTEID.getApiName())))
                .distinct()
                .collect(Collectors.toList());
        List<IObjectData> quoteLines = findQuoteLinesByQuoteIds(actionContext, serviceFacade, quoteIds);
        //log.debug("validateProductIsRepeated->findQuoteLinesByQuoteIds.result:{} ",quoteLines);
        //log.debug("dataList:{} ",dataList);

        dataList.forEach(data -> {
            //当前表格比对
            if (dataList.stream()
                    .map(BaseImportDataAction.ImportData::getData)
                    .filter(it -> String.valueOf(it.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()))
                            .equals(String.valueOf(data.getData().get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName())))
                            && String.valueOf(it.get(QuoteConstants.QuoteField.QUOTEID.getApiName()))
                            .equals(String.valueOf(data.getData().get(QuoteConstants.QuoteField.QUOTEID.getApiName())))
                            && String.valueOf(it.get(IFieldType.RECORD_TYPE))
                            .equals(String.valueOf(data.getData().get(IFieldType.RECORD_TYPE))))
                    .count() > 1) {
                errorList.add(new BaseImportAction.ImportError(data.getRowNo(), "产品在当前报价单中已经存在"));
            }

            //和数据库比对
            if (quoteLines.stream()
                    .anyMatch(it -> String.valueOf(it.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()))
                            .equals(String.valueOf(data.getData().get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName())))
                            && String.valueOf(it.get(QuoteConstants.QuoteField.QUOTEID.getApiName()))
                            .equals(String.valueOf(data.getData().get(QuoteConstants.QuoteField.QUOTEID.getApiName())))
                            && String.valueOf(it.get(IFieldType.RECORD_TYPE))
                            .equals(String.valueOf(data.getData().get(IFieldType.RECORD_TYPE)))
                            && (data.getData().getId() == null || !data.getData().getId().equals(it.getId())))
                    ) {
                errorList.add(new BaseImportAction.ImportError(data.getRowNo(), "产品在当前报价单中已经存在"));
            }
        });
    }

    private static List<IObjectData> findQuoteLinesByQuoteIds(ActionContext actionContext,
                                                              ServiceFacade serviceFacade,
                                                              List<String> quoteIds) {
        SearchTemplateQuery searchQuery = new SearchTemplateQuery();

        searchQuery.setLimit(3000);
        searchQuery.setOffset(0);
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, QuoteConstants.QuoteField.QUOTEID.getApiName(), quoteIds);
        searchQuery.setFilters(filters);
        return serviceFacade.findBySearchQuery(actionContext.getUser(),
                SFAPreDefineObject.QuoteLines.getApiName(), searchQuery)
                .getData();
    }

    public static void customValidate(IObjectDescribe objectDescribe,
                                      ActionContext actionContext,
                                      PriceBookCommonService priceBookCommonService,
                                      ServiceFacade serviceFacade,
                                      List<BaseImportAction.ImportError> errorList,
                                      List<BaseImportDataAction.ImportData> dataList) {
        if (QuoteValidator.enablePriceBook(objectDescribe)) {
            validateProductInPriceBook(actionContext, priceBookCommonService, serviceFacade, errorList, dataList);

            List<IObjectData> validList = dataList.stream()
                    .map(BaseImportDataAction.ImportData::getData).collect(Collectors.toList());
            setProductId(actionContext, serviceFacade, validList);
        }

        validateProductIsRepeated(actionContext, serviceFacade, errorList, dataList);
    }
}
