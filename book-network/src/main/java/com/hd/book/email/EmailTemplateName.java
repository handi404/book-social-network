package com.hd.book.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"); // 调用构造方法 EmailTemplateName("activate_account")

    private final String name;

    private EmailTemplateName(String name) {
        this.name = name;
    }
}
