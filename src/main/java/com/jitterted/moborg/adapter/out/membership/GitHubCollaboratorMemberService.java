package com.jitterted.moborg.adapter.out.membership;

import com.jitterted.moborg.domain.MemberService;
import org.springframework.stereotype.Service;

@Service
public class GitHubCollaboratorMemberService implements MemberService {
    @Override
    public boolean isMember(String username) {
        // TODO: use the github API to check for collaborator (see GitHubApiTest)
        return false;
    }
}
