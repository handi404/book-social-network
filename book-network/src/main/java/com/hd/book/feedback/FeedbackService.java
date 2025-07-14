package com.hd.book.feedback;

import com.hd.book.book.Book;
import com.hd.book.book.BookRepository;
import com.hd.book.common.PageResponse;
import com.hd.book.exception.OperationNotPermittedException;
import com.hd.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // 分页请求的信息（请求第几页，每页多少条数据，以及可选的排序信息）
        Pageable pageable = PageRequest.of(page, size);
        // 分页查询返回的结果
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        // 当前页数据
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(response -> feedbackMapper.toFeedbackResponse(response, user.getId()))
                .toList();
        return PageResponse.<FeedbackResponse>builder()
                .content(feedbackResponses)
                .number(feedbacks.getNumber())
                .size(feedbacks.getSize())
                .totalElements(feedbacks.getTotalElements())
                .totalPages(feedbacks.getTotalPages())
                .first(feedbacks.isFirst())
                .last(feedbacks.isLast())
                .build();
    }
}
