package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.enums.type.*;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.BuyRecordDAO;
import com.facishare.crm.electronicsign.predefine.dao.FsQuotaDAO;
import com.facishare.crm.electronicsign.predefine.dao.TenantQuotaDAO;
import com.facishare.crm.electronicsign.predefine.model.BuyRecordDO;
import com.facishare.crm.electronicsign.predefine.model.TenantQuotaDO;
import com.facishare.crm.electronicsign.predefine.model.vo.Pager;
import com.facishare.crm.electronicsign.predefine.model.vo.TenantQuotaVO;
import com.facishare.crm.electronicsign.predefine.service.dto.BuyQuotaType;
import com.facishare.crm.electronicsign.predefine.service.dto.TenantQuotaType;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.open.app.center.common.utils.BeanUtil;
import com.facishare.paas.appframework.core.model.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 租户配额
 */
@Slf4j
@Service
public class TenantQuotaManager {
    @Resource
    private TenantQuotaDAO tenantQuotaDAO;
    @Resource
    private BuyRecordDAO buyRecordDAO;
    @Resource
    private FsQuotaDAO fsQuotaDAO;

    /**
     * 给租户添加初始化的数据
     */
    public void initForTenant(User user) {
        initForTenant(user, QuotaTypeEnum.INDIVIDUAL.getType());
        initForTenant(user, QuotaTypeEnum.ENTERPRISE.getType());
    }

    /**
     * 给租户添加初始化的数据
     */
    private void initForTenant(User user, String quotaType) {
        //查询是否存在
        TenantQuotaDO tenantQuotaDOArg = new TenantQuotaDO();
        tenantQuotaDOArg.setTenantId(user.getTenantId());
        tenantQuotaDOArg.setQuotaType(quotaType);
        List<TenantQuotaDO> tenantQuotaDOS = tenantQuotaDAO.queryList(tenantQuotaDOArg);

        //没有才初始化
        if (CollectionUtils.isEmpty(tenantQuotaDOS)) {
            TenantQuotaDO individual = new TenantQuotaDO();
            individual.setTenantId(user.getTenantId());
            individual.setQuotaType(quotaType);
            individual.setUsedQuota(0);
            individual.setBuyQuota(0);
            individual.setPayMoney(0L);
            individual.setCreateTime(System.currentTimeMillis());
            individual.setUpdateTime(System.currentTimeMillis());
            tenantQuotaDAO.save(individual);
        }
    }

    public List<TenantQuotaVO> getByTenantId(String tenantId) {
        List<TenantQuotaDO> tenantQuotaDOS = tenantQuotaDAO.getByTenantId(tenantId);
        List<TenantQuotaVO> tenantQuotaVOS = Lists.newArrayList();
        for (TenantQuotaDO quotaDo : tenantQuotaDOS) {
            TenantQuotaVO tenantQuotaVO = BeanUtil.copyProperties(TenantQuotaVO.class, quotaDo);
            tenantQuotaVO.setRemainedQuota(tenantQuotaVO.getBuyQuota() - tenantQuotaVO.getUsedQuota());
            tenantQuotaVOS.add(tenantQuotaVO);
        }
        return tenantQuotaVOS;
    }

    /**
     * 配额是否足够 (有一个<0就不能使用)
     * 开始不知道用的是企业配额还是租户配额，只要有一个是负数的，就不给用
     */
    public void checkQuota(User user) {
        TenantQuotaDO tenantQuotaDOArg = new TenantQuotaDO();
        tenantQuotaDOArg.setTenantId(user.getTenantId());
        List<TenantQuotaDO> tenantQuotaDOS = tenantQuotaDAO.queryList(tenantQuotaDOArg);
        for (TenantQuotaDO tenantQuotaDO : tenantQuotaDOS) {
            Integer remainedQuota = tenantQuotaDO.getBuyQuota() - tenantQuotaDO.getUsedQuota();
            if (remainedQuota < 0) {
                if (Objects.equals(tenantQuotaDO.getQuotaType(), QuotaTypeEnum.INDIVIDUAL.getType())) {
                    throw new ElecSignBusinessException(ElecSignErrorCode.INDIVIDUAL_QUOTA_NO_ENOUGH, "个人配额剩余个数为:" + remainedQuota + ", 至少必须是0个才能正常使用");
                } else if (Objects.equals(tenantQuotaDO.getQuotaType(), QuotaTypeEnum.ENTERPRISE.getType())) {
                    throw new ElecSignBusinessException(ElecSignErrorCode.ENTERPRISE_QUOTA_NO_ENOUGH, "企业配额剩余个数为:" + remainedQuota + ", 至少必须是0个才能正常使用");
                }
            }
        }
    }

