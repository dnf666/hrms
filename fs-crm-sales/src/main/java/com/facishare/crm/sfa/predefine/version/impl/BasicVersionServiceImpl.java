package com.facishare.crm.sfa.predefine.version.impl;

import com.facishare.crm.sfa.predefine.version.VersionService;
import com.facishare.paas.appframework.metadata.menu.MenuConstants;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BasicVersionServiceImpl implements VersionService {
    //基础版支持CRM信息、线索、线索池、客户、联系人 市场活动 公海 商机 订单 退货单 回款 回款明细 退款 开票申请 拜访 产品 价目表 价目表明细
    //制造业包 回款计划 工单 服务管理 发货单 库存
    Set supportApiNams = MenuConstants.basicVersionSupportApiNames;

    @Override
    public Set<String> filterSupportObj(String tenantId, Set<String> apiNames) {
        //移除不包含的对象
        apiNames.removeIf(apiName -> !supportApiNams.contains(apiName));
        return apiNames;
    }
}
