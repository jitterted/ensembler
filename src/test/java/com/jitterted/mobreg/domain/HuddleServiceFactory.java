package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.adapter.in.web.member.DummyNotifier;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;

public class HuddleServiceFactory {

    public static HuddleService createHuddleServiceForTest(HuddleRepository huddleRepository) {
        return new HuddleService(huddleRepository, new InMemoryMemberRepository(), new DummyNotifier());
    }

    public static HuddleService createHuddleServiceForTest(HuddleRepository huddleRepository, MemberRepository memberRepository) {
        return new HuddleService(huddleRepository, memberRepository, new DummyNotifier());
    }
}
