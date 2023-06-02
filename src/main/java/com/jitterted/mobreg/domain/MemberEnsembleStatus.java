package com.jitterted.mobreg.domain;

public enum MemberEnsembleStatus {
    UNKNOWN,
    FULL,
    DECLINED, DECLINED_FULL,
    PENDING_COMPLETED, COMPLETED,
    ACCEPTED,
    IN_GRACE_PERIOD,
    CANCELED, HIDDEN
}
