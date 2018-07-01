package com.facishare.crm.customeraccount.predefine.action;

import com.facishare.crm.customeraccount.util.RequestUtil;
import com.facishare.paas.appframework.core.exception.ValidateException;
import com.facishare.paas.appframework.core.model.ObjectDataDocument;
import com.facishare.paas.appframework.core.predef.action.StandardInvalidAction;
import com.facishare.paas.metadata.api.IObjectData;

/**
 * Created by xujf on 2017/10/21.
 * 客户账户不能手动删除，所以处理办法为：
 * 1.不给前端返回Action描述，屏蔽入口。
 * 2.前端根据描述符直接屏蔽按钮。
 * 3.作废客户的时候 掉customerAccount.service -> CustomerAccountInvalidAction
 */
public class CustomerAccountInvalidAction extends StandardInvalidAction {

    @Override
    protected void before(Arg arg) {
        super.before(arg);
        if (!RequestUtil.isFromInner(actionContext)) {
            throw new ValidateException("不可作废，随主对象作废");
        }
    }

    @Override
    protected StandardInvalidAction.Result after(StandardInvalidAction.Arg arg, StandardInvalidAction.Result result) {
        StandardInvalidAction.Result result1 = super.after(arg, result);
        IObjectData objectData = result1.getObjectData().toObjectData();
        String lockStatus = objectData.get("lock_status", String.class);
        IObjectData resultData = objectData;
        if ("0".equals(lockStatus)) {
            objectData.set("lock_status", "1");
            resultData = serviceFacade.updateObjectData(actionContext.getUser(), objectData);
        }
        result.setObjectData(ObjectDataDocument.of(resultData));
        return result;
    }

    @Override
    protected void doFunPrivilegeCheck() {

    }

    @Override
    protected void doDataPrivilegeCheck(Arg arg) {
        if (!RequestUtil.isFromInner(actionContext)) {
            super.doDataPrivilegeCheck(arg);
        }
    }
}
