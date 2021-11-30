package com.jitterted.mobreg.application;

import com.jitterted.mobreg.adapter.in.web.member.DummyNotifier;
import com.jitterted.mobreg.application.port.HuddleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;

public class HuddleServiceFactory {

    public static HuddleService createHuddleServiceForTest(HuddleRepository huddleRepository) {
        return new HuddleService(huddleRepository, new InMemoryMemberRepository(), new DummyNotifier());
    }

    public static HuddleService createHuddleServiceForTest(HuddleRepository huddleRepository, MemberRepository memberRepository) {
        return new HuddleService(huddleRepository, memberRepository, new DummyNotifier());
    }
}
