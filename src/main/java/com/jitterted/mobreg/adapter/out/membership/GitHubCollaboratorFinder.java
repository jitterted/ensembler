package com.jitterted.mobreg.adapter.out.membership;

import com.spotify.github.v3.clients.GitHubClient;
import com.spotify.github.v3.clients.RepositoryClient;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.concurrent.ExecutionException;

// TODO: use when new user is signing up
public class GitHubCollaboratorFinder {
    @Value("${github.personal.access.token}")
    private String personalAccessToken;

    // TODO: pull from app properties
    private static final String GITHUB_API_URI = "https://api.github.com/";

    public boolean isMember(String username) {
        URI gitHubUri = URI.create(GITHUB_API_URI);
        GitHubClient github = GitHubClient.create(gitHubUri, personalAccessToken);

        // Check for specific user as collaborator
        RepositoryClient repositoryClient = github.createRepositoryClient("tedyoung", "mycmt2-blackjack-20210322");
        try {
            return repositoryClient.isCollaborator(username).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
}