    private TenantQuotaDO queryTenantQuota(String tenantId, String quotaType) {
        TenantQuotaDO tenantQuotaDOArg = new TenantQuotaDO();
        tenantQuotaDOArg.setTenantId(tenantId);
        tenantQuotaDOArg.setQuotaType(quotaType);
        List<TenantQuotaDO> tenantQuotaDOS = tenantQuotaDAO.queryList(tenantQuotaDOArg);

        if (CollectionUtils.isEmpty(tenantQuotaDOS)) {
            return null;
        }
        return tenantQuotaDOS.get(0);
    }

    /**
     * 签署完，扣除配额
     */
    public void addUsedQuota(User user, String quotaType) {
        //查配额
        TenantQuotaDO originTenantQuotaDO = queryTenantQuota(user.getTenantId(), quotaType);

        //如果配额不够，也继续扣
        if (originTenantQuotaDO == null || (originTenantQuotaDO.getBuyQuota() <= originTenantQuotaDO.getUsedQuota())) {
            log.warn("tenant quota not enough user[{}], quotaType[{}], originTenantQuotaDO[{}]", user, quotaType, originTenantQuotaDO);
        }
        //扣配额
        Integer usedQuota = originTenantQuotaDO.getUsedQuota() + 1;
        boolean hasUpdateUsedQuota = false;
        int updateUsedQuotaTryTimes = ConfigCenter.UPDATE_USED_QUOTA_TRY_TIMES;
        log.info("addUsedQuota updateUsedQuotaTryTimes[{}]", updateUsedQuotaTryTimes);
        while (!hasUpdateUsedQuota && updateUsedQuotaTryTimes > 0) {
            int updateNum = tenantQuotaDAO.updateUsedQuota(originTenantQuotaDO.getId(), usedQuota, originTenantQuotaDO.getUpdateTime());
            if (updateNum != 1) {
                updateUsedQuotaTryTimes--;
                originTenantQuotaDO = queryTenantQuota(user.getTenantId(), quotaType);
                usedQuota = originTenantQuotaDO.getUsedQuota() + 1;
                log.warn("tenantQuotaDAO.updateUsedQuota failed, id[{}], usedQuota[{}], lastUpdateTime[{}], updateNum[{}]", originTenantQuotaDO.getId(), usedQuota, originTenantQuotaDO.getUpdateTime(), updateNum);
            } else {
                hasUpdateUsedQuota = true;
                log.info("tenantQuotaDAO.updateUsedQuota success, id[{}], usedQuota[{}], lastUpdateTime[{}], updateNum[{}]", originTenantQuotaDO.getId(), usedQuota, originTenantQuotaDO.getUpdateTime(), updateNum);
            }
        }
        if (!hasUpdateUsedQuota) {
            log.error("tenantQuotaDAO.updateUsedQuota try [{}] times failed, id[{}], usedQuota[{}], lastUpdateTime[{}]", updateUsedQuotaTryTimes, originTenantQuotaDO.getId(), usedQuota, originTenantQuotaDO.getUpdateTime());
        }
    }

    public Pager<TenantQuotaVO> getTenantQuotaByPage(TenantQuotaType.GetTenantQuotaByPage.Arg arg) {
        String quotaType = arg.getQuotaType();
        if (!Strings.isNullOrEmpty(quotaType)) {
            if (!QuotaTypeEnum.get(quotaType).isPresent()) {
                log.warn("TenantQuotaManager.getTenantQuotaByPage failed arg :[{}]", arg);
                throw new ElecSignBusinessException(ElecSignErrorCode.NO_SUPPORT_QUOTA_TYPE);
            }
        }
        Pager<TenantQuotaVO> pager = new Pager<>();
        TenantQuotaDO tenantQuotaDO = new TenantQuotaDO();
        pager.setCurrentPage(arg.getCurrentPage());
        pager.setPageSize(arg.getPageSize());
        tenantQuotaDO.setQuotaType(quotaType);
        int count = tenantQuotaDAO.getTenantQuotaByQuotaTypeCounts(tenantQuotaDO);
        pager.setRecordSize(count);
        List<TenantQuotaDO> tenantQuotaDOS = tenantQuotaDAO.getTenantQuotaByQuotaTypeAndPage(tenantQuotaDO, pager);
        List<TenantQuotaVO> tenantQuotaVOS = new ArrayList<>();
        tenantQuotaDOS.forEach(e -> {
            TenantQuotaVO tenantQuotaVO = BeanUtil.copyProperties(TenantQuotaVO.class, e);
            Integer remainQuota = e.getBuyQuota() - e.getUsedQuota();
            tenantQuotaVO.setRemainedQuota(remainQuota);
            tenantQuotaVOS.add(tenantQuotaVO);
        });
        pager.setData(tenantQuotaVOS);
        return pager;


    }

