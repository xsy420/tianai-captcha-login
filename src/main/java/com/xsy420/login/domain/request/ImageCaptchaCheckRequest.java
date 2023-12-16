package com.xsy420.login.domain.request;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCaptchaCheckRequest {
    private String id;
    private ImageCaptchaTrack data;
}
