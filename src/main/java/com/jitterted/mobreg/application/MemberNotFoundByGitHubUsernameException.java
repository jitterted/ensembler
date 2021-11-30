package com.jitterted.mobreg.application;

public class MemberNotFoundByGitHubUsernameException extends RuntimeException {
    public MemberNotFoundByGitHubUsernameException() {
        super();
    }

    public MemberNotFoundByGitHubUsernameException(String message) {
        super(message);
    }
}
