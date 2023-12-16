package com.xsy420.login.common;

import org.jetbrains.annotations.NotNull;

public class CommonResponse {

    public static @NotNull <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(0, message, data);
    }

    @NotNull
    public static <T> CommonResult<T> error(String message) {
        return error(message, null);
    }

    @NotNull
    public static <T> CommonResult<T> error(String message, T data) {
        return new CommonResult<>(1, message, data);
    }
}
