package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.Rotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantsTransformer {
    public static Map<String, List<String>> participantsToRolesAndNames(EnsembleTimer ensembleTimer) {
        Map<String, List<String>> rolesToNames = new HashMap<>();
        Rotation rotation = ensembleTimer.rotation();
        mapRoleToNames("Driver", List.of(rotation.driver()), rolesToNames);
        mapRoleToNames("Navigator", List.of(rotation.navigator()), rolesToNames);
        mapRoleToNames("Next Driver", List.of(rotation.nextDriver()), rolesToNames);
        mapRoleToNames("Participant", rotation.restOfParticipants(), rolesToNames);
        return rolesToNames;
    }

    private static void mapRoleToNames(String roleName,
                                       List<Member> members,
                                       Map<String, List<String>> rolesToNames) {
        List<String> memberNames = members.stream()
                                          .map(Member::firstName)
                                          .toList();
        rolesToNames.put(roleName, memberNames);
    }
}
