package com.facishare.crm.deliverynote.predefine.action;

import com.facishare.crm.action.CommonEditAction;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.enums.DeliveryNoteObjStatusEnum;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteManager;
import com.facishare.crm.stock.enums.StockOperateObjectTypeEnum;
import com.facishare.crm.stock.enums.StockOperateResultEnum;
import com.facishare.crm.stock.enums.StockOperateTypeEnum;
import com.facishare.crm.stock.model.StockOperateInfo;
import com.facishare.crm.deliverynote.predefine.manager.DeliveryNoteProductManager;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.DBRecord;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货单数据
 * Created by chenzs on 2018/1/8.
 */
@Slf4j
public class DeliveryNoteEditAction extends CommonEditAction {
    private DeliveryNoteManager deliveryNoteManager = SpringUtil.getContext().getBean(DeliveryNoteManager.class);
    private DeliveryNoteProductManager deliveryNoteProductManager = SpringUtil.getContext().getBean(DeliveryNoteProductManager.class);
    private String oldLifeStatus;
    private IObjectData oldObjectData;
    private List<IObjectData> oldProductObjectDataList;


    @Override
    protected void before(Arg arg) {
        String deliveryNoteId = (String) arg.getObjectData().get(DeliveryNoteObjConstants.Field.Id.apiName);
        oldObjectData = deliveryNoteManager.getObjectDataById(this.getActionContext().getUser(), deliveryNoteId);
        oldLifeStatus = oldObjectData.get(SystemConstants.Field.LifeStatus.apiName).toString();
        oldProductObjectDataList = deliveryNoteProductManager.queryObjectDatas(this.getActionContext().getUser(), Lists.newArrayList(deliveryNoteId));

        deliveryNoteManager.modifyArg(this.getActionContext().getTenantId(), arg);
        checkReadonlyField(arg);

        // 由于OpenAPI的更新操作提交的数据是增量数据，需要与当前的系统数据合并
        mergeObjectData(arg);

        // 新增校验
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Ineffective.value)) {
            List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME);
            deliveryNoteManager.checkForAdd(this.getActionContext().getUser(), arg.getObjectData().toObjectData(), ObjectDataDocument.ofDataList(productObjectDocList));
        }

        // 发货单编辑时只未生效状态才重新计算发货金额，因为其它状态下订单的产品单价及产品数都不可以修改、发货单的发货数也不可以修改
        if (oldLifeStatus.equals(SystemConstants.LifeStatus.Ineffective.value)) {
            List<ObjectDataDocument> productObjectDocList = arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME);

            // 设置发货单产品的本次发货金额
            String salesOrderId = (String) this.arg.getObjectData().get(DeliveryNoteObjConstants.Field.SalesOrderId.getApiName());
            deliveryNoteProductManager.setDeliveryMoney(actionContext.getUser(), salesOrderId, productObjectDocList);

            // 设置发货单发货总金额
            BigDecimal totalDeliveryMoney = BigDecimal.ZERO;
            for (ObjectDataDocument productObjectData : productObjectDocList) {
                BigDecimal deliveryMoney = productObjectData.toObjectData().get(DeliveryNoteProductObjConstants.Field.DeliveryMoney.apiName, BigDecimal.class);
                totalDeliveryMoney = totalDeliveryMoney.add(deliveryMoney);
            }
            arg.getObjectData().put(DeliveryNoteObjConstants.Field.TotalDeliveryMoney.apiName, totalDeliveryMoney);
        }

        super.before(arg);
    }

    private void checkReadonlyField(Arg arg) {
        // 非未生效的编辑均要校验只读字段
        if (!Objects.equals(oldLifeStatus ,SystemConstants.LifeStatus.Ineffective.value)) {
            //校验入库单主对象只读字段
            IObjectData newObjectData = arg.getObjectData().toObjectData();

            //如果发货单不是已收货状态，则不校验收货日期
            String status = oldObjectData.get(DeliveryNoteObjConstants.Field.Status.apiName, String.class);
            if (!Objects.equals(status, DeliveryNoteObjStatusEnum.RECEIVED.getStatus())) {
                DeliveryNoteObjConstants.READONLY_FIELD_API_NAMES_FOR_NORMAL_EDIT.removeIf(field -> Objects.equals(field, DeliveryNoteObjConstants.Field.ReceiveDate.apiName));
            }
            log.debug("checkReadOnlyField[{}]", DeliveryNoteObjConstants.READONLY_FIELD_API_NAMES_FOR_NORMAL_EDIT);
            checkReadOnlyField(oldObjectData, newObjectData, DeliveryNoteObjConstants.READONLY_FIELD_API_NAMES_FOR_NORMAL_EDIT);

            //校验入库单产品只读字段
            Map<String, IObjectData> id2OldProductObjectDataMap = oldProductObjectDataList.stream().collect(Collectors.toMap(DBRecord::getId, o -> o));
            List<IObjectData> newProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME));
            if (CollectionUtils.isEmpty(newProductObjectDocList)) {
                throw new ValidateException("发货单产品为空");
            }
            newProductObjectDocList.forEach(newProductObjectData -> {
                if (Objects.isNull(newProductObjectData.getId())) {
                    throw new ValidateException("当前不可添加新的发货单产品");
                }
                String id = newProductObjectData.getId();
                if (!id2OldProductObjectDataMap.containsKey(id)) {
                    throw new ValidateException("["+id+"]不属于当前发货单");
                }
                checkReadOnlyField(id2OldProductObjectDataMap.get(id), newProductObjectData, DeliveryNoteProductObjConstants.READONLY_FIELD_API_NAMES_FOR_NORMAL_EDIT);
            });

            if (oldProductObjectDataList.size() != newProductObjectDocList.size()) {
                throw new ValidateException("不可变更发货单产品");
            }
        }
    }

    private void checkReadOnlyField(IObjectData oldObjectData, IObjectData newObjectData, List<String> readOnlyFieldApiNames) {
        readOnlyFieldApiNames.forEach(readOnlyFieldApiName -> {
            if (Objects.nonNull(newObjectData.get(readOnlyFieldApiName))) {
                log.info("Feild[{}]: old[{}] - [{}]", readOnlyFieldApiName, oldObjectData.get(readOnlyFieldApiName), newObjectData.get(readOnlyFieldApiName));
                if (!Objects.equals(oldObjectData.get(readOnlyFieldApiName), newObjectData.get(readOnlyFieldApiName))) {
                    throw new ValidateException(readOnlyFieldApiName + "不可修改");
                }
            }
        });
    }

    private void mergeObjectData(Arg arg) {
        log.debug("mergeObjectData before[{}]", arg);
        String deliveryNoteId = (String) arg.getObjectData().get(DeliveryNoteObjConstants.Field.Id.apiName);
        IObjectData currentObjectData = deliveryNoteManager.getObjectDataById(this.getActionContext().getUser(), deliveryNoteId);
        IObjectData newObjectData = arg.getObjectData().toObjectData();
        arg.setObjectData(ObjectDataDocument.of(this.merge(currentObjectData, newObjectData)));

        List<IObjectData> currentProductObjectDataList = deliveryNoteProductManager.queryObjectDatas(this.getActionContext().getUser(), Lists.newArrayList(deliveryNoteId));
        Map<String, IObjectData> id2CurrentProductObjectDataMap = currentProductObjectDataList.stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        List<IObjectData> argProductObjectDocList = ObjectDataDocument.ofDataList(arg.getDetails().get(DeliveryNoteProductObjConstants.API_NAME));
        List<IObjectData> mergedProductObjectData = Lists.newArrayList();
        for (IObjectData argProduct : argProductObjectDocList) {
            String deliveryNoteProductIdStr = Objects.nonNull(argProduct.getId()) ? argProduct.getId() : null;
            boolean isNewAdd = StringUtils.isBlank(deliveryNoteProductIdStr);
            if (isNewAdd) {
                mergedProductObjectData.add(argProduct);
            } else {
                if (!id2CurrentProductObjectDataMap.containsKey(deliveryNoteProductIdStr)) {
                    throw new ValidateException("不存在对应的发货单产品记录");
                }
                mergedProductObjectData.add(this.merge(id2CurrentProductObjectDataMap.get(deliveryNoteProductIdStr), argProduct));
            }
        }
        arg.getDetails().remove(DeliveryNoteProductObjConstants.API_NAME);
        arg.getDetails().put(DeliveryNoteProductObjConstants.API_NAME, ObjectDataDocument.ofList(mergedProductObjectData));
        log.debug("mergeObjectData after[{}]", arg);
    }

    private IObjectData merge(IObjectData currentObjectData, IObjectData newObjectDate) {
        Map<String, Object> currentObjectDataMap = ObjectDataExt.of(currentObjectData).toMap();
        Map<String, Object> newObjectDataMap = ObjectDataExt.of(newObjectDate).toMap();
        newObjectDataMap.keySet().forEach(currentObjectDataMap::remove);
        currentObjectDataMap.putAll(newObjectDataMap);
        return ObjectDataExt.of(currentObjectDataMap).getObjectData();
    }

    @Override
    protected Result after(Arg arg, Result result) {
        result = super.after(arg, result);

        String newLifeStatus = (String) result.getObjectData().get(SystemConstants.Field.LifeStatus.apiName);
        log.debug("newLifeStatus[{}]", newLifeStatus);

        String deliveryNoteId = result.getObjectData().toObjectData().get(DeliveryNoteObjConstants.Field.Id.apiName, String.class);
        IObjectData deliveryNoteObjectData = deliveryNoteManager.getObjectDataById(this.getActionContext().getUser(), deliveryNoteId);

        // 无审批流程
        if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.Normal.value)) {
            // 未生效状态编辑需处理库存，正常状态的编辑不需要处理库存，因为正常状态的编辑不允许修改影响库存的字段
            if (oldLifeStatus.equals(SystemConstants.LifeStatus.Ineffective.value)) {
                User user = this.getActionContext().getUser();
                List<ObjectDataDocument> detailObjectDocList = result.getDetails().get(DeliveryNoteProductObjConstants.API_NAME);
                IObjectData newStatusObjectData = deliveryNoteManager.updateStatus(this.actionContext.getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.HAS_DELIVERED);
                result.setObjectData(ObjectDataDocument.of(newStatusObjectData));

                StockOperateInfo stockOperateInfo = StockOperateInfo.builder().operateObjectId(deliveryNoteId).operateType(StockOperateTypeEnum.EDIT.value)
                        .beforeLifeStatus(SystemConstants.LifeStatus.Ineffective.value)
                        .afterLifeStatus(SystemConstants.LifeStatus.Normal.value)
                        .operateObjectType(StockOperateObjectTypeEnum.DELIVERY_NOTE.value)
                        .operateResult(StockOperateResultEnum.PASS.value).build();

                deliveryNoteManager.doCreateDeliveryNoteBecomeHasDelivered(user, deliveryNoteObjectData, ObjectDataDocument.ofDataList(detailObjectDocList), stockOperateInfo);
            }
        } else if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.UnderReview.value)) {
            // 新建有审批
            IObjectData newStatusObjectData = deliveryNoteManager.updateStatus(this.actionContext.getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.IN_APPROVAL);
            result.setObjectData(ObjectDataDocument.of(newStatusObjectData));
        } else if (Objects.equals(newLifeStatus, SystemConstants.LifeStatus.InChange.value)) {
            // 编辑有审批
            IObjectData newStatusObjectData = deliveryNoteManager.updateStatus(this.actionContext.getUser(), deliveryNoteObjectData, DeliveryNoteObjStatusEnum.CHANGING);
            result.setObjectData(ObjectDataDocument.of(newStatusObjectData));
        }

        return result;
    }
}