    public int buyQuotaByFs(BuyQuotaType.AddBuyQuota.Arg arg) {
        String tenantId = arg.getTenantId();
        String quotaType = arg.getQuotaType();
        String payType = arg.getPayType();
        int buyQuota = arg.getBuyQuota();
        Long payMoney = 0L;
        if (QuotaTypeEnum.INDIVIDUAL.getType().equals(quotaType)) {
            payMoney = buyQuota * ConfigCenter.INDIVIDUAL_QUOTA_PRICE;
        }
        if (QuotaTypeEnum.ENTERPRISE.getType().equals(quotaType)) {
            payMoney = buyQuota * ConfigCenter.ENTERPRISE_QUOTA_PRICE;
        }
        //检查参数
        argCheck(tenantId, quotaType, payType, buyQuota);

        TenantQuotaDO tenantQuotaDO = new TenantQuotaDO();
        tenantQuotaDO.setTenantId(tenantId);
        tenantQuotaDO.setQuotaType(quotaType);
        tenantQuotaDO.setBuyQuota(buyQuota);
        tenantQuotaDO.setPayMoney(payMoney);
        //更新租户的配额信息
        int updateCount = tenantQuotaDAO.updateBuyQuotaByTenantIdAndQuotaType(tenantQuotaDO);
        if (updateCount == 1) {

            //添加配额记录
            BuyRecordDO buyRecordDO = new BuyRecordDO();
            buyRecordDO.setTenantId(tenantId);
            // 当前只能管理后台添加购买记录
            buyRecordDO.setOperatorType(BuyRecordOperatorTypeEnum.FS_ADMIN.getType());
            buyRecordDO.setQuotaType(quotaType);
            buyRecordDO.setPayMoney(payMoney);
            buyRecordDO.setBuyQuota(buyQuota);
            buyRecordDO.setPayType(payType);
            buyRecordDO.setPayMoney(payMoney);
            buyRecordDO.setBuyTime(System.currentTimeMillis());
            buyRecordDAO.save(buyRecordDO);
        } else {
            log.warn("TenantQuotaManager buyQuotaByFs 数据库没有记录 arg:[{}]", arg);
            throw new ElecSignBusinessException(ElecSignErrorCode.NO_RECORD_IN_DB);
        }

        if (QuotaTypeEnum.INDIVIDUAL.getType().equals(quotaType)) {
            //更新纷享个人配额
            return fsQuotaDAO.updateSaleIndividualQuota(buyQuota, payMoney);
        }
        if (QuotaTypeEnum.ENTERPRISE.getType().equals(quotaType)) {
            //更新纷享企业配额
            return fsQuotaDAO.updateSaleEnterpriseQuota(buyQuota, payMoney);
        }
        log.warn("TenantQuotaManager buyQuotaByFs 参数错误 quotaType:[{}]", quotaType);
        throw new ElecSignBusinessException(ElecSignErrorCode.PARAM_ERROR);

    }

    private void argCheck(String tenantId, String quotaType, String payType, int buyQuota) {
        if (Strings.isNullOrEmpty(tenantId) || Strings.isNullOrEmpty(quotaType) || Strings.isNullOrEmpty(payType)) {
            log.warn("TenantQuotaManager argCheck 有参数为空 tenantId:[{}],quotaType:[{}],payType:[{}]", tenantId, quotaType, payType);
            throw new ElecSignBusinessException(ElecSignErrorCode.ARG_NULL_ERROR);
        }
        if (!QuotaTypeEnum.get(quotaType).isPresent()) {
            log.warn("TenantQuotaManager argCheck 不支持租户类型 quotaType:[{}]", quotaType);
            throw new ElecSignBusinessException(ElecSignErrorCode.NO_SUPPORT_QUOTA_TYPE);
        }

        if (buyQuota == 0) {
            log.warn("TenantQuotaManager argCheck 购买配额不能为0 buyQuota:[{}]", buyQuota);
            throw new ElecSignBusinessException(ElecSignErrorCode.ARG_NULL_ERROR);
        }
    }
}