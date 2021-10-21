package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.adapter.in.web.member.DummyNotifier;
import com.jitterted.mobreg.domain.port.HuddleRepository;

public class HuddleServiceFactory {

    public static HuddleService createHuddleServiceForTest(HuddleRepository huddleRepository) {
        return new HuddleService(huddleRepository, new InMemoryMemberRepository(), new DummyNotifier());
    }

}
