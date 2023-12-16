package com.xsy420.login.services;

import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import com.xsy420.login.domain.request.ImageCaptchaCheckRequest;

import javax.servlet.http.HttpServletRequest;

public interface CaptchaService {
    CaptchaResponse<ImageCaptchaVO> generate(HttpServletRequest request) throws Exception;

    ApiResponse<?> checkCaptcha(ImageCaptchaCheckRequest data, HttpServletRequest request);
}
