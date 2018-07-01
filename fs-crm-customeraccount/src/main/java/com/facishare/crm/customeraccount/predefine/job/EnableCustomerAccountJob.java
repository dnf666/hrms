package com.facishare.crm.customeraccount.predefine.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.facishare.crm.customeraccount.dao.CustomerAccountConfigDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.predefine.manager.CustomerAccountManager;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnableCustomerAccountJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("start EnableCustomerAccountJob");
        CustomerAccountManager customerAccountManager = SpringUtil.getContext().getBean(CustomerAccountManager.class);
        CustomerAccountConfigDao customerAccountConfigDao = SpringUtil.getContext().getBean(CustomerAccountConfigDao.class);
        List<CustomerAccountConfig> customerAccountConfigList = customerAccountConfigDao.findEnterpriseWithCustomerAccountOpening();
        for (CustomerAccountConfig customerAccountConfig : customerAccountConfigList) {
            try {
                boolean success = customerAccountManager.batchInitCustomerAccounts(customerAccountConfig.getTenantId());
                log.info("set customerAccount Enable,for tenantId:{},result={}", customerAccountConfig.getTenantId(), success);
            } catch (Exception e) {
                log.error("error opening customerAccount,for tenantId:{}", customerAccountConfig.getTenantId());
            }
        }

    }

}
