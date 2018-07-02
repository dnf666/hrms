package com.facishare.crm.electronicsign.predefine.manager.obj;

import com.facishare.crm.constants.DeliveryNoteObjConstants;
import com.facishare.crm.constants.SystemConstants;
import com.facishare.crm.describebuilder.*;
import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.InternalSignCertifyObjConstants;
import com.facishare.crm.electronicsign.constants.SignRecordObjConstants;
import com.facishare.crm.electronicsign.constants.SignerObjConstants;
import com.facishare.crm.electronicsign.enums.OriginEnum;
import com.facishare.crm.electronicsign.enums.status.CertifyStatusEnum;
import com.facishare.crm.electronicsign.enums.status.UseStatusEnum;
import com.facishare.crm.electronicsign.enums.type.AppTypeEnum;
import com.facishare.crm.electronicsign.enums.type.QuotaTypeEnum;
import com.facishare.crm.electronicsign.util.ConfigCenter;
import com.facishare.crm.openapi.Utils;
import com.facishare.crm.util.ObjectUtil;
import com.facishare.paas.metadata.api.IRecordTypeOption;
import com.facishare.paas.metadata.api.ISelectOption;
import com.facishare.paas.metadata.api.describe.IFieldDescribe;
import com.facishare.paas.metadata.api.describe.IObjectDescribe;
import com.facishare.paas.metadata.impl.describe.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对象定义
 */
@Service
@Slf4j
public class ElecSignObjectDescribeDraftManager {
    /**
     * 生成InternalSignCertifyObj的draft
     */
    public IObjectDescribe generateInternalSignCertifyDescribeDraft(String tenantId, String userId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe name = AutoNumberFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.Name.apiName).label(InternalSignCertifyObjConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("{yyyy}{mm}{dd}_").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(name);

        PhoneNumberFieldDescribe regMobile = PhoneNumberFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.RegMobile.apiName).label(InternalSignCertifyObjConstants.Field.RegMobile.label).required(true).helpText("使用手机号注册实名帐号").build();
        fieldDescribeList.add(regMobile);


