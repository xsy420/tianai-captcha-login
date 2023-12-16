package com.xsy420.login.services;

import com.xsy420.login.common.CommonResult;
import com.xsy420.login.domain.request.LoginRequest;
import com.xsy420.login.domain.response.LoginResponse;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    CommonResult<LoginResponse> login(HttpServletRequest httpServletRequest, LoginRequest loginRequest);
}
