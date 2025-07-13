package com.hd.book.feedback;

import com.hd.book.book.Book;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .shareable(false) // 无需提供且无影响 :: 仅为满足 Lombok 要求
                        .archived(false) // 无需提供且无影响 :: 仅为满足 Lombok 要求
                        .build())
                .build();
    }
}