        TextFieldDescribe enterpriseName = TextFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.EnterpriseName.apiName).label(InternalSignCertifyObjConstants.Field.EnterpriseName.label).required(true).maxLength(100).helpText("只支持中国大陆工商局或市场监督管理局登记的企业。请填写工商营业执照上的企业全称，该名称将用作企业认证审核").build();
        fieldDescribeList.add(enterpriseName);

        TextFieldDescribe unifiedSocialCreditIdentifier = TextFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName).label(InternalSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.label).required(true).maxLength(100).helpText("输入与企业名称对应的代码").build();
        fieldDescribeList.add(unifiedSocialCreditIdentifier);

        TextFieldDescribe legalPersonName = TextFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.LegalPersonName.apiName).label(InternalSignCertifyObjConstants.Field.LegalPersonName.label).required(true).maxLength(100).helpText("用于企业认证事务管理和沟通联系").build();
        fieldDescribeList.add(legalPersonName);

        PhoneNumberFieldDescribe legalPersonMobile = PhoneNumberFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.LegalPersonMobile.apiName).label(InternalSignCertifyObjConstants.Field.LegalPersonMobile.label).required(true).helpText("用于企业认证事务管理和沟通联系").build();
        fieldDescribeList.add(legalPersonMobile);

        TextFieldDescribe legalPersonIdentity = TextFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.LegalPersonIdentity.apiName).label(InternalSignCertifyObjConstants.Field.LegalPersonIdentity.label).required(true).maxLength(100).helpText("用于企业认证事务管理，请确保上方人员与身份证号一致").build();
        fieldDescribeList.add(legalPersonIdentity);

        TextFieldDescribe bestSignAccount = TextFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.BestSignAccount.apiName).label(InternalSignCertifyObjConstants.Field.BestSignAccount.label).maxLength(100).build();
        fieldDescribeList.add(bestSignAccount);

        List<ISelectOption> certifyStatusSelectOptions = Arrays.stream(CertifyStatusEnum.values()).map(statusEnum -> SelectOptionBuilder.builder().value(statusEnum.getStatus()).label(statusEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe certifyStatus = SelectOneFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.CertifyStatus.apiName).label(InternalSignCertifyObjConstants.Field.CertifyStatus.label).selectOptions(certifyStatusSelectOptions).defaultValud(CertifyStatusEnum.NO_RECORD.getStatus()).required(false).build();
        fieldDescribeList.add(certifyStatus);

        LongTextFieldDescribe certifyErrMsg = LongTextFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.CertifyErrMsg.apiName).label(InternalSignCertifyObjConstants.Field.CertifyErrMsg.label).maxLength(2000).build();
        fieldDescribeList.add(certifyErrMsg);

        List<ISelectOption> useStatusSelectOptions = Arrays.stream(UseStatusEnum.values()).map(statusEnum -> SelectOptionBuilder.builder().value(statusEnum.getStatus()).label(statusEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe useStatus = SelectOneFieldDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.Field.UseStatus.apiName).label(InternalSignCertifyObjConstants.Field.UseStatus.label).selectOptions(useStatusSelectOptions).defaultValud(UseStatusEnum.UN_USE.getStatus()).required(false).build();
        fieldDescribeList.add(useStatus);

        //业务类型(不给禁用）
        List<IRecordTypeOption> recordTypeOptions = Lists.newArrayList();
        RecordTypeOption defaultRecordTypeOption = getRecordTypeOption(InternalSignCertifyObjConstants.RecordType.DefaultRecordType.apiName, InternalSignCertifyObjConstants.RecordType.DefaultRecordType.label, true);
        recordTypeOptions.add(defaultRecordTypeOption);
        RecordTypeFieldDescribe recordTypeFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeFieldDescribe);

        Map<String, Object> configMap = ObjectUtil.buildConfigMap(true, true, true, false, false);
        return ObjectDescribeBuilder.builder().apiName(InternalSignCertifyObjConstants.API_NAME).displayName(InternalSignCertifyObjConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(InternalSignCertifyObjConstants.STORE_TABLE_NAME).iconIndex(InternalSignCertifyObjConstants.ICON_INDEX).build();
    }

    /**
     * 生成AccountSignCertifyObj的draft
     */
    public IObjectDescribe generateAccountSignCertifyDescribeDraft(String tenantId, String userId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe name = AutoNumberFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.Name.apiName).label(AccountSignCertifyObjConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("{yyyy}{mm}{dd}_").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(name);

        List<ISelectOption> certifyStatusSelectOptions = Arrays.stream(CertifyStatusEnum.values()).map(statusEnum -> SelectOptionBuilder.builder().value(statusEnum.getStatus()).label(statusEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe certifyStatus = SelectOneFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.CertifyStatus.apiName).label(AccountSignCertifyObjConstants.Field.CertifyStatus.label).selectOptions(certifyStatusSelectOptions).defaultValud(CertifyStatusEnum.NO_RECORD.getStatus()).required(false).build();
        fieldDescribeList.add(certifyStatus);

        LongTextFieldDescribe certifyErrMsg = LongTextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.CertifyErrMsg.apiName).label(AccountSignCertifyObjConstants.Field.CertifyErrMsg.label).maxLength(2000).build();
        fieldDescribeList.add(certifyErrMsg);

        TextFieldDescribe bestSignAccount = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.BestSignAccount.apiName).label(AccountSignCertifyObjConstants.Field.BestSignAccount.label).maxLength(100).build();
        fieldDescribeList.add(bestSignAccount);

        PhoneNumberFieldDescribe regMobile = PhoneNumberFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.RegMobile.apiName).label(AccountSignCertifyObjConstants.Field.RegMobile.label).required(true).build();
        fieldDescribeList.add(regMobile);
        
        ObjectReferenceFieldDescribe accountId = ObjectReferenceFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.AccountId.apiName).label(AccountSignCertifyObjConstants.Field.AccountId.label).targetApiName(Utils.ACCOUNT_API_NAME).targetRelatedListLabel(AccountSignCertifyObjConstants.Field.AccountId.targetRelatedListLabel).targetRelatedListName(AccountSignCertifyObjConstants.Field.AccountId.targetRelatedListName).unique(false).required(true).build();
        fieldDescribeList.add(accountId);

        TextFieldDescribe enterpriseName = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.EnterpriseName.apiName).label(AccountSignCertifyObjConstants.Field.EnterpriseName.label).maxLength(100).defaultIsExpression(true).defaultValue("$account_id__r.name$").build();
        fieldDescribeList.add(enterpriseName);

        TextFieldDescribe unifiedSocialCreditIdentifier = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.apiName).label(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier.label).maxLength(100).build();
        fieldDescribeList.add(unifiedSocialCreditIdentifier);

        TextFieldDescribe legalPersonName = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.LegalPersonName.apiName).label(AccountSignCertifyObjConstants.Field.LegalPersonName.label).maxLength(100).build();
        fieldDescribeList.add(legalPersonName);

        TextFieldDescribe legalPersonIdentity = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.LegalPersonIdentity.apiName).label(AccountSignCertifyObjConstants.Field.LegalPersonIdentity.label).maxLength(100).build();
        fieldDescribeList.add(legalPersonIdentity);

        PhoneNumberFieldDescribe legalPersonMobile = PhoneNumberFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.LegalPersonMobile.apiName).label(AccountSignCertifyObjConstants.Field.LegalPersonMobile.label).build();
        fieldDescribeList.add(legalPersonMobile);



        TextFieldDescribe userName = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.UserName.apiName).label(AccountSignCertifyObjConstants.Field.UserName.label).maxLength(100).defaultIsExpression(true).defaultValue("$account_id__r.name$").build();
        fieldDescribeList.add(userName);

        TextFieldDescribe userIdentity = TextFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.UserIdentity.apiName).label(AccountSignCertifyObjConstants.Field.UserIdentity.label).maxLength(100).build();
        fieldDescribeList.add(userIdentity);

        PhoneNumberFieldDescribe userMobile = PhoneNumberFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.UserMobile.apiName).label(AccountSignCertifyObjConstants.Field.UserMobile.label).build();
        fieldDescribeList.add(userMobile);

        List<ISelectOption> useStatusSelectOptions = Arrays.stream(UseStatusEnum.values()).map(statusEnum -> SelectOptionBuilder.builder().value(statusEnum.getStatus()).label(statusEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe useStatus = SelectOneFieldDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.Field.UseStatus.apiName).label(AccountSignCertifyObjConstants.Field.UseStatus.label).selectOptions(useStatusSelectOptions).defaultValud(UseStatusEnum.UN_USE.getStatus()).required(false).build();
        fieldDescribeList.add(useStatus);

        //两个业务类型
        List<IRecordTypeOption> recordTypeOptions = Lists.newArrayList();
        RecordTypeOption individualRecordTypeOption = getRecordTypeOption(AccountSignCertifyObjConstants.RecordType.IndividualRecordType.apiName, AccountSignCertifyObjConstants.RecordType.IndividualRecordType.label, true);
        RecordTypeOption enterpriseRecordTypeOption = getRecordTypeOption(AccountSignCertifyObjConstants.RecordType.EnterpriseRecordType.apiName, AccountSignCertifyObjConstants.RecordType.EnterpriseRecordType.label, true);
        recordTypeOptions.add(individualRecordTypeOption);
        recordTypeOptions.add(enterpriseRecordTypeOption);
        RecordTypeFieldDescribe recordTypeFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeFieldDescribe);

        Map<String, Object> configMap = ObjectUtil.buildConfigMap(true, true, true, false, false);
        return ObjectDescribeBuilder.builder().apiName(AccountSignCertifyObjConstants.API_NAME).displayName(AccountSignCertifyObjConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(AccountSignCertifyObjConstants.STORE_TABLE_NAME).iconIndex(AccountSignCertifyObjConstants.ICON_INDEX).build();
    }

    //http://wiki.firstshare.cn/pages/viewpage.action?pageId=59344930
    private RecordTypeOption getRecordTypeOption(String apiName, String label, boolean isActive) {
        RecordTypeOption recordTypeOption = new RecordTypeOption();
        recordTypeOption.setApiName(apiName);
        recordTypeOption.setIsActive(isActive);
        recordTypeOption.setLabel(label);

        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("enable", 0); //不允许禁用该业务类型
        recordTypeOption.set("config", configMap);
        return recordTypeOption;
    }

    /**
     * 生成SignRecordObj的draft
     */
    public IObjectDescribe generateSignRecordDescribeDraft(String tenantId, String userId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe name = AutoNumberFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.Name.apiName).label(SignRecordObjConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("{yyyy}{mm}{dd}_").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(name);

        List<ISelectOption> quotaTypeSelectOptions = Arrays.stream(QuotaTypeEnum.values()).map(codeEnum -> SelectOptionBuilder.builder().value(codeEnum.getType()).label(codeEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe quotaType = SelectOneFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.QuotaType.apiName).label(SignRecordObjConstants.Field.QuotaType.label).selectOptions(quotaTypeSelectOptions).required(false).build();
        fieldDescribeList.add(quotaType);

        List<ISelectOption> appTypeSelectOptions = Arrays.stream(AppTypeEnum.values()).map(codeEnum -> SelectOptionBuilder.builder().value(codeEnum.getType()).label(codeEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe appType = SelectOneFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.AppType.apiName).label(SignRecordObjConstants.Field.AppType.label).selectOptions(appTypeSelectOptions).required(false).build();
        fieldDescribeList.add(appType);

        List<ISelectOption> originSelectOptions = Arrays.stream(OriginEnum.values()).map(codeEnum -> SelectOptionBuilder.builder().value(codeEnum.getType()).label(codeEnum.getLabel()).build()).collect(Collectors.toList());
        SelectOneFieldDescribe origin = SelectOneFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.Origin.apiName).label(SignRecordObjConstants.Field.Origin.label).selectOptions(originSelectOptions).required(false).build();
        fieldDescribeList.add(origin);

        ObjectReferenceFieldDescribe salesOrderId = ObjectReferenceFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.SalesOrderId.apiName).label(SignRecordObjConstants.Field.SalesOrderId.label).targetApiName(Utils.SALES_ORDER_API_NAME).targetRelatedListLabel(SignRecordObjConstants.Field.SalesOrderId.targetRelatedListLabel).targetRelatedListName(SignRecordObjConstants.Field.SalesOrderId.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(salesOrderId);

        /**
         * 用了自定义的对账单的企业
         */
        boolean isUseCustomAccountStatementObjApiName = ConfigCenter.isUseCustomAccountStatementObjApiName(tenantId);
        log.info("generateSignRecordDescribeDraft, tenantId[{}], userId[{}], isUseCustomAccountStatementObjApiName[{}]", tenantId, userId, isUseCustomAccountStatementObjApiName);
        if (isUseCustomAccountStatementObjApiName) {
            String customAccountStatementObjApiName = ConfigCenter.getCustomAccountStatementObjApiName(tenantId);
            log.info("generateSignRecordDescribeDraft, tenantId[{}], userId[{}], customAccountStatementObjApiName[{}]", tenantId, userId, customAccountStatementObjApiName);
            ObjectReferenceFieldDescribe accountStatementId = ObjectReferenceFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.AccountStatementId.apiName).label(SignRecordObjConstants.Field.AccountStatementId.label).targetApiName(customAccountStatementObjApiName).targetRelatedListLabel(SignRecordObjConstants.Field.AccountStatementId.targetRelatedListLabel).targetRelatedListName(SignRecordObjConstants.Field.AccountStatementId.targetRelatedListName).unique(false).required(false).build();
            fieldDescribeList.add(accountStatementId);
        }

        // TODO: 2018/5/22 chenzs 加字段要调整
        ObjectReferenceFieldDescribe deliveryNoteId = ObjectReferenceFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.DeliveryNoteId.apiName).label(SignRecordObjConstants.Field.DeliveryNoteId.label).targetApiName(DeliveryNoteObjConstants.API_NAME).targetRelatedListLabel(SignRecordObjConstants.Field.DeliveryNoteId.targetRelatedListLabel).targetRelatedListName(SignRecordObjConstants.Field.DeliveryNoteId.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(deliveryNoteId);

        TextFieldDescribe contractId = TextFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.ContractId.apiName).label(SignRecordObjConstants.Field.ContractId.label).maxLength(200).build();
        fieldDescribeList.add(contractId);

        FileAttachmentFieldDescribe fileAttachmentFieldDescribe = FileAttachmentFieldDescribeBuilder.builder().apiName(SignRecordObjConstants.Field.ContractFileAttachment.apiName).label(SignRecordObjConstants.Field.ContractFileAttachment.label).fileAmountLimit(10).fileSizeLimit(104857600L).build();
        fieldDescribeList.add(fileAttachmentFieldDescribe);

        //业务类型(不给禁用）
        List<IRecordTypeOption> recordTypeOptions = Lists.newArrayList();
        RecordTypeOption defaultRecordTypeOption = getRecordTypeOption(SignRecordObjConstants.RecordType.DefaultRecordType.apiName, SignRecordObjConstants.RecordType.DefaultRecordType.label, true);
        recordTypeOptions.add(defaultRecordTypeOption);
        RecordTypeFieldDescribe recordTypeFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeFieldDescribe);

        Map<String, Object> configMap = ObjectUtil.buildConfigMap(true, true, true, false, false);
        return ObjectDescribeBuilder.builder().apiName(SignRecordObjConstants.API_NAME).displayName(SignRecordObjConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(SignRecordObjConstants.STORE_TABLE_NAME).iconIndex(SignRecordObjConstants.ICON_INDEX).build();
    }

    /**
     * 生成SignerObj的draft
     */
    public IObjectDescribe generateSignerDescribeDraft(String tenantId, String userId) {
        List<IFieldDescribe> fieldDescribeList = Lists.newArrayList();

        AutoNumberFieldDescribe name = AutoNumberFieldDescribeBuilder.builder().apiName(SignerObjConstants.Field.Name.apiName).label(SignerObjConstants.Field.Name.label).required(true).serialNumber(4).startNumber(1).prefix("{yyyy}{mm}{dd}_").postfix("").required(true).unique(true).index(true).build();
        fieldDescribeList.add(name);

        MasterDetailFieldDescribe signRecordId = MasterDetailFieldDescribeBuilder.builder().isCreateWhenMasterCreate(true).isRequiredWhenMasterCreate(false)
                .apiName(SignerObjConstants.Field.SignRecordId.apiName).label(SignerObjConstants.Field.SignRecordId.label).index(true).required(true).targetApiName(SignRecordObjConstants.API_NAME).unique(false).targetRelatedListName(SignerObjConstants.Field.SignRecordId.targetRelatedListName)
                .targetRelatedListLabel(SignerObjConstants.Field.SignRecordId.targetRelatedListLabel).build();
        fieldDescribeList.add(signRecordId);

        ObjectReferenceFieldDescribe accountSignCertifyId = ObjectReferenceFieldDescribeBuilder.builder().apiName(SignerObjConstants.Field.AccountSignCertifyId.apiName).label(SignerObjConstants.Field.AccountSignCertifyId.label).targetApiName(AccountSignCertifyObjConstants.API_NAME).targetRelatedListLabel(SignerObjConstants.Field.AccountSignCertifyId.targetRelatedListLabel).targetRelatedListName(SignerObjConstants.Field.AccountSignCertifyId.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(accountSignCertifyId);

        ObjectReferenceFieldDescribe internalSignCertifyId = ObjectReferenceFieldDescribeBuilder.builder().apiName(SignerObjConstants.Field.InternalSignCertifyId.apiName).label(SignerObjConstants.Field.InternalSignCertifyId.label).targetApiName(InternalSignCertifyObjConstants.API_NAME).targetRelatedListLabel(SignerObjConstants.Field.InternalSignCertifyId.targetRelatedListLabel).targetRelatedListName(SignerObjConstants.Field.InternalSignCertifyId.targetRelatedListName).unique(false).required(false).build();
        fieldDescribeList.add(internalSignCertifyId);


        //业务类型(不给禁用）
        List<IRecordTypeOption> recordTypeOptions = Lists.newArrayList();
        RecordTypeOption defaultRecordTypeOption = getRecordTypeOption(SignerObjConstants.RecordType.DefaultRecordType.apiName, SignerObjConstants.RecordType.DefaultRecordType.label, true);
        recordTypeOptions.add(defaultRecordTypeOption);
        RecordTypeFieldDescribe recordTypeFieldDescribe = RecordTypeFieldDescribeBuilder.builder().apiName(SystemConstants.Field.RecordType.apiName).label(SystemConstants.Field.RecordType.label).recordTypeOptions(recordTypeOptions).build();
        fieldDescribeList.add(recordTypeFieldDescribe);

        Map<String, Object> configMap = ObjectUtil.buildConfigMap(true, true, true, false, false);
        return ObjectDescribeBuilder.builder().apiName(SignerObjConstants.API_NAME).displayName(SignerObjConstants.DISPLAY_NAME).config(configMap).tenantId(tenantId).createBy(userId).fieldDescribes(fieldDescribeList).storeTableName(SignerObjConstants.STORE_TABLE_NAME).iconIndex(SignerObjConstants.ICON_INDEX).build();
    }
}