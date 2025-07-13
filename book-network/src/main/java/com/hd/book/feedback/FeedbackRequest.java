package com.hd.book.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest(
        @Positive(message = "200") // 被注释的元素必须是一个正数
        @Min(value = 0, message = "201")
        @Max(value = 5, message = "202")
        Double note, // 1-5 starts
        @NotNull(message = "203")
        @NotEmpty(message = "203")
        @NotBlank(message = "203")
        String comment, // 评论
        @NotNull(message = "204")
        Integer bookId
) {
}
