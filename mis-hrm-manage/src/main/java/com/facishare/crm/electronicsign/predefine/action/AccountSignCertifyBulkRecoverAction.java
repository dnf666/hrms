package com.facishare.crm.electronicsign.predefine.action;

import com.facishare.crm.electronicsign.constants.AccountSignCertifyObjConstants;
import com.facishare.crm.electronicsign.predefine.manager.obj.AccountSignCertifyObjManager;
import com.facishare.crm.util.SearchUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.User;
import com.facishare.paas.appframework.core.predef.action.StandardBulkRecoverAction;
import com.facishare.paas.metadata.api.IObjectData;
import com.facishare.paas.metadata.api.search.IFilter;
import com.facishare.paas.metadata.util.SpringUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class AccountSignCertifyBulkRecoverAction extends StandardBulkRecoverAction {
    private AccountSignCertifyObjManager accountSignCertifyObjManager = SpringUtil.getContext().getBean(AccountSignCertifyObjManager.class);

    @Override
    protected void before(Arg arg) {
        super.before(arg);

        // 验证客户是否已经存在新的认证信息
        checkCustomerCertifyInfo();
    }

    private void checkCustomerCertifyInfo() {
        User superAdmin = new User(actionContext.getTenantId(), User.SUPPER_ADMIN_USER_ID);
        List<IObjectData> recoverDataList = accountSignCertifyObjManager.findObjectDataByIdsIncludeDeleted(superAdmin, arg.getIdList());

        checkRecoverDataList(recoverDataList);
        checkRecoverDataWithValid(recoverDataList);
    }

    private void checkRecoverDataWithValid(List<IObjectData> recoverDataList) {
        checkFieldWithValidData(AccountSignCertifyObjConstants.Field.AccountId, recoverDataList);
        checkFieldWithValidData(AccountSignCertifyObjConstants.Field.RegMobile, recoverDataList);
        checkFieldWithValidData(AccountSignCertifyObjConstants.Field.EnterpriseName, recoverDataList);
        checkFieldWithValidData(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier, recoverDataList);
        checkFieldWithValidData(AccountSignCertifyObjConstants.Field.UserIdentity, recoverDataList);
    }

    /**
     * 校验与有效的数据比较是否有重复数据
     */
    private void checkFieldWithValidData(AccountSignCertifyObjConstants.Field field, List<IObjectData> recoverDataList) {
        List<String> recoverDataFieldValues = recoverDataList.stream()
                .map(objectData -> objectData.get(field.apiName, String.class))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(recoverDataFieldValues)) {
            List<IFilter> filters = Lists.newArrayList();
            SearchUtil.fillFilterIn(filters, field.apiName, recoverDataFieldValues);
            User superAdmin = new User(actionContext.getTenantId(), User.SUPPER_ADMIN_USER_ID);
            List<IObjectData> queryResult = accountSignCertifyObjManager.searchQuery(superAdmin, AccountSignCertifyObjConstants.API_NAME, filters, Lists.newArrayList(), 0, 1).getData();
            if (CollectionUtils.isNotEmpty(queryResult)) {
                String errMsg = String.format("已存在同样的认证信息：[%s]-[%s]", field.label, queryResult.get(0).get(field.apiName, String.class));
                throw new ValidateException(errMsg );
            }
        }
    }

    /**
     * 校验恢复的数据本身是否有重复数据
     */
    private void checkRecoverDataList(List<IObjectData> recoverDataList) {
        checkRecoverDataField(AccountSignCertifyObjConstants.Field.AccountId, recoverDataList);
        checkRecoverDataField(AccountSignCertifyObjConstants.Field.RegMobile, recoverDataList);
        checkRecoverDataField(AccountSignCertifyObjConstants.Field.EnterpriseName, recoverDataList);
        checkRecoverDataField(AccountSignCertifyObjConstants.Field.UnifiedSocialCreditIdentifier, recoverDataList);
        checkRecoverDataField(AccountSignCertifyObjConstants.Field.UserIdentity, recoverDataList);
    }

    private void checkRecoverDataField(AccountSignCertifyObjConstants.Field field, List<IObjectData> recoverDataList) {
        List<String> fieldValues = recoverDataList.stream().map(objectData -> objectData.get(field.apiName, String.class))
                .collect(Collectors.toList());
        fieldValues.stream().filter(Objects::nonNull).forEach(fieldValue -> {
            List<IObjectData> sameFieldValueDataList = recoverDataList.stream().filter(objectData -> Objects.equals(fieldValue, objectData.get(field.apiName, String.class))).collect(Collectors.toList());
            if (sameFieldValueDataList.size() > 1) {
                String errMsg = String.format("被恢复的数据中已存在同样的认证信息：[%s]-[%s]", field.label, fieldValue);
                throw new ValidateException(errMsg );
            }
        });
    }

}
