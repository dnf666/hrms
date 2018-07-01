package com.facishare.crm.customeraccount.predefine.manager;

import com.facishare.crm.customeraccount.dao.CustomerAccountConfigDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class CustomerAccountConfigManager {
    @Autowired
    private CustomerAccountConfigDao customerAccountConfigDao;

    public void updateStatus(User user, CustomerAccountType.CustomerAccountEnableSwitchStatus stauts) {
        CustomerAccountConfig config = customerAccountConfigDao.findByTenantId(user.getTenantId());
        if (config == null) {
            Date date = new Date();
            config = new CustomerAccountConfig();
            config.setTenantId(user.getTenantId());
            config.setCreateTime(date);
            config.setUpdateBy(user.getUserId());
            config.setCreateBy(user.getUserId());
            config.setUpdateTime(date);
            config.setCreditEnable(false);
            config.setCustomerAccountEnable(stauts.getValue());
            customerAccountConfigDao.insert(config);
        } else {
            config.setCustomerAccountEnable(stauts.getValue());
            config.setUpdateTime(new Date());
            config.setUpdateBy(user.getUserId());
            customerAccountConfigDao.update(config);
        }
    }

    public CustomerAccountType.CustomerAccountEnableSwitchStatus getStatus(String tenantId) {
        CustomerAccountConfig config = customerAccountConfigDao.findByTenantId(tenantId);
        if (config == null) {
            log.info("CustomerAccountConfig ==null,for tenantId:{}", tenantId);
            return CustomerAccountType.CustomerAccountEnableSwitchStatus.UNABLE;
        }
        return CustomerAccountType.CustomerAccountEnableSwitchStatus.valueOf(config.getCustomerAccountEnable());
    }

    public boolean isCustomerAccountEnable(String tenantId) {
        CustomerAccountType.CustomerAccountEnableSwitchStatus customerAccountEnableSwitchStatus = this.getStatus(tenantId);
        if (customerAccountEnableSwitchStatus.getValue() != CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE.getValue()) {
            //除了enable 状态，其他都返回false<br>
            log.info("customer account switch status is not enable,for status:{}", customerAccountEnableSwitchStatus.getValue());
            return false;
        } else {
            return true;
        }
    }

    public CustomerAccountConfig getConfigByTenantId(String tenantId) {
        return customerAccountConfigDao.findByTenantId(tenantId);
    }

    public CustomerAccountConfig getCutomerAccountConfig(String tenantId) {
        CustomerAccountConfig config = customerAccountConfigDao.findByTenantId(tenantId);
        return config;

    }

    public boolean isCreditEnable(String tenantId) {
        CustomerAccountConfig customerAccountConfig = this.getConfigByTenantId(tenantId);
        if (customerAccountConfig == null) {
            return false;
        }
        return customerAccountConfig.isCreditEnable();

    }

}
