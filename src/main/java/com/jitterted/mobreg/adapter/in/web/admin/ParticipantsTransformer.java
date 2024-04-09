package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.Rotation;

import java.util.HashMap;
import java.util.Map;

import static com.jitterted.mobreg.adapter.RotationRole.ROLE_DRIVER;
import static com.jitterted.mobreg.adapter.RotationRole.ROLE_NAVIGATOR;
import static com.jitterted.mobreg.adapter.RotationRole.ROLE_NEXT_DRIVER;
import static com.jitterted.mobreg.adapter.RotationRole.ROLE_REST_OF_PARTICIPANTS;

public class ParticipantsTransformer {

    public static Map<String, Object> participantRolesToNames(EnsembleTimer ensembleTimer) {
        Map<String, Object> rolesToNames = new HashMap<>();
        Rotation rotation = ensembleTimer.rotation();
        rolesToNames.put(ROLE_DRIVER.idString(), rotation.driver().firstName());
        rolesToNames.put(ROLE_NAVIGATOR.idString(), rotation.navigator().firstName());
        rolesToNames.put(ROLE_NEXT_DRIVER.idString(), rotation.nextDriver().firstName());
        rolesToNames.put(ROLE_REST_OF_PARTICIPANTS.idString(),
                         rotation.restOfParticipants().stream()
                                 .map(Member::firstName)
                                 .toList());
        return rolesToNames;
    }

}
