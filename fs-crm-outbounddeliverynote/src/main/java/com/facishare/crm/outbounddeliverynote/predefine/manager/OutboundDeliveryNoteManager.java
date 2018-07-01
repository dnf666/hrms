package com.facishare.crm.outbounddeliverynote.predefine.manager;

import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteConstants;
import com.facishare.crm.outbounddeliverynote.constants.OutboundDeliveryNoteProductConstants;
import com.facishare.crm.outbounddeliverynote.enums.OutboundDeliveryNoteRecordTypeEnum;
import com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteProductVO;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteVO;
import com.facishare.crm.stock.constants.GoodsReceivedNoteConstants;
import com.facishare.crm.util.ObjectFieldConstantsUtil;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.appframework.metadata.TeamMember;
import com.facishare.paas.appframework.metadata.dto.SaveMasterAndDetailData;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.DELETE_STATUS;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.QueryResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.ObjectData;
import com.facishare.paas.metadata.impl.search.Filter;
import com.facishare.paas.metadata.impl.search.Operator;
import com.facishare.paas.metadata.impl.search.SearchTemplateQuery;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linchf
 * @date 2018/3/15
 */
@Service
@Slf4j(topic = "outBoundDeliveryNoteAccessLog")
public class OutboundDeliveryNoteManager {
    @Resource
    private ServiceFacade serviceFacade;

    public void create(User user, OutboundDeliveryNoteVO outboundDeliveryNoteVO, List<OutboundDeliveryNoteProductVO> outboundDeliveryNoteProductVOs, String type) {
        //采用用户身份去创建
//        user = new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        SaveMasterAndDetailData.Arg arg = buildNote(user, outboundDeliveryNoteVO, outboundDeliveryNoteProductVOs, type);
        serviceFacade.saveMasterAndDetailData(user, arg);
    }

    /**
     *
     * @param user 操作者
     * @param id 发货单id或调拨单id
     * @param type 操作类型
     */
    public void invalid(User user, String id, String type) {
        //采用用户身份去作废
//        user = new User(user.getTenantId(), User.SUPPER_ADMIN_USER_ID);

        List<IObjectData> invalidDataList = Lists.newArrayList();

        if (Objects.equals(type, OutboundTypeEnum.REQUISITION_OUTBOUND.value)) {
            invalidDataList = findByDeliveryOrRequisitionId(user, null, id);
        }

        if (Objects.equals(type, OutboundTypeEnum.SALES_OUTBOUND.value)) {
            invalidDataList = findByDeliveryOrRequisitionId(user, id, null);
        }

        if (!CollectionUtils.isEmpty(invalidDataList)) {
            List<String> invalidIds = invalidDataList.stream().map(objectData -> objectData.getId()).collect(Collectors.toList());

            List<IObjectData> productDataList = findProductByIds(user, invalidIds);

            if (!CollectionUtils.isEmpty(productDataList)) {
                serviceFacade.bulkInvalid(productDataList, user);
            }
            serviceFacade.bulkInvalid(invalidDataList, user);
        }
    }

