package com.hd.book.book;

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
public class Book {

    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private String authorName;
    private String isbn; // 编号
    private String synopsis; // 简介
    private String bookCover; // 封面
    private boolean archived; // 是否存档
    private boolean shareable; // 是否共享

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
