package com.facishare.crm.customeraccount.predefine.manager.common;

import com.facishare.crm.customeraccount.constants.SystemConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LifeStatusDeleteExecutor {
    private LifeStatusToDelete lifeStatusToDelete;

    public LifeStatusDeleteExecutor(LifeStatusToDelete lifeStatusChange) {
        this.lifeStatusToDelete = lifeStatusChange;
    }

    public void doChange(String lifeStatus) {
        log.info("{}-> deleted", lifeStatus);
        if (SystemConstants.LifeStatus.Ineffective.value.equals(lifeStatus)) {
            lifeStatusToDelete.ineffectiveToDeleted();
        } else if (SystemConstants.LifeStatus.UnderReview.value.equals(lifeStatus)) {
            lifeStatusToDelete.underReviewToDeleted();
        } else if (SystemConstants.LifeStatus.InChange.value.equals(lifeStatus)) {
            lifeStatusToDelete.inchangeToDeleted();
        } else if (SystemConstants.LifeStatus.Invalid.value.equals(lifeStatus)) {
            lifeStatusToDelete.invalidToDeleted();
        } else if (SystemConstants.LifeStatus.Normal.value.equals(lifeStatus)) {
            lifeStatusToDelete.normalToDeleted();
        } else {
            log.warn("NotFound LifeStatus={}", lifeStatus);
        }
    }

    /**
     * 编辑回款 场景下删除回款明细，会在以下状态下进行编辑：
     * 未生效、正常
     */
    public interface LifeStatusToDelete {

        public void ineffectiveToDeleted();

        public void underReviewToDeleted();

        public void normalToDeleted();

        public void inchangeToDeleted();

        public void invalidToDeleted();
    }
}
