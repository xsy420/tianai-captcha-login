package com.xsy420.login.enums;

import java.io.Serializable;

public interface EnumItem<V> extends Serializable {
    V getCode();
    String getValue();
    String getName();
}
