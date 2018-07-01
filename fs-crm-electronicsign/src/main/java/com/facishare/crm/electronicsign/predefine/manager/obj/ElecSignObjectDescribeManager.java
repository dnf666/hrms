package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignRecordObjConstants;
import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.crm.electronicsign.enums.type.AccountSignCertifyLayoutTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.DescribeLogicService;
import com.facishare.paas.appframework.metadata.LayoutLogicService;
import com.facishare.paas.appframework.metadata.dto.DescribeResult;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.service.IObjectDescribeService;
import com.facishare.paas.metadata.exception.MetadataServiceException;
import com.facishare.paas.metadata.ui.layout.ILayout;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 对象定义
 */
@Service
@Slf4j
public class ElecSignObjectDescribeManager {
    @Resource
    private IObjectDescribeService objectDescribeService;
    @Resource
    private DescribeLogicService describeLogicService;
    @Autowired
    private ElecSignObjectDescribeDraftManager elecSignObjectDescribeDraftManager;
    @Autowired
    private ElecSignLayoutManager elecSignLayoutManager;
    @Autowired
    private LayoutLogicService layoutLogicService;

    public Set<String> getExistDisplayName(String tenantId) {
        try {
            //内部签章认证
            Set<String> existDisplayNames = Sets.newHashSet();
            List<String> existInternalSignCertifyApiNames = objectDescribeService.checkDisplayNameExist(tenantId, InternalSignCertifyObjConstants.DISPLAY_NAME, "CRM");
            existInternalSignCertifyApiNames.forEach(x -> {
                if (!InternalSignCertifyObjConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(InternalSignCertifyObjConstants.DISPLAY_NAME);
                }
            });

            //客户签章认证
            List<String> existAccountSignCertifyApiNames = objectDescribeService.checkDisplayNameExist(tenantId, AccountSignCertifyObjConstants.DISPLAY_NAME, "CRM");
            existAccountSignCertifyApiNames.forEach(x -> {
                if (!AccountSignCertifyObjConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(AccountSignCertifyObjConstants.DISPLAY_NAME);
                }
            });

            //签署记录
            List<String> existSignRecordApiNames = objectDescribeService.checkDisplayNameExist(tenantId, SignRecordObjConstants.DISPLAY_NAME, "CRM");
            existSignRecordApiNames.forEach(x -> {
                if (!SignRecordObjConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(SignRecordObjConstants.DISPLAY_NAME);
                }
            });

            //签署方
            List<String> existSignerApiNames = objectDescribeService.checkDisplayNameExist(tenantId, SignerObjConstants.DISPLAY_NAME, "CRM");
            existSignerApiNames.forEach(x -> {
                if (!SignerObjConstants.API_NAME.equals(x)) {
                    existDisplayNames.add(SignerObjConstants.DISPLAY_NAME);
                }
            });

            log.debug("getExistDisplayName tenantId:{}, Result:{}", tenantId, existDisplayNames);
            return existDisplayNames;
        } catch (MetadataServiceException e) {
            log.warn("getExistDisplayName error,tenantId:{}", tenantId, e);
            throw new ElecSignBusinessException(() -> e.getErrorCode().getCode(), e.getMessage());
        }
    }

    /**
     * 创建"内部签章认证"对象
     */
    public DescribeResult createInternalSignCertifyDescribeAndLayout(String tenantId, String userId) {
        IObjectDescribe objectDescribeDraft = elecSignObjectDescribeDraftManager.generateInternalSignCertifyDescribeDraft(tenantId, userId);

        String defaultLayoutApiName = InternalSignCertifyObjConstants.DEFAULT_LAYOUT_API_NAME;
        String defaultLayoutDisplayName = InternalSignCertifyObjConstants.DEFAULT_LAYOUT_DISPLAY_NAME;
        String refObjectApiName = InternalSignCertifyObjConstants.API_NAME;
        ILayout detailLayout = elecSignLayoutManager.generateInternalSignCertifyDetailLayout(tenantId, userId, defaultLayoutApiName, defaultLayoutDisplayName, refObjectApiName);

        String listLayoutApiName = InternalSignCertifyObjConstants.LIST_LAYOUT_API_NAME;
        String listLayoutDisplayName = InternalSignCertifyObjConstants.LIST_LAYOUT_DISPLAY_NAME;
        ILayout listLayout = elecSignLayoutManager.generateListLayout(tenantId, userId, listLayoutApiName, listLayoutDisplayName, refObjectApiName);

        return createDescribe(tenantId, userId, objectDescribeDraft, detailLayout, listLayout);
    }

