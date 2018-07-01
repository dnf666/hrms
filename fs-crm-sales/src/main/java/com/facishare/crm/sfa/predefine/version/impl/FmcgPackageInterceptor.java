package com.facishare.crm.sfa.predefine.version.impl;

import com.google.common.collect.Sets;

import com.facishare.paas.appframework.license.util.LicenseConstants;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FmcgPackageInterceptor {
    //盘点对象
    String INVENTORYOBJ = "InventoryObj";
    //快销包中支持的对象apiname
    Set<String> VERSION_FMCG_PACKAGE_API = Sets.newHashSet(INVENTORYOBJ);

    public Set<String> filterSupportObj(List<String> licenseInfo, Set<String> apiNames) {
        //是否包含了快销包
        Boolean isContainsFMGG = licenseInfo.contains(LicenseConstants.Packages.FMCG_PACKAGE);
        if (!isContainsFMGG) {
            //如果不包含则移除
            apiNames.removeIf(apiName -> VERSION_FMCG_PACKAGE_API.contains(apiName));
        }
        return apiNames;
    }
}
