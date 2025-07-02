package com.hd.book.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication") // OpenAPI 的注解
public class AuthenticationController {

    private final AuthenticationService service;

    /*
    * 注册
    * */
    @PostMapping("register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            /*
            * @Valid: 这是 JSR 380/Jakarta Bean Validation 标准定义的注解。它主要用于级联校验
            * 它将验证标记的所有对象或所有字段或我们将其标记为必填项, 也会向最终用户发送或生成一条消息
            * */
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    /*
    * 身份验证
    * */
    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}
