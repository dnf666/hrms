package com.facishare.crm.electronicsign.predefine.service;

import com.facishare.crm.electronicsign.predefine.service.dto.ElecSignType;
import com.facishare.paas.appframework.core.annotation.ServiceMethod;
import com.facishare.paas.appframework.core.annotation.ServiceModule;
import com.facishare.paas.appframework.core.model.ServiceContext;

@ServiceModule("elec_sign_sign_setting")
public interface SignSettingService {
    /**
     * 开启应用级"电子签章"开关
     */
    @ServiceMethod("enable_switch_for_app")
    ElecSignType.EnableSwitchForApp.Result enableSwitchForApp(ServiceContext serviceContext, ElecSignType.EnableSwitchForApp.Arg arg);

    @ServiceMethod("query_app_switch_and_sign_setting")
    ElecSignType.QueryAppSwitchAndSignSetting.Result queryAppSwitchAndSignSetting(ServiceContext serviceContext, ElecSignType.QueryAppSwitchAndSignSetting.Arg arg);

    @ServiceMethod("save_or_update_sign_setting")
    ElecSignType.SaveOrUpdateSignSetting.Result saveOrUpdateSignSetting(ServiceContext serviceContext, ElecSignType.SaveOrUpdateSignSetting.Arg arg);

    @ServiceMethod("delete_sign_setting")
    ElecSignType.DeleteSignSetting.Result deleteSignSetting(ServiceContext serviceContext, ElecSignType.DeleteSignSetting.Arg arg);
}
