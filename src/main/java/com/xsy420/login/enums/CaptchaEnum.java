package com.xsy420.login.enums;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;

public enum CaptchaEnum implements EnumItem<Integer> {
    SLIDER(1, CaptchaTypeConstant.SLIDER),
    ROTATE(2, CaptchaTypeConstant.ROTATE),
    CONCAT(3, CaptchaTypeConstant.CONCAT),
    WORD_IMAGE_CLICK(4, CaptchaTypeConstant.WORD_IMAGE_CLICK),
    IMAGE_TEXT_IDENTIFY(5, "IMAGE_TEXT_IDENTIFY");

    CaptchaEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    private final Integer code;
    private final String name;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
