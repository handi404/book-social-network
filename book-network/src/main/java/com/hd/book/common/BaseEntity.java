package com.hd.book.common;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id
    @GeneratedValue
    private Integer id;

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
    private String createdBy; // 创建者

    @LastModifiedBy
    @Column(insertable = false)
    private String lastModifiedBy; // 最后修改者
}
