package com.hd.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @Email(message = "电子邮件格式不正确")
    @NotEmpty(message = "邮箱是必填项")
    @NotBlank(message = "邮箱是必填项")
    private String email;
    @NotEmpty(message = "密码是必填项")
    @NotBlank(message = "密码是必填项")
    @Size(min = 8, message = "密码长度应至少为8个字符")
    private String password;
}
