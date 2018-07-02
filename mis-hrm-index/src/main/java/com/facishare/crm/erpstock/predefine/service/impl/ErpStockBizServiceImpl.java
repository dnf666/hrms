package com.facishare.crm.erpstock.predefine.service.impl;

import com.facishare.common.proxy.helper.StringUtils;
import com.facishare.crm.erpstock.enums.YesOrNoEnum;
import com.facishare.crm.erpstock.exception.ErpStockBusinessException;
import com.facishare.crm.erpstock.exception.ErpStockErrorCode;
import com.facishare.crm.erpstock.predefine.manager.ErpStockConfigManager;
import com.facishare.crm.erpstock.predefine.manager.ErpStockInitManager;
import com.facishare.crm.erpstock.predefine.manager.ErpStockManager;
import com.facishare.crm.erpstock.predefine.service.ErpStockBizService;
import com.facishare.crm.erpstock.predefine.service.dto.ErpOrderCheckType;
import com.facishare.crm.erpstock.predefine.service.dto.ErpStockType;
import com.facishare.crm.erpstock.predefine.service.model.IsErpStockEnableModel;
import com.facishare.crm.erpstock.predefine.service.model.QueryErpStockConfigModel;
import com.facishare.crm.erpstock.predefine.service.model.SaveErpStockConfigModel;
import com.facishare.paas.appframework.core.model.ServiceContext;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author linchf
 * @date 2018/5/14
 */
@Slf4j(topic = "erpStockAccess")
@Component
public class ErpStockBizServiceImpl implements ErpStockBizService {
    @Resource
    private ErpStockInitManager erpStockInitManager;

    @Resource
    private ErpStockManager erpStockManager;

    @Resource
    private ErpStockConfigManager erpStockConfigManager;

    @Override
    public ErpStockType.EnableErpStockResult enableErpStock(ServiceContext serviceContext) throws MetadataServiceException {
        ErpStockType.EnableErpStockResult enableErpStockResult = new ErpStockType.EnableErpStockResult();
        //纷享发货单是否开启
        if (erpStockManager.isDeliveryNoteEnable(serviceContext.getUser())) {
            throw new ErpStockBusinessException(ErpStockErrorCode.INIT_ERROR, "纷享发货单已经开启，不能开启ERP库存");
        }

        //纷享库存是否开启
        if (erpStockManager.isStockEnable(serviceContext.getUser())) {
            throw new ErpStockBusinessException(ErpStockErrorCode.INIT_ERROR, "纷享库存已经开启，不能开启ERP库存");
        }

        //ERP库存是否开启
        if (erpStockManager.isErpStockEnable(serviceContext.getTenantId())) {
            throw new ErpStockBusinessException(ErpStockErrorCode.INIT_ERROR, "ERP库存已经开启");
        }

        //校验对象重名
        Set<String> existDisplayNames = erpStockInitManager.checkExistDisplayName(serviceContext.getTenantId());
        if (!CollectionUtils.isEmpty(existDisplayNames)) {
            String existDisplayNameString = StringUtils.join(existDisplayNames, "、");
            log.warn("init erpStock failed. existDisplayNames[{}]", existDisplayNameString);
            throw new ErpStockBusinessException(ErpStockErrorCode.INIT_ERROR, existDisplayNameString + "对象名称已存在");
        }

        //对象初始化
        try {
            erpStockInitManager.init(serviceContext.getUser());
            log.info("init erpStock describe success.");

            //更新开关
            erpStockConfigManager.insertOrUpdateErpStockSwitch(serviceContext.getUser(), ErpStockType.ErpStockSwitchEnum.ENABLE);
            enableErpStockResult.setEnableStatus(ErpStockType.ErpStockSwitchEnum.ENABLE.getStatus());
            enableErpStockResult.setMessage("开启成功");
        } catch (Exception e) {
            //初始化失败 更新失败状态
            log.warn("erpStock init failed.");
            erpStockConfigManager.insertOrUpdateErpStockSwitch(serviceContext.getUser(), ErpStockType.ErpStockSwitchEnum.FAILED);
            throw e;
        }

        return enableErpStockResult;
    }

    @Override
    public ErpStockType.CloseErpStockResult closeErpStock(ServiceContext serviceContext) {
        ErpStockType.CloseErpStockResult closeErpStockResult = new ErpStockType.CloseErpStockResult();

        //更新开关
        erpStockConfigManager.insertOrUpdateErpStockSwitch(serviceContext.getUser(), ErpStockType.ErpStockSwitchEnum.UNABLE);
        closeErpStockResult.setEnableStatus(ErpStockType.ErpStockSwitchEnum.UNABLE.getStatus());
        closeErpStockResult.setMessage("关闭成功");

        return closeErpStockResult;
    }

    @Override
    public SaveErpStockConfigModel.Result saveErpStockConfig(ServiceContext serviceContext, SaveErpStockConfigModel.Arg arg) {
        //1、是否是Crm管理员

        //2、校验库存是否开启
        Boolean hasSwitched = erpStockManager.isErpStockEnable(serviceContext.getUser().getTenantId());
        if (!hasSwitched) {
            throw new ErpStockBusinessException(ErpStockErrorCode.BUSINESS_ERROR, "库存未开启");
        }

        //3、保存库存设置
        SaveErpStockConfigModel.Result result = new SaveErpStockConfigModel.Result();
        result.setIsSuccess(true);

        if (!StringUtils.isBlank(arg.getValidateOrderType())) {
            erpStockConfigManager.updateErpOrderCheckType(serviceContext.getUser(),
                    ErpOrderCheckType.OrderCheckTypeEnum.valueOf(Integer.valueOf(arg.getValidateOrderType())));
        }

        if (!StringUtils.isBlank(arg.getIsNotShowZeroStockType())) {
            erpStockConfigManager.updateErpIsNotShowZeroStock(serviceContext.getUser(),
                    YesOrNoEnum.valueOf(Integer.valueOf(arg.getIsNotShowZeroStockType())));
        }

        return result;
    }

    @Override
    public QueryErpStockConfigModel.Result queryErpStockConfig(ServiceContext serviceContext) {
        QueryErpStockConfigModel.Result result = new QueryErpStockConfigModel.Result();

        //获取ERP库存开关 默认不能开启
        ErpStockType.ErpStockSwitchEnum erpStockSwitchEnum = erpStockConfigManager.getErpStockSwitch(serviceContext.getUser().getTenantId());
        switch (erpStockSwitchEnum) {
            case ENABLE: {
                result.setEnable(true);
            }
        }

        //获取订单检查配置  默认库存不足，不允许提交订单
        ErpOrderCheckType.OrderCheckTypeEnum orderCheckTypeEnum = erpStockConfigManager.getErpOrderCheckType(serviceContext.getUser());
        result.setValidateOrderType(orderCheckTypeEnum.getStringStatus());

        //获取是否展示库存为0的数据
        YesOrNoEnum isNotShowZeroStockEnum = erpStockConfigManager.getErpIsNotShowZeroStock(serviceContext.getTenantId());
        result.setIsNotShowZeroStockType(isNotShowZeroStockEnum.getStringStatus());

        return result;
    }

    @Override
    public IsErpStockEnableModel.Result isErpStockEnable(ServiceContext serviceContext) {
        IsErpStockEnableModel.Result result = new IsErpStockEnableModel.Result();
        result.setIsEnable(erpStockManager.isErpStockEnable(serviceContext.getTenantId()));
        return result;
    }
}
