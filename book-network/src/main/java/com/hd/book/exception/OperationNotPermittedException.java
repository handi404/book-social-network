package com.hd.book.exception;

// 操作未被授权异常
public class OperationNotPermittedException extends RuntimeException{

    public OperationNotPermittedException(String message) {
        super(message); // 调用父类构造方法
    }
}
