package com.hd.book.config;

import com.hd.book.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// AuditorAware 是一个通用接口，你可以决定要审计的内容
public class ApplicationAuditAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // authentication 为 null || authentication 未经过身份验证 || authentication 是匿名身份验证令牌的实例
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }
        // User userPrincipal = (User)authentication.getPrincipal();

        // Optional.ofNullable(value): 用一个可能为 null 的值创建一个 Optional 实例
        return Optional.ofNullable(authentication.getName());
    }
}
