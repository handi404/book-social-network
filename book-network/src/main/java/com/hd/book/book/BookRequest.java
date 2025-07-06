package com.hd.book.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String title,
        @NotNull(message = "101")
        @NotEmpty(message = "101")
        String authorName,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String isbn, // 编号
        @NotNull(message = "103")
        @NotEmpty(message = "103")
        String synopsis, // 简介
        boolean shareable // 是否共享
) {
}
