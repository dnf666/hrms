package com.facishare.crm.sfa.utilities.util;

import com.google.common.collect.Sets;

import com.github.autoconf.ConfigFactory;

import org.springframework.util.CollectionUtils;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cqx
 * @date 2018/5/21 15:12
 */
@Slf4j
public class PriceBookSpecImpCheckGrayTenantUtil {
    private static Set<String> grayTenants = Sets.newHashSet();

    static {
        ConfigFactory.getInstance().getConfig("fs-crm-sys-variable", (config) -> {
            try {
                String priceBookSpecImpGrayTenants = config.get("priceBookSpecImpGrayTenants").trim();
                CollectionUtils.mergeArrayIntoCollection(priceBookSpecImpGrayTenants.split(","), grayTenants);
            } catch (Exception var11) {
                log.error("fs-crm-sys-variable config has error");
            }
        });
    }

    public static boolean checkGrayTenant(String tenantId) {
        return grayTenants.contains(tenantId);
    }
}
