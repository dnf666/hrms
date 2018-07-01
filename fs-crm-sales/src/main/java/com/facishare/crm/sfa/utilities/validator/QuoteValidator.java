package com.facishare.crm.sfa.utilities.validator;

import com.facishare.crm.constants.CommonConstants;
import com.facishare.crm.sfa.predefine.SFAPreDefineObject;
import com.facishare.crm.sfa.predefine.service.PriceBookCommonService;
import com.facishare.crm.sfa.utilities.common.convert.SearchUtil;
import com.facishare.crm.sfa.utilities.constant.QuoteConstants;
import com.facishare.paas.appframework.common.util.CollectionUtils;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ActionContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IFieldType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.stream.Collectors;

public class QuoteValidator {

    /**
     *是否开启价目表
     * @return
     */
    public static boolean enablePriceBook(IObjectDescribe objectDescribe){

        String fieldApiName = objectDescribe.getApiName().equals(SFAPreDefineObject.Quote.getApiName()) ?
                QuoteConstants.QuoteField.PRICEBOOKID.getApiName() :
                QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName();

        return ObjectDescribeExt.of(objectDescribe)
                .getFieldDescribesSilently()
                .stream()
                .anyMatch(field->
                        field.getApiName().equals(fieldApiName)
                                && field.isActive()
                );

    }

    /**
     * 校验当前价目表产品是否在已选的价目表范围内
     * @param priceBookCommonService
     * @param tenantId
     * @param objectData
     * @param detailObjectData
     */
    public static void validateProductInPriceBook(
            PriceBookCommonService priceBookCommonService,
            String tenantId,
            IObjectData objectData,
            Map<String, List<IObjectData>> detailObjectData){

        Object priceBookId = objectData.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName());
        if(priceBookId!=null && !priceBookId.toString().isEmpty() && detailObjectData!=null){
            List<IObjectData> quoteLinesDatas = detailObjectData.get(SFAPreDefineObject.QuoteLines.getApiName());
            if(CollectionUtils.notEmpty(quoteLinesDatas)){
                Map<String,String> priceBookProductMap = Maps.newHashMap();
                quoteLinesDatas.forEach(data->
                        priceBookProductMap.computeIfAbsent(
                                data.get(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName()).toString(),
                                v->data.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName()).toString())
                );

                QuoteValidator.validateProductInPriceBook(priceBookCommonService,
                        tenantId,
                        priceBookId.toString(),
                        priceBookProductMap);
            }
        }
    }

    /**
     * 校验当前价目表产品是否在已选的价目表范围内(报价单明细)
     * @param priceBookCommonService
     * @param serviceFacade
     * @param context
     * @param objectData
     */
    public static void validateProductInPriceBook(
            ActionContext context,
            PriceBookCommonService priceBookCommonService,
            ServiceFacade serviceFacade,
            IObjectData objectData){

        Object quoteId = objectData.get(QuoteConstants.QuoteField.QUOTEID.getApiName());
        if(quoteId == null){
            throw new ValidateException("报价单ID不存在" );
        }

        //获取报价单
        IObjectData quoteData = serviceFacade.findObjectData(context.getUser(),
                quoteId.toString(),
                SFAPreDefineObject.Quote.getApiName());
        if(quoteData == null){
            throw new ValidateException("报价单不存在" );
        }

        Object priceBookId = quoteData.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName());
        if(priceBookId!=null && !priceBookId.toString().isEmpty()){
            Map<String,String> priceBookProductMap = new HashMap<>();
            priceBookProductMap.put(
                    String.valueOf(objectData.get(QuoteConstants.QuoteLinesField.PRICEBOOKPRODUCTID.getApiName())) ,
                    String.valueOf(objectData.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName())));

            QuoteValidator.validateProductInPriceBook(priceBookCommonService,
                    context.getTenantId(),
                    priceBookId.toString(),
                    priceBookProductMap);
            }
    }

    private static void validateProductInPriceBook(
            PriceBookCommonService priceBookCommonService,
            String tenantId,
            String priceBookId,
            Map<String, String> priceBookProductId2ProductIdMap){

        List<IObjectData> notInPriceBookPriceBookProducts = priceBookCommonService
                .getNotInPriceBookPriceBookProducts(
                        tenantId,
                        priceBookId.toString(),
                        priceBookProductId2ProductIdMap);

        if(!notInPriceBookPriceBookProducts.isEmpty() && notInPriceBookPriceBookProducts.get(0) !=null){
            Object productName = notInPriceBookPriceBookProducts.get(0)
                    .get(CommonConstants.NAME);
            if(productName!=null){
                throw new ValidateException(String.format("产品 %s 不在当前价目表范围内",productName.toString()) );
            }
        }
    }

    /**
     * 校验产品是否在当前报价单中已经存在
     * @param context
     * @param serviceFacade
     * @param objectData
     */
    public static void validateProductIsRepeated(
            ActionContext context,
            ServiceFacade serviceFacade,
            IObjectData objectData){

        Object productId = objectData.get(QuoteConstants.QuoteLinesField.PRODUCTID.getApiName());
        //产品Id不传时不校验
        if(productId == null){
            return;
        }

        Object quoteId = objectData.get(QuoteConstants.QuoteField.QUOTEID.getApiName());
        if(quoteId == null){
            throw new ValidateException("报价单ID不存在" );
        }

        Object recordType = objectData.get(IFieldType.RECORD_TYPE);
        if(recordType == null){
            throw new ValidateException("业务类型不存在" );
        }

        SearchTemplateQuery searchQuery = new SearchTemplateQuery();
        searchQuery.setLimit(1);
        searchQuery.setOffset(0);
        List filters = Lists.newLinkedList();
        SearchUtil.fillFilterEq(filters, QuoteConstants.QuoteField.QUOTEID.getApiName(), quoteId.toString());
        SearchUtil.fillFilterEq(filters, QuoteConstants.QuoteLinesField.PRODUCTID.getApiName(), productId.toString());
        SearchUtil.fillFilterEq(filters, IFieldType.RECORD_TYPE,recordType.toString() );
        searchQuery.setFilters(filters);
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(context.getUser(),
                SFAPreDefineObject.QuoteLines.getApiName(), searchQuery);
        if(!queryResult.getData().isEmpty()){
            IObjectData quoteLine = queryResult.getData().get(0);
            if (quoteLine !=null && !quoteLine.getId().equals(objectData.getId())) {
                throw new ValidateException("产品在当前报价单中已经存在");
            }
        }
    }

    /**
     * 校验当前价目表是否适用当前客户
     * @param context
     * @param priceBookCommonService
     * @param objectData
     */
    public static void validateAccountPriceBook(
            ActionContext context,
            PriceBookCommonService priceBookCommonService,
            IObjectData objectData){

        Object priceBookId = objectData.get(QuoteConstants.QuoteField.PRICEBOOKID.getApiName());
        if(priceBookId!=null && !priceBookId.toString().isEmpty()) {

            Object accountId = objectData.get(QuoteConstants.QuoteField.ACCOUNTID.getApiName());
            Boolean isApplicable = priceBookCommonService
                    .validateAccountPriceBook(
                            context.getUser(),
                            priceBookId.toString(),
                            accountId.toString());
            if(!isApplicable){
                throw new ValidateException("当前价目表不适用当前客户");
            }
        }
    }
}
