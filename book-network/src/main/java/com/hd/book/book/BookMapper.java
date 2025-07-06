package com.hd.book.book;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(@Valid BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .isbn(request.isbn())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false) // 默认不存档
                .shareable(request.shareable())
                .build();
    }
}
