package com.hd.book.feedback;

import com.hd.book.book.Book;
import com.hd.book.book.BookRepository;
import com.hd.book.exception.OperationNotPermittedException;
import com.hd.book.user.User;
import com.hd.book.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // 得到被反馈的book
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));
        // 检查此书是否已存档且不可共享
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("您无法对已存档或不可共享的书籍进行反馈");
        }
        // 检查此书是否是用户自己的书
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("您无法对自己的书籍进行反馈");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }
}
