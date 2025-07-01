package com.hd.book.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String token;
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime expiresAt; // 到期时间
    private LocalDateTime validatedAt; // 最后一次验证时间

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
