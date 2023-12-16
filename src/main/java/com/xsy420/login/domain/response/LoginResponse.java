package com.xsy420.login.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String jwt;

    public LoginResponse() {
    }

    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }

}
