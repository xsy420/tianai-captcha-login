package com.xsy420.login.domain.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private String username;
    private String password;
}