    private SaveMasterAndDetailData.Arg buildNote(User user, OutboundDeliveryNoteVO outboundDeliveryNoteVO, List<OutboundDeliveryNoteProductVO> outboundDeliveryNoteProductVOs, String type) {
        IObjectDescribe noteDescribe = serviceFacade.findObject(user.getTenantId(), OutboundDeliveryNoteConstants.API_NAME);
        IObjectDescribe noteProductDescribe = serviceFacade.findObject(user.getTenantId(), OutboundDeliveryNoteProductConstants.API_NAME);

        if (noteDescribe == null || noteProductDescribe == null) {
            log.warn("build outboundDeliveryNote failed. describe is null. user[{}]", user);
            return null;
        }

        //创建主对象
        IObjectData masterObj = new ObjectData();
        masterObj.set(OutboundDeliveryNoteConstants.Field.Outbound_Date.apiName, outboundDeliveryNoteVO.getOutboundDate());
        masterObj.set(OutboundDeliveryNoteConstants.Field.Warehouse.apiName, outboundDeliveryNoteVO.getWarehouseId());
        masterObj.set(OutboundDeliveryNoteConstants.Field.Outbound_Type.apiName, outboundDeliveryNoteVO.getOutboundType());
        masterObj.set(OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName, outboundDeliveryNoteVO.getDeliveryNoteId());
        masterObj.set(OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName, outboundDeliveryNoteVO.getRequisitionNoteId());
        masterObj.set(OutboundDeliveryNoteConstants.Field.Remark.apiName, outboundDeliveryNoteVO.getRemark());

        masterObj.setTenantId(user.getTenantId());
        masterObj.setCreatedBy(user.getUserId());
        masterObj.setLastModifiedBy(user.getUserId());
        masterObj.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
        masterObj.setRecordType(OutboundDeliveryNoteRecordTypeEnum.DefaultOutbound.apiName);
        masterObj.set(IObjectData.DESCRIBE_ID, noteDescribe.getId());
        masterObj.set(IObjectData.DESCRIBE_API_NAME, OutboundDeliveryNoteConstants.API_NAME);
        masterObj.set(IObjectData.PACKAGE, "CRM");
        masterObj.set(IObjectData.VERSION, noteDescribe.getVersion());

        if (Objects.equals(type, OutboundTypeEnum.REQUISITION_OUTBOUND.value)) {
            masterObj.setRecordType(OutboundDeliveryNoteRecordTypeEnum.RequisitionOutbound.apiName);
        }

        if (Objects.equals(type, OutboundTypeEnum.SALES_OUTBOUND.value)) {
            masterObj.setRecordType(OutboundDeliveryNoteRecordTypeEnum.SalesOutbound.apiName);
        }
        //相关团队
        TeamMember teamMember = new TeamMember(user.getUserId(), TeamMember.Role.OWNER, TeamMember.Permission.READANDWRITE);

        ObjectDataExt objectDataExt = ObjectDataExt.of(masterObj);
        objectDataExt.setTeamMembers(Lists.newArrayList(teamMember));

        masterObj = objectDataExt.getObjectData();

        //创建从对象
        Map<String, List<IObjectData>> detailObjectMap = new HashMap<>();
        List<IObjectData> detailObjects = Lists.newArrayList();
        outboundDeliveryNoteProductVOs.forEach(outboundDeliveryNoteProductVO -> {
            IObjectData productObj = new ObjectData();
            productObj.set(OutboundDeliveryNoteProductConstants.Field.Product.apiName, outboundDeliveryNoteProductVO.getProductId());
            productObj.set(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, outboundDeliveryNoteProductVO.getOutboundAmount());
            productObj.set(OutboundDeliveryNoteProductConstants.Field.Stock.apiName, outboundDeliveryNoteProductVO.getStockId());
            productObj.set(OutboundDeliveryNoteProductConstants.Field.Remark.apiName, outboundDeliveryNoteVO.getRemark());

            productObj.setTenantId(user.getTenantId());
            productObj.setCreatedBy(user.getUserId());
            productObj.setLastModifiedBy(user.getUserId());
            productObj.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(user.getUserId()));
            productObj.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
            productObj.set(IObjectData.DESCRIBE_ID, noteProductDescribe.getId());
            productObj.set(IObjectData.DESCRIBE_API_NAME, OutboundDeliveryNoteProductConstants.API_NAME);
            productObj.set(IObjectData.PACKAGE, "CRM");
            productObj.set(IObjectData.VERSION, noteProductDescribe.getVersion());


            //相关团队
            ObjectDataExt productDataExt = ObjectDataExt.of(productObj);
            productDataExt.setTeamMembers(Lists.newArrayList(teamMember));

            productObj = productDataExt.getObjectData();
            detailObjects.add(productObj);
        });

        detailObjectMap.put(OutboundDeliveryNoteProductConstants.API_NAME, detailObjects);

        //创建映射Map
        Map<String, IObjectDescribe> objectDescribesMap = new HashMap<>();
        objectDescribesMap.put(OutboundDeliveryNoteConstants.API_NAME, noteDescribe);
        objectDescribesMap.put(OutboundDeliveryNoteProductConstants.API_NAME, noteProductDescribe);

