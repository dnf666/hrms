package com.facishare.crm.requisitionnote.predefine.manager;

import com.facishare.crm.outbounddeliverynote.enums.OutboundTypeEnum;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteProductVO;
import com.facishare.crm.outbounddeliverynote.model.OutboundDeliveryNoteVO;
import com.facishare.crm.outbounddeliverynote.predefine.manager.OutboundDeliveryNoteManager;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteConstants;
import com.facishare.crm.requisitionnote.constants.RequisitionNoteProductConstants;
import com.facishare.crm.util.ObjectFieldConstantsUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.BaseObjectSaveAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author liangk
 * @date 16/03/2018
 */
@Service
@Slf4j(topic = "requisitionNoteAccess")
public class RequisitionNoteManager extends CommonManager {

    @Resource
    private ServiceFacade serviceFacade;

    @Resource
    private OutboundDeliveryNoteManager outboundDeliveryNoteManager;


    public void createOutboundDeliveryNote(User user, IObjectData objectData, String transferOutWarehouseId, List<ObjectDataDocument> productDocList) {
        String requisitionId = objectData.getId();
        OutboundDeliveryNoteVO outboundDeliveryNoteVO = OutboundDeliveryNoteVO.builder().warehouseId(transferOutWarehouseId).outboundType(OutboundTypeEnum.REQUISITION_OUTBOUND.value).outboundDate(System.currentTimeMillis()).requisitionNoteId(requisitionId).build();
        List<OutboundDeliveryNoteProductVO> outboundDeliveryNoteProductVOList = Lists.newArrayList();
        for (ObjectDataDocument product : productDocList) {
            String productId = product.toObjectData().get(RequisitionNoteProductConstants.Field.Product.apiName, String.class);
            String stockId = product.toObjectData().get(RequisitionNoteProductConstants.Field.Stock.apiName, String.class);
            BigDecimal requisitionProductAmount = product.toObjectData().get(RequisitionNoteProductConstants.Field.RequisitionProductAmount.apiName, BigDecimal.class);
            OutboundDeliveryNoteProductVO outboundDeliveryNoteProductVO = OutboundDeliveryNoteProductVO.builder().outboundAmount(requisitionProductAmount.toString())
                    .productId(productId).stockId(stockId).build();
            outboundDeliveryNoteProductVOList.add(outboundDeliveryNoteProductVO);
        }
        user = new User(user.getTenantId(), objectData.getCreatedBy());
        outboundDeliveryNoteManager.create(user, outboundDeliveryNoteVO, outboundDeliveryNoteProductVOList, OutboundTypeEnum.REQUISITION_OUTBOUND.value);
    }

    public void invalidOutboundDeliveryNote(User user, IObjectData objectData) {
        String requisitionId = objectData.getId();
        outboundDeliveryNoteManager.invalid(user, requisitionId, OutboundTypeEnum.REQUISITION_OUTBOUND.value);
    }

    public void modifyArg(String tenantId, BaseObjectSaveAction.Arg arg) {
        ObjectDataDocument objectData = arg.getObjectData();
        if (objectData == null) {
            throw new ValidateException("对象不能为空");
        }

        if (CollectionUtils.isEmpty(arg.getDetails())) {
            throw new ValidateException("从对象不能为空");
        }

        // 业务类型默认设置为预设业务类型，因为OpenAPI调用时RecordType会传空
        if (StringUtils.isEmpty(arg.getObjectData().toObjectData().getRecordType())) {
            arg.getObjectData().put(MultiRecordType.RECORD_TYPE, MultiRecordType.RECORD_TYPE_DEFAULT);
        }

        // OpenAPI接口调用时describeID为空，需要补充此字段
        String objectDescribeId = (String) arg.getObjectData().get(ObjectFieldConstantsUtil.FIELD_DESCRIBE_ID);
        if (StringUtils.isEmpty(objectDescribeId)) {
            IObjectDescribe describe = findDescribe(tenantId, RequisitionNoteConstants.API_NAME);
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

        List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(RequisitionNoteProductConstants.API_NAME);
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
