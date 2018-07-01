package com.facishare.crm.electronicsign.predefine.manager;

import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GrayReleaseManager {
    private FsGrayReleaseBiz grayReleaseBiz  = FsGrayRelease.getInstance("electronic-sign");

    public boolean isInitSwitchGrayed(String tenantId) {
        return grayReleaseBiz.isAllow("initSignSwitch", tenantId);
    }
}
