package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.predefine.manager.BuyRecordManager;
import com.facishare.crm.electronicsign.predefine.model.vo.BuyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.service.BuyRecordService;
import com.facishare.crm.electronicsign.predefine.service.dto.BuyRecordType;
import com.facishare.paas.appframework.core.model.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class BuyRecordServiceImpl implements BuyRecordService {
    @Resource
    private BuyRecordManager buyRecordManager;

    @Override
    public BuyRecordType.QueryByPage.Result queryByPageForFsManage(ServiceContext serviceContext, BuyRecordType.QueryByPage.Arg arg) {
        BuyRecordType.QueryByPage.Result result = new BuyRecordType.QueryByPage.Result();
        Pager<BuyRecordVO> data = buyRecordManager.getBuyRecordByPage(arg);
        result.setPager(data);
        return result;
    }

    @Override
    public BuyRecordType.QueryByPage.Result queryByPage(ServiceContext serviceContext, BuyRecordType.QueryByPage.Arg arg) {
        BuyRecordType.QueryByPage.Result result = new BuyRecordType.QueryByPage.Result();
        arg.setTenantId(serviceContext.getTenantId());
        Pager<BuyRecordVO> data = buyRecordManager.getBuyRecordByPage(arg);
        result.setPager(data);
        return result;
    }
}