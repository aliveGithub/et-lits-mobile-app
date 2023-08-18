package org.moa.etlits.ui.viewmodels.login;


import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {

    @Nullable
    private String username;

    @Nullable
    private LoginStatus loginStatus;

    @Nullable
    private Integer error;
    public LoginResult(@Nullable String username, @Nullable LoginStatus loginStatus) {
        this.username = username;
        this.loginStatus = loginStatus;
    }

    public LoginResult(@Nullable Integer error, @Nullable LoginStatus loginStatus) {
        this.error = error;
        this.loginStatus = loginStatus;
    }

    @Nullable
    public Integer getError() {
        return error;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public boolean isLoggedIn() {
        return LoginStatus.SUCCESS.equals(loginStatus);
    }
    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public enum LoginStatus {
        SUCCESS,
        FAIL
    }
}