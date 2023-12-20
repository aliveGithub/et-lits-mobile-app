package org.moa.etlits.ui.viewmodels.login;


import java.util.Set;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {

    @Nullable
    private String username;

    private String password;

    @Nullable
    private LoginStatus loginStatus;

    private Set<String> roles;

    @Nullable
    private Integer error;
    public LoginResult(@Nullable String username, String password, Set<String> roles, @Nullable LoginStatus loginStatus) {
        this.username = username;
        this.password = password;
        this.roles = roles;
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

    public Set<String> getRoles() {
        return roles;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }


    public String getPassword() {
        return password;
    }

   public enum LoginStatus {
        SUCCESS,
        FAIL
    }
}