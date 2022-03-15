package com.jitterted.mobreg.adapter.out.jdbc;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface InviteRepository extends CrudRepository<InviteDbo, Long> {
    boolean existsByTokenAndGithubUsernameAndWasUsedFalse(String token, String githubUsername);

    @Modifying
    @Query("UPDATE invites SET was_used = true, date_used_utc = :dateUsedUtc WHERE token = :token")
    void markInviteAsUsed(@Param("token") String token,
                          @Param("dateUsedUtc") LocalDateTime dateUsedUtc);
}
