package com.hd.book.book;

import com.hd.book.common.PageResponse;
import com.hd.book.exception.OperationNotPermittedException;
import com.hd.book.history.BookTransactionHistory;
import com.hd.book.history.BookTransactionHistoryRepository;
import com.hd.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.hd.book.book.BookSpecification.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
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

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageRequest);
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

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return PageResponse.<BorrowedBookResponse>builder()
                .content(bookResponses)
                .number(allBorrowedBooks.getNumber())
                .size(allBorrowedBooks.getSize())
                .totalElements(allBorrowedBooks.getTotalElements())
                .totalPages(allBorrowedBooks.getTotalPages())
                .first(allBorrowedBooks.isFirst())
                .last(allBorrowedBooks.isLast())
                .build();
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return PageResponse.<BorrowedBookResponse>builder()
                .content(bookResponses)
                .number(allReturnedBooks.getNumber())
                .size(allReturnedBooks.getSize())
                .totalElements(allReturnedBooks.getTotalElements())
                .totalPages(allReturnedBooks.getTotalPages())
                .first(allReturnedBooks.isFirst())
                .last(allReturnedBooks.isLast())
                .build();
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // 查找对应book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        // 安全检查(此书是否属于此连接用户)
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("您无法更改他人书籍的共享状态");
        }
        // 更新
        book.setShareable(!book.isShareable());
        // 保存
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // 查找对应book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        // 安全检查(此书是否属于此连接用户)
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("您无法更改他人书籍的存档状态");
        }
        // 更新
        book.setArchived(!book.isArchived());
        // 保存
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // 得到对应book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        // 检查此书是否未存档且可共享
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("所请求的书籍无法借阅，因为它已被归档或不可共享");
        }
        // 检查此书是否是用户自己的书
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("你不能借阅自己的书籍");
        }
        // 检查用户是否已经借阅此书且未归还
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("您已经借阅了这本书，但至今尚未归还，或者归还不被书主批准");
        }
        // 检查此书是否已被他人借阅
        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("您请求的书籍已被借出");
        }

        // 进行借阅
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
}
