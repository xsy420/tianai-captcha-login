package com.xsy420.login.controller;

import com.xsy420.login.common.CommonResult;
import com.xsy420.login.domain.request.*;
import com.xsy420.login.domain.response.LoginResponse;
import com.xsy420.login.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("v1/basis-login")
@RestController
public class BasisLoginController {
    private UserService userService;

    @Autowired
    public void setLoginService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public CommonResult<LoginResponse> login(HttpServletRequest httpServletRequest, @RequestBody LoginRequest loginRequest) {
        return userService.login(httpServletRequest, loginRequest);
    }

}
