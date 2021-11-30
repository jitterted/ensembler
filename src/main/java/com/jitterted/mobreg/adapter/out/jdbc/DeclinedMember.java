package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;

//equivalent to @Table("declined_member") -- WARNING: Case-sensitive
public class DeclinedMember {
    @Id
    public Long memberId;

    // this constructor is necessary for Spring Data JDBC to use the name of the public Long memberId
    // otherwise it'd use the name of the parameter to the single-parameter constructor (???)
    public DeclinedMember() {
    }

    public DeclinedMember(long id) {
        this.memberId = id;
    }

    static DeclinedMember toEntityId(MemberId memberId) {
        return new DeclinedMember(memberId.id());
    }

    MemberId asMemberId() {
        return MemberId.of(memberId);
    }

}
