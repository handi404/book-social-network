package com.hd.book.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hd.book.book.Book;
import com.hd.book.history.BookTransactionHistory;
import com.hd.book.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// @Builder
// @Entity(name = "_user")
// @EntityListeners(AuditingEntityListener.class) // 启用审计实体监听器
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth; // 出生日期
    @Column(unique = true) // 唯一
    private String email;
    private String password;
    private boolean accountLocked; // 账户锁定
    private boolean enabled; // 启用

    // FetchType.EAGER: 加载父实体时，立即加载关联的子实体
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Role> roles;
    @OneToMany(mappedBy = "owner")
    private List<Book> books;
    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> histories;


    @CreatedDate
    // 映射实体属性到数据库表中的列，并定义列的属性
    @Column(
            // 是否可为null(默认为true)
            nullable = false,
            // 是否可更新(默认为true)
            updatable = false
    )
    private LocalDateTime createDate; // 创建日期

    @LastModifiedDate
    @Column(
            // 该列是否包含在持久化提供程序生成的 SQL INSERT 语句中(默认为true)
            // 只能是可更新的，因为可更新的默认值为true
            insertable = false
    )
    private LocalDateTime lastModified; // 最后修改日期

    @Override
    public String getName() {
        return email;
    }

    // 提供一个授予权限的集合
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // 对我们来说username是email
        return email;
    }

    // 指示用户账户是否已过期。过期的账户无法进行身份验证。
    // NonExpired 指未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 指示用户是否处于锁定状态。处于锁定状态的用户无法进行身份验证。
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }
    // 指示用户凭据（密码）是否已过期。过期的凭据将阻止身份验证。
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 指示用户是否已启用或禁用。已禁用的用户无法进行身份验证。
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // 全名
    public String fullName() {
        return firstname + " " + lastname;
    }
}