    /**
     * 创建"客户签章认证"对象
     */
    public DescribeResult createAccountSignCertifyDescribeAndLayout(User user, String userId) {
        String tenantId = user.getTenantId();
        IObjectDescribe objectDescribeDraft = elecSignObjectDescribeDraftManager.generateAccountSignCertifyDescribeDraft(tenantId, userId);
        ILayout detailLayout = elecSignLayoutManager.generateAccountSignCertifyLayout(tenantId, userId, AccountSignCertifyLayoutTypeEnum.DEFAULT, AccountSignCertifyObjConstants.DEFAULT_LAYOUT_API_NAME, AccountSignCertifyObjConstants.DEFAULT_LAYOUT_DISPLAY_NAME);
        ILayout enterpriseLayout = elecSignLayoutManager.generateAccountSignCertifyLayout(tenantId, userId, AccountSignCertifyLayoutTypeEnum.ENTERPRISE, AccountSignCertifyObjConstants.ENTERPRISE_LAYOUT_API_NAME, AccountSignCertifyObjConstants.ENTERPRISE_LAYOUT_DISPLAY_NAME);
        ILayout individualLayout = elecSignLayoutManager.generateAccountSignCertifyLayout(tenantId, userId, AccountSignCertifyLayoutTypeEnum.INDIVIDUAL, AccountSignCertifyObjConstants.INDIVIDUAL_LAYOUT_API_NAME, AccountSignCertifyObjConstants.INDIVIDUAL_LAYOUT_DISPLAY_NAME);
        ILayout listLayout = elecSignLayoutManager.generateAccountSignCertifyListLayout(tenantId, userId);

        DescribeResult result = createDescribe(tenantId, userId, objectDescribeDraft, detailLayout, listLayout);

        enterpriseLayout = layoutLogicService.createLayout(user, enterpriseLayout);
        log.info("createLayout,user:{},layout:{}", user, enterpriseLayout);
        individualLayout = layoutLogicService.createLayout(user, individualLayout);
        log.info("createLayout,user:{},layout:{}", user, individualLayout);
        return result;
    }

    /**
     * 创建"签署记录"对象
     */
    public DescribeResult createSignRecordDescribeAndLayout(String tenantId, String userId) {
        IObjectDescribe objectDescribeDraft = elecSignObjectDescribeDraftManager.generateSignRecordDescribeDraft(tenantId, userId);

        String defaultLayoutApiName = SignRecordObjConstants.DEFAULT_LAYOUT_API_NAME;
        String defaultLayoutDisplayName = SignRecordObjConstants.DEFAULT_LAYOUT_DISPLAY_NAME;
        String refObjectApiName = SignRecordObjConstants.API_NAME;
        ILayout detailLayout = elecSignLayoutManager.generateDetailLayout(tenantId, userId, defaultLayoutApiName, defaultLayoutDisplayName, refObjectApiName);

        String listLayoutApiName = SignRecordObjConstants.LIST_LAYOUT_API_NAME;
        String listLayoutDisplayName = SignRecordObjConstants.LIST_LAYOUT_DISPLAY_NAME;
        ILayout listLayout = elecSignLayoutManager.generateListLayout(tenantId, userId, listLayoutApiName, listLayoutDisplayName, refObjectApiName);

        return createDescribe(tenantId, userId, objectDescribeDraft, detailLayout, listLayout);
    }

    /**
     * 创建"签署方"对象
     */
    public DescribeResult createSignerDescribeAndLayout(String tenantId, String userId) {
        IObjectDescribe objectDescribeDraft = elecSignObjectDescribeDraftManager.generateSignerDescribeDraft(tenantId, userId);

        String defaultLayoutApiName = SignerObjConstants.DEFAULT_LAYOUT_API_NAME;
        String defaultLayoutDisplayName = SignerObjConstants.DEFAULT_LAYOUT_DISPLAY_NAME;
        String refObjectApiName = SignerObjConstants.API_NAME;
        ILayout detailLayout = elecSignLayoutManager.generateDetailLayout(tenantId, userId, defaultLayoutApiName, defaultLayoutDisplayName, refObjectApiName);

        String listLayoutApiName = SignerObjConstants.LIST_LAYOUT_API_NAME;
        String listLayoutDisplayName = SignerObjConstants.LIST_LAYOUT_DISPLAY_NAME;
        ILayout listLayout = elecSignLayoutManager.generateListLayout(tenantId, userId, listLayoutApiName, listLayoutDisplayName, refObjectApiName);

        return createDescribe(tenantId, userId, objectDescribeDraft, detailLayout, listLayout);
    }

    private DescribeResult createDescribe(String tenantId, String fsUserId, IObjectDescribe objectDescribeDraft, ILayout detailLayout, ILayout listLayout) {
        String describeJson = objectDescribeDraft.toJsonString();
        String detailLayoutJson = detailLayout.toJsonString();
        String listLayoutJson = listLayout.toJsonString();
        User user = new User(tenantId, fsUserId);
        DescribeResult describeResult = describeLogicService.createDescribe(user, describeJson, detailLayoutJson, listLayoutJson, true, true);
        log.info("describeLogicService.createDescribe user:{}, jsonData:{}, jsonLayout:{}, jsonListLayout:{}, isActive:{}, isIncludeLayout:{}, result:{}",
                user, describeJson, detailLayoutJson, listLayoutJson, true, true, describeResult);
        return describeResult;
    }
}