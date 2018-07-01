package com.facishare.crm.deliverynote.predefine.util;

import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.enums.SalesOrderLogisticsStatusEnum;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteVO;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货单状态相关
 * Created by chenzs on 2018/1/17.
 */
public class DeliveryNoteStatusUtil {
    /**
     * 计算订单的发货状态
     *      if 1、 都处于"未发货"/"审批中"/"已作废"                                               =>  未发货
     * else if 2、 都处于"已发货"/"变更中"/"已作废"                                +数量足够       =>  已发货
     * else if 3、 都处于"已收货"/"已作废"                                        +数量足够       =>  已收货
     * else if 4、 除了"未发货""审批中""已作废"，最新的一条是"已发货"/"变更中"                       =>部分发货
     * else    5、（除了"未发货""审批中""已作废"，最新的一条是"已收货"）                             =>部分收货
     */
    public SalesOrderLogisticsStatusEnum getDeliveryStatus(List<IObjectData> deliveryNoteObjectDatas, List<DeliveryNoteProductVO> deliveryNoteProductVos, BigDecimal allNeedDeliveryNum) {
        List<DeliveryNoteVO> deliveryNoteVOs = ObjectDataUtil.parseObjectData(deliveryNoteObjectDatas, DeliveryNoteVO.class);

        if (isAllToBeShipped(deliveryNoteVOs)) {
            return SalesOrderLogisticsStatusEnum.ToBeShipped;
        } else if (isAllConsigned(deliveryNoteVOs, deliveryNoteProductVos, allNeedDeliveryNum)) {
            return SalesOrderLogisticsStatusEnum.Consigned;
        } else if (isAllReceived(deliveryNoteVOs, deliveryNoteProductVos, allNeedDeliveryNum)) {
            return SalesOrderLogisticsStatusEnum.Received;
        }

        DeliveryNoteVO lastDeliveryNoteVo = getLastDeliveryNoteStatus(deliveryNoteObjectDatas);
        List<String> statusList = Lists.newArrayList(DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus(), DeliveryNoteObjStatusEnum.CHANGING.getStatus());
        if (statusList.contains(lastDeliveryNoteVo.getStatus())) {
            return SalesOrderLogisticsStatusEnum.PartialDelivery;
        }
        return SalesOrderLogisticsStatusEnum.PartialReceipt;
    }

    /**
     * 1、都处于"未发货"/"审批中"/"已作废"                                               =>  未发货
     */
    private boolean isAllToBeShipped(List<DeliveryNoteVO> deliveryNoteVOs) {
        List<String> statusList = Lists.newArrayList(DeliveryNoteObjStatusEnum.UN_DELIVERY.getStatus(), DeliveryNoteObjStatusEnum.IN_APPROVAL.getStatus(), DeliveryNoteObjStatusEnum.INVALID.getStatus());
        List<DeliveryNoteVO> notInStatusResult = deliveryNoteVOs.stream().filter(vo-> !statusList.contains(vo.getStatus())).collect(Collectors.toList());
        return CollectionUtils.isEmpty(notInStatusResult);
    }

    /**
     * 2、都处于"已发货"/"变更中"/"已作废"                                +数量足够       =>  已发货
     */
    private boolean isAllConsigned(List<DeliveryNoteVO> deliveryNoteVOs, List<DeliveryNoteProductVO> deliveryNoteProductVos, BigDecimal allNeedDeliveryNum) {
        //不是"已发货"/"变更中"/"已作废"这些状态的发货单
        List<String> statusList = Lists.newArrayList(DeliveryNoteObjStatusEnum.HAS_DELIVERED.getStatus(), DeliveryNoteObjStatusEnum.CHANGING.getStatus(), DeliveryNoteObjStatusEnum.INVALID.getStatus());
        List<DeliveryNoteVO> notInStatusResult = deliveryNoteVOs.stream().filter(vo-> !statusList.contains(vo.getStatus())).collect(Collectors.toList());

        return CollectionUtils.isEmpty(notInStatusResult) && Objects.equals(getAllDeliveredNum(deliveryNoteVOs, deliveryNoteProductVos, DeliveryNoteObjStatusEnum.HAS_DELIVERED), allNeedDeliveryNum);
    }

    /**
     * 3、都处于"已收货"/"已作废"                                        +数量足够       =>  已收货
     */
    private boolean isAllReceived(List<DeliveryNoteVO> deliveryNoteVOs, List<DeliveryNoteProductVO> deliveryNoteProductVos, BigDecimal allNeedDeliveryNum) {
        //不是"已收货"/"已作废"这些状态的发货单
        List<String> statusList = Lists.newArrayList(DeliveryNoteObjStatusEnum.RECEIVED.getStatus(), DeliveryNoteObjStatusEnum.INVALID.getStatus());
        List<DeliveryNoteVO> notInStatusResult = deliveryNoteVOs.stream().filter(vo-> !statusList.contains(vo.getStatus())).collect(Collectors.toList());

        return CollectionUtils.isEmpty(notInStatusResult) && Objects.equals(getAllDeliveredNum(deliveryNoteVOs, deliveryNoteProductVos, DeliveryNoteObjStatusEnum.RECEIVED), allNeedDeliveryNum);
    }

    /**
     * 除了"未发货""审批中""已作废"，最新的一条的状态（"已发货"/"变更中"/"已收货")
     */
    private DeliveryNoteVO getLastDeliveryNoteStatus(List<IObjectData> deliveryNoteObjectDatas) {
        List<String> noIncludeStatus = Lists.newArrayList(DeliveryNoteObjStatusEnum.UN_DELIVERY.getStatus(), DeliveryNoteObjStatusEnum.IN_APPROVAL.getStatus(), DeliveryNoteObjStatusEnum.INVALID.getStatus());

        IObjectData lastDeliveryNoteObjectData = null;
        for (IObjectData d : deliveryNoteObjectDatas) {
            String voStatus = d.get(DeliveryNoteObjConstants.Field.Status.getApiName(), String.class);
            if (!noIncludeStatus.contains(voStatus)) {
                if (lastDeliveryNoteObjectData == null) {
                    lastDeliveryNoteObjectData = d;
                } else {
                    Long dCreateTime = d.getCreateTime();
                    Long lastDeliveryNoteCreateTime = lastDeliveryNoteObjectData.getCreateTime();
                    if (dCreateTime > lastDeliveryNoteCreateTime) {
                        lastDeliveryNoteObjectData = d;
                    }
                }
            }
        }

        return ObjectDataUtil.parseObjectData(lastDeliveryNoteObjectData, DeliveryNoteVO.class);
    }

    /**
     * 所有状态为status的发货数量（比如所有"已收货"的数量）
     */
    private BigDecimal getAllDeliveredNum(List<DeliveryNoteVO> deliveryNoteVOs, List<DeliveryNoteProductVO> deliveryNoteProductVos, DeliveryNoteObjStatusEnum statusEnum) {
        BigDecimal allDeliveredNum = new BigDecimal(0);
        Map<String, String> deliveryNoteId2StatusMap = deliveryNoteVOs.stream().collect(Collectors.toMap(DeliveryNoteVO::getId, DeliveryNoteVO::getStatus));
        deliveryNoteProductVos.forEach(vo -> {
            String status = deliveryNoteId2StatusMap.get(vo.getId());
            if (Objects.equals(status, statusEnum.getStatus())) {
                allDeliveredNum.add(vo.getDeliveryNum());
            }
        });

        return allDeliveredNum;
    }
}