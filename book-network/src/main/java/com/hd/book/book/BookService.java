package com.hd.book.book;

import com.hd.book.common.PageResponse;
import com.hd.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // 分页请求的信息（请求第几页，每页多少条数据，以及可选的排序信息）
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        // 分页查询返回的结果。需查询除连接用户外的所有可共享书籍
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageRequest, user.getId());
        // 当前页数据
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(books.getNumber())
                .size(books.getSize())
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .first(books.isFirst())
                .last(books.isLast())
                .build();
    }


}
