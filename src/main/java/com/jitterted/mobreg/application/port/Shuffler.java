package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Member;

import java.util.List;

public interface Shuffler {
    void shuffle(List<Member> participants);
}
