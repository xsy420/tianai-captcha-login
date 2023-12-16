package com.xsy420.login.controller;

import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import com.xsy420.login.domain.request.ImageCaptchaCheckRequest;
import com.xsy420.login.services.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证码Controller
 * 主要生成验证码
 * 获取验证码 String code = (String)request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
 */
@RestController
@RequestMapping("v1/captcha")
public class CaptchaController {

    private CaptchaService captchaService;

    @Autowired
    public void setCaptchaService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping
    public CaptchaResponse<ImageCaptchaVO> generate(HttpServletRequest request) throws Exception {
        return captchaService.generate(request);
    }

    @PostMapping("/check")
    public ApiResponse<?> checkCaptcha(@RequestBody ImageCaptchaCheckRequest data,
                                       HttpServletRequest request) {
        return captchaService.checkCaptcha(data, request);
    }

}
