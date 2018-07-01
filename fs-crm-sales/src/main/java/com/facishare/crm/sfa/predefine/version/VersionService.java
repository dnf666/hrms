package com.facishare.crm.sfa.predefine.version;

import java.util.Set;

public interface VersionService {
    /**
     * 根据企业id和apiNames过滤出当前企业可以支持的对象列表
     * 注意：只是过滤企业支持，不过滤权限
     */
    default Set<String> filterSupportObj(String tenantId, Set<String> apiNames) {
        return null;
    }
}
