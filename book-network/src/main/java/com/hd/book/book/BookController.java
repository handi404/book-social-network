package com.hd.book.book;

import com.hd.book.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    /**
     * save book
     */
    @PostMapping
    public ResponseEntity<Integer> save(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser // 获得连接的用户
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    /**
     * 按 ID 查找 book
     */
    @GetMapping("/{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer bookId
    ) {
        return ResponseEntity.ok(service.findById(bookId));
    }

    /*
    * 查找网络中所有可共享的书籍(除连接用户外)
    * */
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page, // 当前页
            @RequestParam(name = "size", defaultValue = "10", required = false) int size, // 页大小
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    /*
    * 按 owner(所有者)查找所有书籍
    * */
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }

    /*
     * 查找所有 borrowed(借阅)书籍
     * */
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
    }
}
