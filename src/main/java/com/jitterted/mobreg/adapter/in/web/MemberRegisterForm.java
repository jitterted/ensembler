package com.jitterted.mobreg.adapter.in.web;

public class MemberRegisterForm {
    private Long huddleId;
    private String username;
    private String name;
    private Long memberId;

    public Long getHuddleId() {
        return huddleId;
    }

    public void setHuddleId(Long huddleId) {
        this.huddleId = huddleId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
