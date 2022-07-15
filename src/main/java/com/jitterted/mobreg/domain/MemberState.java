package com.jitterted.mobreg.domain;

import java.time.ZoneId;

public record MemberState(String firstName,
                          String githubUsername,
                          java.util.Set<String> roles,
                          String email,
                          ZoneId timeZone,
                          MemberId memberId) {
}
