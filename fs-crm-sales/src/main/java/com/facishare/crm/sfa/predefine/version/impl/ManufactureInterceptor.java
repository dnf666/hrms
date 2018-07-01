package com.facishare.crm.sfa.predefine.version.impl;

import com.google.common.collect.Sets;

import com.facishare.crm.openapi.Utils;
import com.facishare.paas.appframework.license.util.LicenseConstants;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author cqx
 * @date 2018/3/28 15:05
 */
@Service
public class ManufactureInterceptor {
    //制造业包中包含的对象
    Set<String> VERSION_MANUFACTURE_PACKAGE_API = Sets.newHashSet(Utils.PAYMENT_PLAN_API_NAME, "CasesObj", "CrmServiceManager",
            "DeliveryNoteObj", "DeliveryNoteProductObj", "StockObj", "GoodsReceivedNoteObj", "WarehouseObj", "GoodsReceivedNoteProductObj");

    public Set<String> filterSupportObj(List<String> licenseInfo, Set<String> apiNames) {
        //是否包含了制造业包
        Boolean isManufacture = licenseInfo.contains(LicenseConstants.Packages.MANUFACTURE_PACKAGE);
        if (licenseInfo.contains(LicenseConstants.Versions.VERSION_BASIC) && !isManufacture) {
            //如果不包含则移除
            apiNames.removeIf(apiName -> VERSION_MANUFACTURE_PACKAGE_API.contains(apiName));
        }
        return apiNames;
    }
}
