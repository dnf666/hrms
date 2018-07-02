package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignRecordObjConstants;
import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.crm.electronicsign.enums.OriginEnum;
import com.facishare.crm.electronicsign.enums.type.QuotaTypeEnum;
import com.facishare.crm.electronicsign.enums.type.SignerTypeEnum;
import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.crm.electronicsign.predefine.manager.CommonManager;
import com.facishare.crm.electronicsign.predefine.manager.SignRequestManager;
import com.facishare.crm.electronicsign.predefine.model.SignRequestDO;
import com.facishare.crm.electronicsign.predefine.model.SignerDO;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRecordType;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.util.SearchUtil;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NUploadFileDirect;
import com.facishare.paas.appframework.core.model.ServiceFacade;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.metadata.dto.SaveMasterAndDetailData;
import com.facishare.paas.common.util.UdobjConstants;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.MultiRecordType;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.impl.ObjectData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class SignRecordObjManager extends CommonManager {
    private static final int MAX_LIMIT_FOR_QUERY_ALL = 1000000;

    @Resource
    private ServiceFacade serviceFacade;
    @Autowired
    private SignRequestManager signRequestManager;
    @Resource
    private InternalSignCertifyObjManager internalSignCertifyObjManager;
    @Resource
    private AccountSignCertifyObjManager accountSignCertifyObjManager;

    /**
     * 增加"签署记录"
     */
    public void saveSignRecord(User user, SignRequestDO signRequestDO, String contractId, NUploadFileDirect.Result uploadResult, String quotaType) {
        //origin
        String origin = getOrigin(user.getTenantId(), signRequestDO.getObjApiName());

        // TODO: 2018/5/22 chenzs 加字段要调整 
        //salesOrderId、accountStatementId、deliveryNoteId
        String salesOrderId = null;
        String accountStatementId = null;
        String deliveryNoteId = null;
        if (Objects.equals(origin, OriginEnum.SALES_ORDER.getType())) {
            salesOrderId = signRequestDO.getObjDataId();
        } else if (Objects.equals(origin, OriginEnum.ACCOUNT_STATEMENT.getType())) {
            accountStatementId = signRequestDO.getObjDataId();
        } else if (Objects.equals(origin, OriginEnum.DELIVERY_NOTE.getType())) {
            deliveryNoteId = signRequestDO.getObjDataId();
        }

        //附件
        Map<String, Object> contractFileAttachment = new HashMap<>();
        if (uploadResult != null) {
            contractFileAttachment.put("create_time", System.currentTimeMillis());
            contractFileAttachment.put("size", uploadResult.getFileSize());
            contractFileAttachment.put("filename", signRequestDO.getContractFileAttachmentName());
            contractFileAttachment.put("ext", "pdf");
            contractFileAttachment.put("path", uploadResult.getFinalNPath());
        }
        List<Map<String, Object>> contractFileAttachments = Lists.newArrayList(contractFileAttachment);

        SaveMasterAndDetailData.Arg arg = buildSignRecord(user, quotaType, signRequestDO.getAppType(), origin, salesOrderId, accountStatementId, deliveryNoteId, contractId, contractFileAttachments, signRequestDO.getSigners());
        serviceFacade.saveMasterAndDetailData(user, arg);
    }

    private SaveMasterAndDetailData.Arg buildSignRecord(User user, String quotaType, String appType, String origin, String salesOrderId, String accountStatementId, String deliveryNoteId, String contractId, List<Map<String, Object>> contractFileAttachments, List<SignerDO> signerDOS) {
        //1 查定义
        IObjectDescribe masterDescribe = serviceFacade.findObject(user.getTenantId(), SignRecordObjConstants.API_NAME);
        IObjectDescribe detailDescribe = serviceFacade.findObject(user.getTenantId(), SignerObjConstants.API_NAME);
        if (masterDescribe == null || detailDescribe == null) {
            log.warn("build buildSignRecord failed. describe is null. user[{}], masterDescribe[{}], detailDescribe[{}]", user, masterDescribe, detailDescribe);
            return null;
        }

        //2 查数据（内部签章认证id、客户签章认证id）
        List<String> tenantBestSignAccounts = Lists.newArrayList();
        List<String> userBestSignAccounts = Lists.newArrayList();
        signerDOS.forEach(signerDO -> {
            if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.CRM_ACCOUNT.getType())) {
                userBestSignAccounts.add(signerDO.getBestSignAccount());
            } else if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.TENANT.getType())) {
                tenantBestSignAccounts.add(signerDO.getBestSignAccount());
            }
        });
        Map<String, String>  tenantBestSignAccount2Ids = internalSignCertifyObjManager.queryIdsByBestSignAccounts(user, tenantBestSignAccounts);
        Map<String, String>  userBestSignAccount2Ids = accountSignCertifyObjManager.queryIdsByBestSignAccounts(user, userBestSignAccounts);

        //2 主对象
        // TODO: 2018/5/22 chenzs 加字段要调整
        IObjectData masterObj = new ObjectData();
        masterObj.set(SignRecordObjConstants.Field.QuotaType.apiName, quotaType);
        masterObj.set(SignRecordObjConstants.Field.AppType.apiName, appType);
        masterObj.set(SignRecordObjConstants.Field.Origin.apiName, origin);
        masterObj.set(SignRecordObjConstants.Field.SalesOrderId.apiName, salesOrderId);
        masterObj.set(SignRecordObjConstants.Field.AccountStatementId.apiName, accountStatementId);
        masterObj.set(SignRecordObjConstants.Field.DeliveryNoteId.apiName, deliveryNoteId);
        masterObj.set(SignRecordObjConstants.Field.ContractId.apiName, contractId);
        masterObj.set(SignRecordObjConstants.Field.ContractFileAttachment.apiName, contractFileAttachments);

        masterObj.setTenantId(user.getTenantId());
        masterObj.setCreatedBy(User.SUPPER_ADMIN_USER_ID);
        masterObj.setLastModifiedBy(User.SUPPER_ADMIN_USER_ID);
        masterObj.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(User.SUPPER_ADMIN_USER_ID));
        masterObj.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
        masterObj.set(IObjectData.DESCRIBE_ID, masterDescribe.getId());
        masterObj.set(IObjectData.DESCRIBE_API_NAME, SignRecordObjConstants.API_NAME);
        masterObj.set(IObjectData.PACKAGE, "CRM");
        masterObj.set(IObjectData.VERSION, masterDescribe.getVersion());

        //3 从对象
        Map<String, List<IObjectData>> detailObjectMap = new HashMap<>();
        List<IObjectData> detailObjects = Lists.newArrayList();
        signerDOS.forEach(signerDO -> {
            IObjectData productObj = new ObjectData();
            if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.CRM_ACCOUNT.getType())) {
                productObj.set(SignerObjConstants.Field.AccountSignCertifyId.apiName, userBestSignAccount2Ids.get(signerDO.getBestSignAccount()));
            } else if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.TENANT.getType())) {
                productObj.set(SignerObjConstants.Field.InternalSignCertifyId.apiName, tenantBestSignAccount2Ids.get(signerDO.getBestSignAccount()));
            }

            productObj.setTenantId(user.getTenantId());
            productObj.setCreatedBy(User.SUPPER_ADMIN_USER_ID);
            productObj.setLastModifiedBy(User.SUPPER_ADMIN_USER_ID);
            productObj.set(UdobjConstants.OWNER_API_NAME, Arrays.asList(User.SUPPER_ADMIN_USER_ID));
            productObj.setRecordType(MultiRecordType.RECORD_TYPE_DEFAULT);
            productObj.set(IObjectData.DESCRIBE_ID, detailDescribe.getId());
            productObj.set(IObjectData.DESCRIBE_API_NAME, SignerObjConstants.API_NAME);
            productObj.set(IObjectData.PACKAGE, "CRM");
            productObj.set(IObjectData.VERSION, detailDescribe.getVersion());

            detailObjects.add(productObj);
        });
        detailObjectMap.put(SignerObjConstants.API_NAME, detailObjects);

        //4 创建映射Map
        Map<String, IObjectDescribe> objectDescribesMap = new HashMap<>();
        objectDescribesMap.put(SignRecordObjConstants.API_NAME, masterDescribe);
        objectDescribesMap.put(SignerObjConstants.API_NAME, detailDescribe);

        return SaveMasterAndDetailData.Arg.builder().masterObjectData(masterObj).detailObjectData(detailObjectMap).objectDescribes(objectDescribesMap).build();
    }

    /**
     * 获取配额类型
     * 有一个是企业的，就是扣企业配额
     */
    public String getQuotaType(User user, List<SignerDO> signerDOS) {
        //有租户签署的，扣企业配额
        List<String> accountBestSignAccounts = Lists.newArrayList();
        for (SignerDO signerDO : signerDOS) {
            if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.TENANT.getType())) {
                return QuotaTypeEnum.ENTERPRISE.getType();
            }
            else if (Objects.equals(signerDO.getSignerType(), SignerTypeEnum.CRM_ACCOUNT.getType())) {
                accountBestSignAccounts.add(signerDO.getBestSignAccount());
            }
        }

        //客户里面有企业客户的，扣企业配额
        List<IObjectData> objectDatas = accountSignCertifyObjManager.queryByBestSignAccounts(user, accountBestSignAccounts);
        for (IObjectData objectData : objectDatas) {
            String recordType = (String) objectData.get(SystemConstants.Field.RecordType.apiName);
            if (Objects.equals(recordType, AccountSignCertifyObjConstants.RecordType.EnterpriseRecordType.apiName)) {
                return QuotaTypeEnum.ENTERPRISE.getType();
            }
        }

        return QuotaTypeEnum.INDIVIDUAL.getType();
    }

    /**
     *  获取合同附件
     */
    public ArrayList getContractFileAttachment(User user, SignRecordType.GetContractFileAttachment.Arg arg) {
        String origin = getOrigin(user.getTenantId(), arg.getObjApiName());
        IObjectData objectData = query(user, arg.getAppType(), origin, arg.getObjDataId());
        if (objectData != null) {
            return (ArrayList) objectData.get(SignRecordObjConstants.Field.ContractFileAttachment.apiName);
        }

        // 是否有签署请求记录
        SignRequestDO signRequestDO = signRequestManager.getSignRequestDO(user.getTenantId(), arg.getAppType(), arg.getObjDataId());
        if (signRequestDO == null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.NO_SIGN_REQUEST_RECORD_IN_DB);
        }

        //合同还没签署完
        throw new ElecSignBusinessException(ElecSignErrorCode.CONTRACT_HAS_NOT_SIGNED_AND_FINISH);
    }

    // TODO: 2018/5/7 chenzs 加字段要调整
    public String getOrigin(String tenantId, String objApiName) {
        if (Objects.equals(objApiName, Utils.SALES_ORDER_API_NAME)) {
            return OriginEnum.SALES_ORDER.getType();
        }

        //使用自定义apiName的对账单（比如：金新农的对账单）
        String customAccountStatementObjApiName = ConfigCenter.getCustomAccountStatementObjApiName(tenantId);
        log.info("getOrigin tenantId[{}], objApiName[{}], customAccountStatementObjApiName[{}]", tenantId, objApiName, customAccountStatementObjApiName);
        if (Objects.equals(objApiName, customAccountStatementObjApiName)) {
            return OriginEnum.ACCOUNT_STATEMENT.getType();
        }

        if (Objects.equals(objApiName, DeliveryNoteObjConstants.API_NAME)) {
            return OriginEnum.DELIVERY_NOTE.getType();
        }
        throw new ElecSignBusinessException(ElecSignErrorCode.NO_SUPPORT_OBJ);
    }

    /**
     * 数据是否已签过
     */
    public void checkHasSigned(User user, String appType, String objApiName, String objDataId) {
        String origin = getOrigin(user.getTenantId(), objApiName);
        IObjectData objectData = query(user, appType, origin, objDataId);
        if (objectData != null) {
            throw new ElecSignBusinessException(ElecSignErrorCode.DATA_HAS_SIGNED_CONTRACT);
        }
    }

    public IObjectData query(User user, String appType, String origin, String objDataId) {
        List<IFilter> filters = Lists.newArrayList();
        SearchUtil.fillFilterEq(filters, SignRecordObjConstants.Field.AppType.apiName, appType);
        SearchUtil.fillFilterEq(filters, SignRecordObjConstants.Field.Origin.apiName, origin);

        if (Objects.equals(origin, OriginEnum.SALES_ORDER.getType())) {
            SearchUtil.fillFilterEq(filters, SignRecordObjConstants.Field.SalesOrderId.apiName, objDataId);
        } else if (Objects.equals(origin, OriginEnum.ACCOUNT_STATEMENT.getType())) {
            SearchUtil.fillFilterEq(filters, SignRecordObjConstants.Field.AccountStatementId.apiName, objDataId);
        } else if (Objects.equals(origin, OriginEnum.DELIVERY_NOTE.getType())) {
            SearchUtil.fillFilterEq(filters, SignRecordObjConstants.Field.DeliveryNoteId.apiName, objDataId);
        }
        // TODO: 2018/5/7 chenzs 加字段要调整

        List<IObjectData> objectDatas = searchQuery(user, SignRecordObjConstants.API_NAME, filters, Lists.newArrayList(), 0, MAX_LIMIT_FOR_QUERY_ALL).getData();
        if (CollectionUtils.isEmpty(objectDatas)) {
            return null;
        }
        return objectDatas.get(0);
    }
}