package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.dao.SignSettingDAO;
import com.facishare.crm.electronicsign.predefine.model.SignSettingDO;
import com.facishare.crm.electronicsign.predefine.model.vo.SignSettingVO;
import com.facishare.crm.electronicsign.predefine.model.vo.SignerSettingVO;
import com.facishare.crm.electronicsign.util.CopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 签署定位关键字
 */
@Slf4j
@Service
public class SignSettingManager {
    @Resource
    private SignSettingDAO signSettingDAO;

    /**
     * 查询租户的应用签署设置
     */
    public List<SignSettingVO> query(String tenantId, AppTypeEnum appTypeEnum) {
        SignSettingDO queryEntity = new SignSettingDO();
        queryEntity.setAppType(appTypeEnum.getType());
        queryEntity.setTenantId(tenantId);
        List<SignSettingDO> signSettingDOList = signSettingDAO.queryList(queryEntity);

        return signSettingDOList.stream().map(this::transferSignSettingDO2VO).collect(Collectors.toList());
    }

    private SignSettingVO transferSignSettingDO2VO(SignSettingDO signSettingDO) {
        SignSettingVO signSettingVO = CopyUtil.copyOne(SignSettingVO.class, signSettingDO);
        if (!CollectionUtils.isEmpty(signSettingDO.getSignerSettings())) {
            signSettingVO.setSignerSettings(CopyUtil.copyMany(SignerSettingVO.class, signSettingDO.getSignerSettings()));
        }
        return signSettingVO;
    }

    /**
     * 获取关键字
     */
    public SignSettingDO getSignSettingDO(String tenantId, String appType, String objApiName) {
        SignSettingDO keywordDOArg = new SignSettingDO();
        keywordDOArg.setTenantId(tenantId);
        keywordDOArg.setAppType(appType);
        keywordDOArg.setObjApiName(objApiName);
        List<SignSettingDO> signSettingDOS = signSettingDAO.queryList(keywordDOArg);
        if (CollectionUtils.isEmpty(signSettingDOS)) {
            throw new ElecSignBusinessException(ElecSignErrorCode.NO_SIGN_SETTING_EXIST);
        }
        return signSettingDOS.get(0);
    }

    public void delete(String tenantId, String appType, String signSettingId) {
        SignSettingDO deleteCondition = new SignSettingDO();
        deleteCondition.setTenantId(tenantId);
        deleteCondition.setAppType(appType);
        deleteCondition.setId(signSettingId);
        signSettingDAO.delete(deleteCondition);
    }
}