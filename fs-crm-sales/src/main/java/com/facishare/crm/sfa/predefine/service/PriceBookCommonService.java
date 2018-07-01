package com.facishare.crm.sfa.predefine.service;

import com.facishare.crm.sfa.predefine.service.model.ValidImportSalesOrder;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;

import java.util.List;
import java.util.Map;

/**
 * Created by luxin on 2018/1/30.
 */
public interface PriceBookCommonService {

    /**
     * 获得不再价目表中的价目表产品列表
     *
     * @param priceBookProductId2ProductId 价目表产品id对应的产品id的map
     */
    List<IObjectData> getNotInPriceBookPriceBookProducts(String tenantId, String priceBookId, Map<String, String> priceBookProductId2ProductId);

    /**
     * 批量获得不在价目表中的价目表产品列表Id
     *
     * @param tenantId
     * @param priceBookId2priceBookProductIds
     * @return
     */
    Map<String, List<String>> getNotInPriceBookPriceBookProductIds(String tenantId, Map<String, List<String>> priceBookId2priceBookProductIds);

    /**
     * 根据价目表id和客户id验证价目表是否适用客户Id
     */
    Boolean validateAccountPriceBook(User user, String priceBookId, String accountId);

    /**
     * 验证价目表数据是否适用客户
     */
    Boolean validateAccountPriceBookWithData(User user, IObjectDescribe accountDescribe, IObjectData priceBookData, IObjectData accountData);

    /**
     * 验证价目表数据是否适用客户
     */
    Boolean validateAccountPriceBookWithData(User user, IObjectDescribe accountDescribe, IObjectData priceBookData, IObjectData accountData, List<String> departList, Boolean isUserAdmin);

    /**
     * 验证价目表数据是否适用客户,导入使用
     *
     * @param user
     * @param importInfos
     * @return
     */
    List<ValidImportSalesOrder.ValidPriceBookImportInfo> validateAccountPriceBook(User user, List<ValidImportSalesOrder.ValidPriceBookImportInfo> importInfos);
}
