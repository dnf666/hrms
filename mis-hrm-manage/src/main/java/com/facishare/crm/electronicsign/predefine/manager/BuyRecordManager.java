package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.enums.type.BuyRecordOperatorTypeEnum;
import com.facishare.crm.electronicsign.enums.type.QuotaTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.BuyRecordDAO;
import com.facishare.crm.electronicsign.predefine.model.BuyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.vo.BuyRecordVO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.service.dto.BuyRecordType;
import com.facishare.open.app.center.common.utils.BeanUtil;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 购买记录
 *
 * @author dailf
 */
@Slf4j
@Service
public class BuyRecordManager {
    @Resource
    private BuyRecordDAO buyRecordDAO;
    @Autowired
    private ServiceFacade serviceFacade;

    /**
     * 获取最新的number条
     */
    public List<BuyRecordVO> getLastBuyRecordVOs(String tenantId, int number) {
        BuyRecordDO buyRecordDO = new BuyRecordDO();
        buyRecordDO.setTenantId(tenantId);

        Pager<BuyRecordVO> pager = new Pager<>();
        pager.setCurrentPage(1);
        pager.setPageSize(number);

        return getListByPage(buyRecordDO, null, null, pager);
    }

    /**
     * 分页查询BuyRecordVO
     */
    public List<BuyRecordVO> getListByPage(BuyRecordDO buyRecordDO, Long startTime, Long endTime, Pager<BuyRecordVO> pager) {
        List<BuyRecordDO> buyRecordDOS = buyRecordDAO.getTenantBuyQuotaRecordsByConditionAndPage(buyRecordDO, startTime, endTime, pager);

        List<BuyRecordVO> buyRecordVOS = new ArrayList<>();
        for (BuyRecordDO buyRecord : buyRecordDOS) {
            BuyRecordVO buyRecordVO = BeanUtil.copyProperties(BuyRecordVO.class, buyRecord);
            if (Objects.equals(buyRecord.getOperatorType(), BuyRecordOperatorTypeEnum.FS_ADMIN.getType())) {
                buyRecordVO.setOperatorName("系统");
            } else {
                User user = serviceFacade.getUser(buyRecord.getTenantId(), buyRecord.getOperatorId().toString());
                buyRecordVO.setOperatorName(user.getUserName());
            }
            buyRecordVOS.add(buyRecordVO);
        }
        return buyRecordVOS;
    }

    public Pager<BuyRecordVO> getBuyRecordByPage(BuyRecordType.QueryByPage.Arg arg) {
        Pager<BuyRecordVO> pager = new Pager<>();
        BuyRecordDO buyRecordDO = new BuyRecordDO();

        int currentPage = arg.getCurrentPage();
        int pageSize = arg.getPageSize();
        pager.setCurrentPage(currentPage);
        pager.setPageSize(pageSize);
        String payType = arg.getPayType();
        String quotaType = arg.getQuotaType();
        if (!Strings.isNullOrEmpty(quotaType)) {
            if (!QuotaTypeEnum.get(quotaType).isPresent()) {
                log.warn("BuyRecordManager getBuyRecordByPage 不支持租户类型 quotaType:[{}]",quotaType);
                throw new ElecSignBusinessException(ElecSignErrorCode.NO_SUPPORT_QUOTA_TYPE);
            }
        }
        buyRecordDO.setPayType(payType);
        buyRecordDO.setQuotaType(quotaType);
        buyRecordDO.setTenantId(arg.getTenantId());

        Long startTime = arg.getStartTime();
        Long endTime = arg.getEndTime();
        int count = buyRecordDAO.getTenantBuyQuotaRecordsByConditionCount(buyRecordDO, startTime, endTime);
        pager.setRecordSize(count);
        pager.setData(getListByPage(buyRecordDO, startTime, endTime, pager));
        return pager;
    }
}