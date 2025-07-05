package com.hd.book.feedback;

import com.hd.book.book.Book;
import com.hd.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Feedback extends BaseEntity {

    private Double note; // 1-5 starts
    private String comment; // 评论

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
