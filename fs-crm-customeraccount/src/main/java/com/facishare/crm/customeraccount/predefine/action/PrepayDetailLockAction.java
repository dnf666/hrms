package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.constants.PrepayDetailConstants;
import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.predef.action.StandardLockAction;
import com.facishare.paas.metadata.api.IObjectData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by xujf on 2017/11/29.
 */
@Slf4j
public class PrepayDetailLockAction extends StandardLockAction {

    /**
     * 明细如果带有回款或者退款id则不能在页面执行手动锁定操作<br>
     * @param arg
     */
    @Override
    protected void before(Arg arg) {
        super.before(arg);
        if (!RequestUtil.isFromInner(actionContext)) {
            List<String> dataIds = arg.getDataIds();
            for (String dataId : dataIds) {
                IObjectData objectData = this.serviceFacade.findObjectData(actionContext.getUser(), dataId, PrepayDetailConstants.API_NAME);
                String paymentId = objectData.get(PrepayDetailConstants.Field.OrderPayment.apiName, String.class);
                String refundId = objectData.get(PrepayDetailConstants.Field.Refund.apiName, String.class);

                if (StringUtils.isNotBlank(paymentId)) {
                    log.warn("由回款创建的预存款明细不能手动锁定,for dataId:{}", dataId);
                    throw new ValidateException("由回款关联创建的预存款记录，暂不支持锁定!");
                }

                if (StringUtils.isNotBlank(refundId)) {
                    log.warn("由退款创建的预存款明细不能手动锁定,for dataId:{}", dataId);
                    throw new ValidateException("由退款关联创建的预存款记录，暂不支持锁定!");
                }
            }
        }
    }
}
