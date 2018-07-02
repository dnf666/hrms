package com.facishare.crm.promotion.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.facishare.crm.promotion.base.BaseServiceTest;
import com.facishare.crm.promotion.constants.PromotionConstants;
import com.facishare.crm.promotion.constants.PromotionProductConstants;
import com.facishare.crm.promotion.constants.PromotionRuleConstants;
import com.facishare.crm.promotion.enums.PromotionRecordTypeEnum;
import com.facishare.crm.promotion.enums.PromotionRuleRecordTypeEnum;
import com.facishare.crm.promotion.predefine.service.PromotionInitService;
import com.facishare.crm.promotion.predefine.service.PromotionService;
import com.facishare.crm.promotion.predefine.service.dto.BatchGetProductQuotaByProductIdsModel;
import com.facishare.crm.promotion.predefine.service.dto.PromotionType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDescribeExt;
import com.facishare.paas.appframework.metadata.RecordTypeAuthProxy;
import com.facishare.paas.appframework.metadata.dto.auth.RoleInfoModel;
import com.facishare.paas.appframework.privilege.UserRoleInfoServiceImpl;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.describe.ObjectReferenceFieldDescribe;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.Where;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test/applicationContext.xml")
public class PromotionServiceTest extends BaseServiceTest {
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private PromotionInitService promotionInitService;
    @Autowired
    private RecordTypeAuthProxy recordTypeAuthApi;
    @Autowired
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private UserRoleInfoServiceImpl userRoleInfoService;
    @Autowired
    private ServiceFacade serviceFacade;

    public PromotionServiceTest() {
        super(PromotionConstants.API_NAME);
    }

    static {
        System.setProperty("spring.profiles.active", "ceshi113");
    }

    @Test
    public void findDetailDataListTest() {
        List<IObjectDescribe> objectDescribeList = serviceFacade.findDetailDescribes(tenantId, PromotionConstants.API_NAME);
        IObjectData masterData = new ObjectData();
        masterData.setDescribeApiName(PromotionConstants.API_NAME);
        masterData.setId("5a6a9ee7a5083dfd9a1a563f");
        masterData.setTenantId(tenantId);
        Map<String, List<IObjectData>> dataMap = serviceFacade.findDetailObjectDataList(objectDescribeList, masterData, new User(tenantId, fsUserId));
        System.out.println(dataMap);
    }

    @Test
    public void roleInfoTest() {
        userRoleInfoService.getMainRoleLayoutAPIName(new User("2", "-10000"), "CustomerPaymentObj", "default__c");
    }

