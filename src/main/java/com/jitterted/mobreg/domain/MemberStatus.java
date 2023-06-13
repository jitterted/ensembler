package com.jitterted.mobreg.domain;

// see member-status.puml state diagram for details
public enum MemberStatus {
    UNKNOWN,
    // DECLINED does not need to be a separate state, once we remove the declinedMembers from an Ensemble
    @Deprecated DECLINED,
    PARTICIPANT,
    SPECTATOR
}
