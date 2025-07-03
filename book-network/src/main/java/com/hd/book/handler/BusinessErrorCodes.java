package com.hd.book.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"), // 调用构造方法 BusinessErrorCodes(0, NOT_IMPLEMENTED, "No code")
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "当前密码不正确"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "新密码不匹配"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "用户账户已被锁定"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "用户账户已被禁用"),
    BAD_CREDENTIALS(304, FORBIDDEN, "登录名或密码不正确");

    private final Integer code; // 错误代码
    private final String description; // 描述
    private final HttpStatus httpStatus; // HTTP 状态

    BusinessErrorCodes(Integer code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