        return SaveMasterAndDetailData.Arg.builder().masterObjectData(masterObj).detailObjectData(detailObjectMap).objectDescribes(objectDescribesMap).build();
    }

    private List<IObjectData> findByDeliveryOrRequisitionId(User user, String deliveryNoteId, String requisitionNoteId) {
        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        if (StringUtils.isNotBlank(deliveryNoteId)) {
            SearchUtil.fillFilterEq(filters, OutboundDeliveryNoteConstants.Field.Delivery_Note.apiName, deliveryNoteId);
        }
        if (StringUtils.isNotBlank(requisitionNoteId)) {
            SearchUtil.fillFilterEq(filters, OutboundDeliveryNoteConstants.Field.Requisition_Note.apiName, requisitionNoteId);
        }

        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setPermissionType(0); //0不走权限  1走权限
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQuery(user, OutboundDeliveryNoteConstants.API_NAME, searchTemplateQuery);
        if (queryResult.getData() == null) {
            return Lists.newArrayList();
        }
        return queryResult.getData();
    }

    public List<IObjectData> findProductByIds(User user, List<String> outboundDeliveryNoteIds) {
        IObjectDescribe objectDescribe = serviceFacade.findObject(user.getTenantId(), OutboundDeliveryNoteProductConstants.API_NAME);

        SearchTemplateQuery searchTemplateQuery = new SearchTemplateQuery();
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterIn(filters, OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName, outboundDeliveryNoteIds);

        IFilter filter = new Filter();
        filter.setFieldName(IObjectData.IS_DELETED);
        filter.setOperator(Operator.IN);
        filter.setFieldValues(Lists.newArrayList(String.valueOf(DELETE_STATUS.NORMAL.getValue()), String.valueOf(DELETE_STATUS.INVALID.getValue())));
        filters.add(filter);

        searchTemplateQuery.setFilters(filters);
        searchTemplateQuery.setPermissionType(0); //0不走权限  1走权限
        QueryResult<IObjectData> queryResult = serviceFacade.findBySearchQueryWithDeleted(user, objectDescribe, searchTemplateQuery);
        if (queryResult.getData() == null) {
            return Lists.newArrayList();
        }
        return queryResult.getData();
    }

    public Map<String, Map<String, BigDecimal>> findProductMapByIds(User user, List<String> outboundDeliveryNoteIds, List<IObjectData> productList) {
        Map<String, Map<String, BigDecimal>> map = new HashMap<>();

        productList.forEach(objectData -> {
            String outboundDeliveryNoteId = objectData.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Delivery_Note.apiName, String.class);
            String productId = objectData.get(OutboundDeliveryNoteProductConstants.Field.Product.apiName, String.class);
            BigDecimal outboundAmount = objectData.get(OutboundDeliveryNoteProductConstants.Field.Outbound_Amount.apiName, BigDecimal.class);

            Map<String, BigDecimal> productAmountMap = map.get(outboundDeliveryNoteId);
            if (productAmountMap == null) {
                productAmountMap = new HashMap<>();
            }
            if (productAmountMap.get(productId) == null) {
                productAmountMap.put(productId, outboundAmount);
            } else {
                productAmountMap.put(productId, productAmountMap.get(productId).add(outboundAmount));
            }
            map.put(outboundDeliveryNoteId, productAmountMap);
        });
        return map;
    }

    public List<IObjectData> findByIds(User user, List<String> outboundDeliveryNoteIds) {
        List<IObjectData> objectDataList = serviceFacade.findObjectDataByIdsIncludeDeleted(user, outboundDeliveryNoteIds, OutboundDeliveryNoteConstants.API_NAME);
        if (objectDataList == null) {
            return Lists.newArrayList();
        }
        return objectDataList;
    }

    public void modifyArg(String tenantId, BaseObjectSaveAction.Arg arg) {
        ObjectDataDocument objectData = arg.getObjectData();
        if (objectData == null) {
            throw new ValidateException("对象不能为空");
        }

        if (org.springframework.util.CollectionUtils.isEmpty(arg.getDetails())) {
            throw new ValidateException("从对象不能为空");
        }

        // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
        if (StringUtils.isEmpty(arg.getObjectData().toObjectData().getRecordType())) {
            arg.getObjectData().put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
        }

        // OpenAPI接口调用时describeID为空，需要补充此字段
        String objectDescribeId = (String) arg.getObjectData().get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID);
        if (StringUtils.isEmpty(objectDescribeId)) {
            IObjectDescribe describe = findDescribe(tenantId, GoodsReceivedNoteConstants.API_NAME);
            setDescribeField(arg.getObjectData(), describe);
        }

        // 补全从对象的describeId
        Map<String, List<ObjectDataDocument>> details = arg.getDetails();
        if (MapUtils.isNotEmpty(details)) {
            details.forEach((describeApiName, value) -> {
                IObjectDescribe detailDescribe = findDescribe(tenantId, describeApiName);
                value.forEach(objectDataDocument -> {
                    String detailObjectDescribeId = (String) objectDataDocument.get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID);
                    if (StringUtils.isEmpty(detailObjectDescribeId)) {
                        setDescribeField(objectDataDocument, detailDescribe);
                    }
                });
            });
        }

        List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(OutboundDeliveryNoteProductConstants.API_NAME);
        productObjectDocList.forEach(product -> {
            // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
            if (StringUtils.isEmpty(product.toObjectData().getRecordType())) {
                product.put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
            }
        });

        arg.setObjectData(objectData);
    }

    private void setDescribeField(ObjectDataDocument objectDataDocument, IObjectDescribe objectDescribe) {
        objectDataDocument.put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID, objectDescribe.getId());
        objectDataDocument.put(ObjectFieldConstantsUtil.FIELD_DESCRIBE_API_NAME, objectDescribe.getApiName());
    }

    private IObjectDescribe findDescribe(String tenantId, String describeApiName) {
        IObjectDescribe describe = serviceFacade.findObject(tenantId, describeApiName);
        if (describe == null) {
            throw new ValidateException("查询不到对象[" + describeApiName + "]");
        }
        return describe;
    }
}
