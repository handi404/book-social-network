package com.hd.book.feedback;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private Double note; // 1-5 starts
    private String comment; // 评论
    private boolean ownFeedback; // 是否是连接用户的反馈
}
