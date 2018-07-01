package com.facishare.crm.deliverynote.predefine.manager.common;

public interface LifeStatusChange {
    void ineffectiveToUnderReview();

    void ineffectiveToNormal();

    void underReviewToNormal();

    void underReviewToIneffective();

    void normalToInChange();

    void normalToInvalid();

    void inChangeToInvalid();

    void inChangeToNormal();

    void invalidToNormal();
}
