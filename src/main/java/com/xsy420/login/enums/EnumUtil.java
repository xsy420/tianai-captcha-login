package com.xsy420.login.enums;

import java.util.*;

public class EnumUtil {

    public static <V, T extends EnumItem<V>> T fromCode(Class<T> enumType, V code) {
        if (code == null) {
            return null;
        }
        for (T enumItem : enumType.getEnumConstants()) {
            if (Objects.equals(code, enumItem.getCode())) {
                return enumItem;
            }
        }
        return null;

    }

    public static <T extends EnumItem<?>> T fromValue(Class<T> enumType, String value) {
        if (value == null) {
            return null;
        }
        for (T enumItem : enumType.getEnumConstants()) {
            if (Objects.equals(value, enumItem.getValue())) {
                return enumItem;
            }
        }
        return null;
    }

}