    @Test
    public void describeFixTest() throws MetadataServiceException {
        List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Filter> filters = Lists.newArrayList();
        Filter filter = new Filter();
        filter.setFieldName("is_giveaway");
        filter.setOperator(Operator.EQ);
        filter.setFieldValues(Lists.newArrayList("1"));//1表示是赠品
        filters.add(filter);
        map.put("filters", filters);
        wheres.add(map);

        IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, PromotionRuleConstants.API_NAME);
        ObjectReferenceFieldDescribe objectReferenceFieldDescribe = (ObjectReferenceFieldDescribe) objectDescribe.getFieldDescribe(PromotionRuleConstants.Field.GiftProduct.apiName);
        objectReferenceFieldDescribe.setWheres(wheres);
        IObjectDescribe updateDescribe = objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(objectReferenceFieldDescribe));
        //        objectDescribeService.update(objectDescribe,true);
        System.out.println(updateDescribe);

    }

    @Test
    public void describeFixTest1() throws MetadataServiceException {
        List<LinkedHashMap> wheres = Lists.newArrayList();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("connector", Where.CONN.OR);
        List<Filter> filters = Lists.newArrayList();
        Filter filter = new Filter();
        filter.setFieldName("is_giveaway");
        filter.setOperator(Operator.EQ);
        filter.setFieldValues(Lists.newArrayList("0"));//0表示是非赠品
        filters.add(filter);
        map.put("filters", filters);
        wheres.add(map);

        IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(PromotionProductConstants.API_NAME, tenantId);
        ObjectDescribeExt objectDescribeExt = ObjectDescribeExt.of(objectDescribe);
        ObjectReferenceFieldDescribe objectReferenceFieldDescribe = (ObjectReferenceFieldDescribe) objectDescribeExt.getFieldDescribe(PromotionProductConstants.Field.Product.apiName);
        objectReferenceFieldDescribe.setWheres(wheres);
        IObjectDescribe updateDescribe = objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(objectReferenceFieldDescribe));
        //        objectDescribeService.update(objectDescribe,true);
        System.out.println(updateDescribe);

    }

    @Test
    public void queryProductDescribeTest() throws MetadataServiceException {
        IObjectDescribe objectDescribe = objectDescribeService.findByTenantIdAndDescribeApiName(tenantId, "ProductObj");
        System.out.print(objectDescribe);
    }

    @Test
    public void enablePromotionTest() {
        PromotionType.EnableResult enableResult = promotionService.enablePromotion(newServiceContext());
        System.out.println(enableResult);
    }

    @Test
    public void recordTypeTest() {
        User user = new User(tenantId, fsUserId);
        RoleInfoModel.Arg roleInfoModelArg = new RoleInfoModel.Arg();
        roleInfoModelArg.setAuthContext(user);
        RoleInfoModel.Result result = recordTypeAuthApi.roleInfo(roleInfoModelArg);
        System.out.println(result);
        promotionInitService.initProductRecordType(user, PromotionConstants.API_NAME, PromotionRecordTypeEnum.ProductPromotion.apiName, PromotionConstants.DEFAULT_LAYOUT_API_NAME, result.getResult().getRoles());
        promotionInitService.initProductRecordType(user, PromotionRuleConstants.API_NAME, PromotionRuleRecordTypeEnum.ProductPromotion.apiName, PromotionRuleConstants.DEFAULT_LAYOUT_API_NAME, result.getResult().getRoles());
        System.out.println(result);
    }

    @Test
    public void getById() {
        ServiceContext serviceContext = newServiceContext();
        PromotionType.IdModel idModel = new PromotionType.IdModel();
        idModel.setId("5a55b20d830bdbc4a5fa0a5f");
        PromotionType.DetailResult detailResult = promotionService.getById(serviceContext, idModel);
        Assert.assertNotNull(detailResult);

    }

    @Test
    public void getByIds() {
        ServiceContext serviceContext = newServiceContext();
        PromotionType.IdsModel idModel = new PromotionType.IdsModel();
        idModel.setIds(Lists.newArrayList("5a55b20d830bdbc4a5fa0a5f"));
        List<PromotionType.DetailResult> detailResult = promotionService.getByIds(serviceContext, idModel);
        Assert.assertNotNull(detailResult);

    }

    @Test
    public void listByProductIds() {
        PromotionType.ProductPromotionListArg listProductsArg = new PromotionType.ProductPromotionListArg();
        listProductsArg.setCustomerId("ad71b92b6f3b4efa956d4e6ca0c3b624");
        listProductsArg.setProductIds(Lists.newArrayList("df2274532dea406ab0ddc7ca9ddfc4ce"));
        PromotionType.ProductPromotionResult productPromotionResult = promotionService.listByProductIds(newServiceContext(), listProductsArg);
        Assert.assertNotNull(productPromotionResult);
    }

    @Test
    public void listPromotionByProductIds() {
        PromotionType.ProductPromotionListArg listProductsArg = new PromotionType.ProductPromotionListArg();
        listProductsArg.setCustomerId("3cf53f42df174aecaaa17d0526f2654d");
        listProductsArg.setProductIds(Lists.newArrayList("a8da2abcff014e2bb4b83a72ce9e9122"));
        PromotionType.ProductToPromotionId productToPromotionId = promotionService.listPromotionByProductIds(newServiceContext(), listProductsArg);
        Assert.assertNotNull(productToPromotionId);
    }

    @Test
    public void listByCustomerId() {
        PromotionType.CustomerIdArg customerIdArg = new PromotionType.CustomerIdArg();
        customerIdArg.setCustomerId("e741f12330c44d3aab20f7f51e1daba3");
        PromotionType.PromotionRuleResult promotionRuleResult = promotionService.listByCustomerId(newServiceContext(), customerIdArg);
        Assert.assertNotNull(promotionRuleResult);
    }

    @Test
    public void listProductsByCustomerId() {
        PromotionType.ListProductsArg listProductsArg = new PromotionType.ListProductsArg();
        listProductsArg.setCustomerId("e741f12330c44d3aab20f7f51e1daba3");
        PromotionType.ListProductResult listProductResult = promotionService.listProductsByCustomerId(newServiceContext(), listProductsArg);
        Assert.assertNotNull(listProductResult);

    }

    @Test
    public void isPromotionEnable() {
        ServiceContext serviceContext = newServiceContext();
        Boolean b = promotionService.isPromotionEnable(serviceContext).getEnable();
        Assert.assertNotNull(b);

    }

    @Test
    public void batchGetProductQuotaByProductIdsTest() {
        ServiceContext serviceContext = newServiceContext();
        BatchGetProductQuotaByProductIdsModel.Arg arg = new BatchGetProductQuotaByProductIdsModel.Arg();
        BatchGetProductQuotaByProductIdsModel.PromotionProductIdArg promotionProductIdArg = new BatchGetProductQuotaByProductIdsModel.PromotionProductIdArg();
        promotionProductIdArg.setAmount(2.0);
        promotionProductIdArg.setPromotionId("5ad96492bab09c993bded543");
        promotionProductIdArg.setProductId("5ad96493bab09c993bded584");
        arg.setPromotionProductIdArgs(Lists.newArrayList(promotionProductIdArg));
        promotionService.batchGetProductQuotaByProductIds(serviceContext, arg);
    }

}
