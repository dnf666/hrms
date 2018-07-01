package com.facishare.crm.customeraccount.predefine.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.facishare.crm.customeraccount.dao.CustomerAccountConfigDao;
import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.facishare.crm.customeraccount.predefine.manager.RebateIncomeDetailManager;
import com.facishare.crm.customeraccount.predefine.service.dto.CustomerAccountType;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EffectiveRebateIncomeJob implements Job {

    /**
     * 1.从customerAccountConfig表中获取所有企业<br>
     * 2.遍历每个生效的收入明细，增加客户账户返利余额<br>
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("start EffectiveRebateIncomeJob");
        RebateIncomeDetailManager rebateIncomeDetailManager = SpringUtil.getContext().getBean(RebateIncomeDetailManager.class);
        CustomerAccountConfigDao customerAccountConfigDao = SpringUtil.getContext().getBean(CustomerAccountConfigDao.class);
        List<CustomerAccountConfig> customerAccountConfigList = customerAccountConfigDao.list(CustomerAccountType.CustomerAccountEnableSwitchStatus.ENABLE.getValue());
        // 查询所有企业
        for (CustomerAccountConfig customerAccountConfig : customerAccountConfigList) {
            String tenantId = customerAccountConfig.getTenantId();
            try {
                int listSize = 0;
                int size = 100;
                int offset = 0;
                int successSize = 0;
                do {
                    List<IObjectData> enabledRebateList = rebateIncomeDetailManager.listNowDayEnableRebateIncomeDetails(tenantId, offset, size);
                    int result = rebateIncomeDetailManager.batchEnableRebateIncomeDatails(enabledRebateList);
                    listSize = enabledRebateList.size();
                    successSize = result + successSize;
                    offset = offset + listSize;
                } while (listSize == size);
                log.info("EffectiveRebateIncomeJob,tenantId={},totalSize={},successSize={}", customerAccountConfig.getTenantId(), offset, successSize);
            } catch (Exception e) {
                log.error(" EffectiveRebateIncomeJob error correct  customerAccount balance,for tenantId:{}", customerAccountConfig.getTenantId());
            }
        }
    }

}
