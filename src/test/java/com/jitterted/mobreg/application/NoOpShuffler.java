package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.Shuffler;
import com.jitterted.mobreg.domain.Member;

import java.util.List;

public class NoOpShuffler implements Shuffler {
    @Override
    public void shuffle(List<Member> participants) {
        // do nothing, leave the list alone
    }
}
