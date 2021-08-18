package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("registered_members") // WARNING: Case-sensitive
public class MemberEntityId {
    @Id
    public Long memberId;

    // this constructor is necessary for Spring Data JDBC to use the name of the public Long memberId
    // otherwise it'd use the name of the parameter to the single-parameter constructor (???)
    public MemberEntityId() {
    }

    public MemberEntityId(long id) {
        this.memberId = id;
    }

    static MemberEntityId toEntityId(MemberId memberId) {
        return new MemberEntityId(memberId.id());
    }

    MemberId asMemberId() {
        return MemberId.of(memberId);
    }

}
