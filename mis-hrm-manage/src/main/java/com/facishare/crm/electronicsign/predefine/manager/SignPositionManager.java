package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.predefine.model.*;
import com.facishare.crm.electronicsign.predefine.service.dto.SignRequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SignPositionManager {
    /**
     * 获取签署位置
     */
    public SignPositionDO getSignPositionDO(List<SignerDO> signers, SignRequestType.GetSignUrl.SignerArg signer) {
        for (SignerDO signerDO : signers) {
            if (Objects.equals(signerDO.getSignerType(), signer.getSignerType())) {
                if (Objects.equals(signerDO.getAccountId(), signer.getAccountId())) {
                    return signerDO.getSignPosition();
                }
                if (Objects.equals(signerDO.getUpDepartmentId(), signer.getUpDepartmentId())) {
                    return signerDO.getSignPosition();
                }
            }
        }
        return null;
    }
}