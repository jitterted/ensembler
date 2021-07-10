package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("REGISTERED_MEMBERS") // WARNING: Case-sensitive
public class MemberEntityId {
    @Id
    public Long member_id;

    public MemberEntityId(long id) {
        this.member_id = id;
    }

    static MemberEntityId toEntityId(Member member) {
        return new MemberEntityId(member.getId().id());
    }
}
