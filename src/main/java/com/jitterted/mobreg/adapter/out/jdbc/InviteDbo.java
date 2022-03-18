package com.jitterted.mobreg.adapter.out.jdbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("invites")
public class InviteDbo {
    @Id
    private Long id;

    private String token;
    private String githubUsername;
    private LocalDateTime dateCreatedUtc;
    private boolean wasUsed;
    private LocalDateTime dateUsedUtc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public LocalDateTime getDateCreatedUtc() {
        return dateCreatedUtc;
    }

    public void setDateCreatedUtc(LocalDateTime dateCreatedUtc) {
        this.dateCreatedUtc = dateCreatedUtc;
    }

    public boolean isWasUsed() {
        return wasUsed;
    }

    public void setWasUsed(boolean wasUsed) {
        this.wasUsed = wasUsed;
    }

    public LocalDateTime getDateUsedUtc() {
        return dateUsedUtc;
    }

    public void setDateUsedUtc(LocalDateTime dateUsedUtc) {
        this.dateUsedUtc = dateUsedUtc;
    }
}
