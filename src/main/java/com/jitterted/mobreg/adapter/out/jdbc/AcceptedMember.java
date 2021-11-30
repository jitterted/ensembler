package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;

//equivalent to @Table("accepted_member") -- WARNING: Case-sensitive
public class AcceptedMember {
    @Id
    public Long memberId;

    // this constructor is necessary for Spring Data JDBC to use the name of the public Long memberId
    // otherwise it'd use the name of the parameter to the single-parameter constructor (???)
    public AcceptedMember() {
    }

    public AcceptedMember(long id) {
        this.memberId = id;
    }

    static AcceptedMember toEntityId(MemberId memberId) {
        return new AcceptedMember(memberId.id());
    }

    MemberId asMemberId() {
        return MemberId.of(memberId);
    }

}
