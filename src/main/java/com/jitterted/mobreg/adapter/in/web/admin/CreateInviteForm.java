package com.jitterted.mobreg.adapter.in.web.admin;

class CreateInviteForm {
    private String githubUsername;
    private String email;

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
