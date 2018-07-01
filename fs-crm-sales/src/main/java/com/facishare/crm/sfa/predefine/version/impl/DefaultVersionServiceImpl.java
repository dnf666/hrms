package com.facishare.crm.sfa.predefine.version.impl;

import com.facishare.crm.sfa.predefine.version.VersionService;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DefaultVersionServiceImpl implements VersionService {
    @Override
    public Set<String> filterSupportObj(String tenantId, Set<String> apiNames) {
        return apiNames;
    }
}
