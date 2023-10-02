package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;

//equivalent to @Table("spectator_member") -- WARNING: Case-sensitive
public class SpectatorMember {
    @Id
    public Long memberId;

    // this constructor is necessary for Spring Data JDBC to use the name of the public Long memberId
    // otherwise it'd use the name of the parameter to the single-parameter constructor (???)
    public SpectatorMember() {
    }

    public SpectatorMember(long id) {
        this.memberId = id;
    }

    static SpectatorMember toEntityId(MemberId memberId) {
        return new SpectatorMember(memberId.id());
    }

    MemberId asMemberId() {
        return MemberId.of(memberId);
    }

}
