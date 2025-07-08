package com.hd.book.book;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBookResponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private double rate; // 评论星级(Feedback 中的 note)的平均值
    private boolean returned; // 是否归还
    private boolean returnApproved; // 书主是否批准归还
}
