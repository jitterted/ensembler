package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.Rotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantsTransformer {
    public static Map<String, List<String>> participantsToRolesAndNames(MemberRepository memberRepository, EnsembleTimer ensembleTimer) {
        Map<String, List<String>> rolesToNames = new HashMap<>();
        Rotation rotation = ensembleTimer.rotation();
        mapRoleToNames("Driver", List.of(rotation.driver()), rolesToNames, memberRepository);
        mapRoleToNames("Navigator", List.of(rotation.navigator()), rolesToNames, memberRepository);
        mapRoleToNames("Next Driver", List.of(rotation.nextDriver()), rolesToNames, memberRepository);
        mapRoleToNames("Participant", rotation.restOfParticipants(), rolesToNames, memberRepository);
        return rolesToNames;
    }

    private static void mapRoleToNames(String roleName,
                                       List<MemberId> memberIds,
                                       Map<String, List<String>> rolesToNames,
                                       MemberRepository memberRepository) {
        List<String> memberNames = memberIds.stream()
                                            .map(memberId -> memberRepository
                                                    .findById(memberId)
                                                    .orElseThrow())
                                            .map(Member::firstName)
                                            .toList();
        rolesToNames.put(roleName, memberNames);
    }
}
