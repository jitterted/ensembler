package com.jitterted.mobreg.adapter.out.membership;


import com.spotify.github.v3.User;
import com.spotify.github.v3.clients.GitHubClient;
import com.spotify.github.v3.clients.RepositoryClient;
import com.spotify.github.v3.repos.Repository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;

@Tag("manual")
@SpringBootTest
// pull in the GitHub Personal Access Token property
@TestPropertySource("file:/Users/tedyoung/.config/spring-boot/spring-boot-devtools.properties")
public class GitHubApiTest {

    @Value("${github.personal.access.token}")
    private String personalAccessToken;

    private static final String GITHUB_API_URI = "https://api.github.com/";

    @Test
    public void canAccessCollaboratorsOfOwnedRepository() throws Exception {
        assertThat(personalAccessToken)
                .isNotBlank();
        URI gitHubUri = URI.create(GITHUB_API_URI);
        final GitHubClient github = GitHubClient.create(gitHubUri, personalAccessToken);

        // Check for specific user as collaborator
        RepositoryClient repositoryClient = github.createRepositoryClient("jitterted", "moborg");
        assertThat(repositoryClient.isCollaborator("tedyoung").get())
                .isTrue();

        // check ownership info
        Repository repository = repositoryClient.getRepository().get();
        User owner = repository.owner();
        assertThat(owner.login())
                .isEqualTo("jitterted");
        assertThat(owner.type())
                .isEqualTo("Organization");
    }
}
