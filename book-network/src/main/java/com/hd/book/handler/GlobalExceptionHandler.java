package com.hd.book.handler;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.hd.book.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

// 使用 @RestControllerAdvice 捕获异常并统一处理响应
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获处理指定异常
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    // 捕获禁用异常
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    // 不良凭证异常
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException() {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error(BAD_CREDENTIALS.getDescription())
                                .build()
                );
    }

    // 消息异常 例如当我们无法发送电子邮件时
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    // 当用户发送无效的数据时: 例如注册用户却不提供email密码名字会捕获异常，
    // 例如来自 Controller 中 @Valid 所抛出的
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        // 错误集合，
        // 例如 RegistrationRequest 中会发现同样的错误消息，这是为什么需要使用一个Set集合
        Set<String> errors = new HashSet<>();
        // 将错误消息添加到错误集合中
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    // 全局异常
    // 例外情况，例如如果得到一个应用程序中没有处理的异常或任何类型的异常，
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        // 记录异常至少可以在控制台中看到它，因为这个全局异常处理程序将吸收任何异常
        exp.printStackTrace();

        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("内部错误，请联系管理员")
                                .error(exp.getMessage())
                                .build()
                );
    }

}
