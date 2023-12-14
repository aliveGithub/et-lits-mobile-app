package org.moa.etlits.api.response;

import java.util.Set;

public class AuthResponse {
    private Set<String> roles;

    public AuthResponse(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
