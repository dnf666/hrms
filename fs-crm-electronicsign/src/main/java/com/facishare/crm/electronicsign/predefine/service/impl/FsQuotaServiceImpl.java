package com.facishare.crm.electronicsign.predefine.service.impl;

import com.facishare.crm.electronicsign.predefine.manager.FsQuotaManager;
import com.facishare.crm.electronicsign.predefine.model.vo.FsQuotaVO;
import com.facishare.crm.electronicsign.predefine.service.FsQuotaService;
import com.facishare.crm.electronicsign.predefine.service.dto.FsQuotaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FsQuotaServiceImpl implements FsQuotaService {
    @Resource
    private FsQuotaManager fsQuotaManager;

    @Override
    public FsQuotaType.GetFsQuota.Result getFsQuota() {
        FsQuotaType.GetFsQuota.Result result = new FsQuotaType.GetFsQuota.Result();
        FsQuotaVO fsQuotaVO = fsQuotaManager.getFsQuota();
        result.setFsQuotaVO(fsQuotaVO);
        return result;
    }
}
