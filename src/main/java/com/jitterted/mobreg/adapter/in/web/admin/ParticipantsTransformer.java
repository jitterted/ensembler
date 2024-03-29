package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.Rotation;

import java.util.HashMap;
import java.util.Map;

public class ParticipantsTransformer {

    public static Map<String, Object> participantRolesToNames(EnsembleTimer ensembleTimer) {
        Map<String, Object> rolesToNames = new HashMap<>();
        Rotation rotation = ensembleTimer.rotation();
        rolesToNames.put("driver", rotation.driver().firstName());
        rolesToNames.put("navigator", rotation.navigator().firstName());
        rolesToNames.put("nextDriver", rotation.nextDriver().firstName());
        rolesToNames.put("restOfParticipants", rotation.restOfParticipants().stream()
                                                       .map(Member::firstName)
                                                       .toList());
        return rolesToNames;
    }

}
