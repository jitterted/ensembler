package com.jitterted.mobreg.domain;

public class MemberNotFoundByGitHubUsernameException extends RuntimeException {
    public MemberNotFoundByGitHubUsernameException() {
        super();
    }

    public MemberNotFoundByGitHubUsernameException(String message) {
        super(message);
    }
}
