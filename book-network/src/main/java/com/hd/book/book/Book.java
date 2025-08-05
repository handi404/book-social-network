package com.hd.book.book;

import com.hd.book.common.BaseEntity;
import com.hd.book.feedback.Feedback;
import com.hd.book.history.BookTransactionHistory;
import com.hd.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn; // 编号
    private String synopsis; // 简介
    private String bookCover; // 封面位置
    private boolean archived; // 是否存档
    private boolean shareable; // 是否共享

    /*@ManyToOne
    @JoinColumn(name = "owner_id") // 指定外键列的名称
    private User owner;*/
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @Transient // 标识该属性不参与持久化，不映射到数据库
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);

        // 计算为 3.25 类似的数字，这种情况下返回 3.0 || 为 3.6 返回 4.0
        // 四舍五入
        return Math.round(rate * 10.0) / 10.0;
    }
}
