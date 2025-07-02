package com.hd.book.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    /*
    * @NotNull: 被注释的元素必须不为 null。
    * @NotEmpty: 被注释的元素（如字符串、集合、Map 或数组）必须不为 null 且不能为空。
    * @NotBlank: 被注释的字符串必须非 null 且必须包含至少一个非空白字符。
    * @Size(min=, max=): 被注释的元素的大小必须在指定的范围内 (适用于字符串、集合、Map、数组)。
    * @Email`: 被注释的元素必须是格式正确的电子邮件地址。
    * */
    // message: 未通过验证需发送的消息
    @NotEmpty(message = "姓名是必填项")
    @NotBlank(message = "姓名是必填项")
    private String firstname;
    @NotEmpty(message = "姓名是必填项")
    @NotBlank(message = "姓名是必填项")
    private String lastname;
    @Email(message = "电子邮件格式不正确")
    @NotEmpty(message = "邮箱是必填项")
    @NotBlank(message = "邮箱是必填项")
    private String email;
    @NotEmpty(message = "密码是必填项")
    @NotBlank(message = "密码是必填项")
    @Size(min = 8, message = "密码长度应至少为8个字符")
    private String password;
}
