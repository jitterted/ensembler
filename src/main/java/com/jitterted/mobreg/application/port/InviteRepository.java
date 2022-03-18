package com.jitterted.mobreg.application.port;

import java.time.LocalDateTime;

public interface InviteRepository {
    boolean existsByTokenAndGithubUsernameAndWasUsedFalse(String token, String githubUsername);

    void markInviteAsUsed(String token, LocalDateTime dateUsedUtc);

    void createInviteFor(String githubUsername, String token, LocalDateTime dateCreatedUtc);
}
