package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.RebateIncomeDetailConstants;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.BaseObjectLockAction;
import com.facishare.paas.appframework.core.predef.action.StandardLockAction;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by xujf on 2017/11/29.
 */
@Slf4j
public class RebateIncomeDetailLockAction extends StandardLockAction {

    /**
     * 明细如果带有回款或者退款id则不能在页面执行手动锁定操作<br>
     *
     * @param arg
     */
    @Override
    protected void before(BaseObjectLockAction.Arg arg) {
        super.before(arg);
        if (!RequestUtil.isFromInner(actionContext)) {
            List<String> dataIds = arg.getDataIds();
            for (String dataId : dataIds) {
                IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), dataId, RebateIncomeDetailConstants.API_NAME);
                String refundId = objectData.get(RebateIncomeDetailConstants.Field.Refund.apiName, String.class);

                if (StringUtils.isNotBlank(refundId)) {
                    log.warn("由退款创建的预存款支出明细不能手动锁定,for dataId:{}", dataId);
                    throw new ValidateException("由退款关联创建的返利记录，暂不支持锁定！");
                }
            }
        }

    }
}

