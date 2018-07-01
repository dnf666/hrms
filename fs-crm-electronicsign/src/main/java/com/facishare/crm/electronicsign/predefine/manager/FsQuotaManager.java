package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.predefine.dao.FsQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.FsQuotaDO;
import com.facishare.crm.electronicsign.predefine.model.vo.FsQuotaVO;
import com.facishare.open.app.center.common.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 纷享配额
 *
 * @author dailf
 */
@Slf4j
@Service
public class FsQuotaManager {
    @Resource
    private FsQuotaDAO fsQuotaDAO;

    /**
     * 取得纷享配额
     * @return 纷享配额
     */
    public FsQuotaVO getFsQuota() {
        FsQuotaDO fsQuotaDO = fsQuotaDAO.getFsQuota();
        return BeanUtil.copyProperties(FsQuotaVO.class, fsQuotaDO);
    }

}