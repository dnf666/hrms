package com.facishare.crm.deliverynote.predefine.manager;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.facishare.crm.deliverynote.enums.DeliveryNoteSwitchEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facishare.crm.deliverynote.constants.DeliveryNoteObjConstants;
import com.facishare.crm.deliverynote.constants.DeliveryNoteProductObjConstants;
import com.facishare.crm.deliverynote.exception.DeliveryNoteBusinessException;
import com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode;
import com.facishare.crm.manager.DeliveryNoteObjManager;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/**
 * 对象定义
 * Created by chenzs on 2018/1/9.
 */
@Service
@Slf4j
public class ObjectDescribeManager {
    @Resource
    private IObjectDescribeService objectDescribeService;
    @Autowired
    private DeliveryNoteObjManager deliveryNoteObjManager;
    @Resource
    private ObjectDescribeDraftManager objectDescribeDraftManager;
    @Resource
    private ConfigManager configManager;

    /**
     * 获取"发货单"和"发货单产品"这两个中已经被使用的displayName
     */
    public Set<String> getExistDisplayName(String tenantId) {
        try {
            Set<String> existDisplayNames = Sets.newHashSet();
            List<String> existDeliveryNoteApiNames = objectDescribeService.checkDisplayNameExist(tenantId, DeliveryNoteObjConstants.DISPLAY_NAME, "CRM");
            existDeliveryNoteApiNames.forEach(x -> {
                if (!DeliveryNoteObjConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(DeliveryNoteObjConstants.DISPLAY_NAME);
                }
            });

            List<String> existDeliveryNoteProductApiNames = objectDescribeService.checkDisplayNameExist(tenantId, DeliveryNoteProductObjConstants.DISPLAY_NAME, "CRM");
            existDeliveryNoteProductApiNames.forEach(x -> {
                if (!DeliveryNoteProductObjConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(DeliveryNoteProductObjConstants.DISPLAY_NAME);
                }
            });

            log.debug("getExistDisplayName tenantId:{}, Result:{}", tenantId, existDisplayNames);
            return existDisplayNames;
        } catch (MetadataServiceException e) {
            log.warn("getExistDisplayName error,tenantId:{}", tenantId, e);
            throw new DeliveryNoteBusinessException(() -> e.getErrorCode().getCode(), e.getMessage());
        }
    }

    /**
     * 把对象objectApiName的fieldApiName改为非必填
     */
    public void changeFieldRequireToFalse(User user, String objectApiName, String fieldApiName) {
        //查describe
        IObjectDescribe objectDescribe = deliveryNoteObjManager.findByTenantIdAndDescribeApiName(user.getTenantId(), objectApiName);

        //找字段
        IFieldDescribe fieldDescribe = objectDescribe.getFieldDescribe(fieldApiName);

        //更新
        fieldDescribe.setRequired(false);
        try {
            objectDescribeService.updateFieldDescribe(objectDescribe, Lists.newArrayList(fieldDescribe));
        } catch (MetadataServiceException e) {
            log.warn("updateFieldDescribe failed,, objectDescribe:{}, fieldDescribeList:{}", objectDescribe, Lists.newArrayList(fieldDescribe), e);
            throw new DeliveryNoteBusinessException(com.facishare.crm.deliverynote.exception.DeliveryNoteErrorCode.UPDATE_FIELD_DESCRIBE_FAILED, "更新字段定义失败, " + e);
        }
    }

    /**
     * 对象objectApiName添加字段fieldApiNames
     */
    public void addFieldDescribes(String tenantId, String objectApiName, List<String> fieldApiNames) {
        //查询describe
        IObjectDescribe objectDescribe = deliveryNoteObjManager.findByTenantIdAndDescribeApiName(tenantId, objectApiName);

        //添加字段（添加之前，判断是否已存在对应的字段）
        addFieldDescribes(objectDescribe, objectApiName, fieldApiNames);
    }

    /**
     * 发货单产品是否有字段(describe)，没有就添加
     * 支持的字段有：
     *     发货单    的：[发货总金额、收货日期、收货备注]
     *     发货单产品的：[平均单价、本次发货金额、本次收货数、收货备注]
     */
    public void addFieldDescribes(IObjectDescribe objectDescribe, String objectApiName, List<String> fieldApiNames) {
        if (CollectionUtils.isEmpty(fieldApiNames)) {
            return;
        }

        //原来的describe是否有要添加的所有字段
        boolean hasAllField = true;
        for (String fieldApiName : fieldApiNames) {
            boolean hasFieldDescribe = hasFieldDescribe(objectDescribe, fieldApiName);

            if (!hasFieldDescribe) {
                hasAllField = false;

                if (Objects.equals(objectApiName, DeliveryNoteObjConstants.API_NAME)) {
                    objectDescribe.addFieldDescribe(objectDescribeDraftManager.getFieldForDeliveryNote(fieldApiName));
                } else if (Objects.equals(objectApiName, DeliveryNoteProductObjConstants.API_NAME)) {
                    objectDescribe.addFieldDescribe(objectDescribeDraftManager.getFieldForDeliveryNoteProduct(fieldApiName));
                }
            }
        }

        //要加的字段中，有的原来的describe是没有的，则更新describe
        if (!hasAllField) {
            deliveryNoteObjManager.replace(objectDescribe);
        }
    }

    /**
     * 是否存在某个字段的定义
     */
    public boolean hasFieldDescribe(IObjectDescribe objectDescribe, String fieldApiName) {
        //获取字段定义
        List<IFieldDescribe> fieldDescribes = null;
        fieldDescribes = objectDescribe.getFieldDescribes();

        //判断是否有字段
        for (IFieldDescribe f : fieldDescribes) {
            if (Objects.equals(f.getApiName(), fieldApiName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新对象定义的config
     */
    public void updateDescribeConfig(String tenantId, String objectApiName) {
        //开启了发货单才需要处理
        DeliveryNoteSwitchEnum deliveryNoteSwitchStatus = configManager.getDeliveryNoteStatus(tenantId);
        if (!Objects.equals(deliveryNoteSwitchStatus.getStatus(), DeliveryNoteSwitchEnum.OPENED.getStatus())) {
            return;
        }

        //查describe
        IObjectDescribe objectDescribe = deliveryNoteObjManager.findByTenantIdAndDescribeApiName(tenantId, objectApiName);

        try {
            //更新config
            objectDescribe.setConfig(ObjectUtil.buildConfigMap());
            objectDescribeService.updateDescribe(objectDescribe);
        } catch (MetadataServiceException e) {
            log.warn("updateFieldDescribe failed, objectDescribe:{}, objectApiName:{}", objectDescribe, objectApiName, e);
            throw new DeliveryNoteBusinessException(DeliveryNoteErrorCode.BUSINESS_ERROR, "更新定义失败, " + e);
        }
    }
}