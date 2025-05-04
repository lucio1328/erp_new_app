package com.lucio.erp_new_app.response;

public class LoginResult {
    private boolean success;
    private String message;
    private String sessionCookie;

    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResult(boolean success, String message, String sessionCookie) {
        this.success = success;
        this.message = message;
        this.sessionCookie = sessionCookie;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionCookie() {
        return sessionCookie;
    }
}

