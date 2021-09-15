package com.jitterted.mobreg.adapter.out.notification.pushover;

class PushoverMessageDto {
    private final String token;
    private final String user;
    private final String message;

    PushoverMessageDto(String token, String user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

}
