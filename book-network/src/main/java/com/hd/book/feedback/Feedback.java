package com.hd.book.feedback;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Feedback {

    @Id
    @GeneratedValue
    private Integer id;
    private Double note; // 1-5 starts
    private String comment; // 评论

    /*
     * Auditing(审计)
     * */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate; // 创建时间
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate; // 最后修改时间
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Integer createdBy; // 创建者
    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy; // 最后修改者
}
