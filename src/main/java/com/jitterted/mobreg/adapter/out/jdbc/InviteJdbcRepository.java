package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.port.InviteRepository;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface InviteJdbcRepository extends CrudRepository<InviteDbo, Long>, InviteRepository {

    @Modifying
    @Query("UPDATE invites SET was_used = true, date_used_utc = :dateUsedUtc WHERE token = :token")
    void markInviteAsUsed(@Param("token") String token,
                          @Param("dateUsedUtc") LocalDateTime dateUsedUtc);

    @Modifying
    @Query("""
            INSERT INTO invites (token, github_username, date_created_utc, was_used)
            VALUES (:token, :githubUsername, :dateCreatedUtc, false)
            """)
    void createInviteFor(@Param("githubUsername") String githubUsername,
                         @Param("token") String token,
                         @Param("dateCreatedUtc") LocalDateTime dateCreatedUtc);

}
