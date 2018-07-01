package com.facishare.crm.sfa.predefine.version.impl;

import com.facishare.crm.sfa.predefine.version.VersionService;
import com.facishare.paas.appframework.license.LicenseService;
import com.facishare.paas.appframework.license.util.LicenseConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("versionService")
public class VersionServiceImpl implements VersionService {
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private FmcgPackageInterceptor fmcgPackageInterceptor;
    @Autowired
    private BasicVersionServiceImpl basicVersionService;
    @Autowired
    private DefaultVersionServiceImpl defaultVersionService;
    @Autowired
    private ManufactureInterceptor manufactureInterceptor;

    @Override
    public Set<String> filterSupportObj(String tenantId, Set<String> apiNames) {
        List<String> licenseInfo = licenseService.getVersionAndPackages(tenantId);
        getInstance(licenseInfo).filterSupportObj(tenantId, apiNames);
        // TODO: 2018/3/13 将来多个资源扩展包可以以Interceptor的形式提供
        //快销包过滤相关对象
        fmcgPackageInterceptor.filterSupportObj(licenseInfo, apiNames);

        //制造包过滤相关对象
        manufactureInterceptor.filterSupportObj(licenseInfo, apiNames);
        return apiNames;
    }

    private VersionService getInstance(List<String> licenseInfo) {
        //如果是基础版
        if (licenseInfo.contains(LicenseConstants.Versions.VERSION_BASIC)) {
            return basicVersionService;
        } else {
            //其他版本
            return defaultVersionService;
        }
    }

}
