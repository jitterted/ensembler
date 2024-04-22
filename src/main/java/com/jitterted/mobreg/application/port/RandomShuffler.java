package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Member;

import java.util.Collections;
import java.util.List;

public class RandomShuffler implements Shuffler {
    @Override
    public void shuffle(List<Member> participants) {
        Collections.shuffle(participants);
    }
}
