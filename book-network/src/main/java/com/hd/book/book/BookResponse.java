package com.hd.book.book;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner; // 用户全名
    private byte[] cover;
    private double rate; // 评论星级(Feedback 中的 note)的平均值
    private boolean archived;
    private boolean shareable;
}
