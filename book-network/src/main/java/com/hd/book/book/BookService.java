package com.hd.book.book;

import com.hd.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Integer save(@Valid BookRequest request, Authentication connectedUser) {
        // 获取用户
        User user = ((User) connectedUser.getPrincipal());
        // DTO to Entity
        Book book = bookMapper.toBook(request);
        // 设置所有者
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("未找到与 ID::" + bookId + " 匹配的书籍"));
    }
}
