package com.hd.book.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        /* authProvider.setUserDetailsService(userDetailsService); 方法已被弃用
         * 最直接的替代方案是使用 `DaoAuthenticationProvider` 的构造函数
         * new DaoAuthenticationProvider(userDetailsService)
         * */
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    // 指定密码编码方式
    public PasswordEncoder passwordEncoder() {
        // 想要验证用户时，需要知道使用哪个密码编码才能够使用正确的算法解码密码
        return new BCryptPasswordEncoder();
    }
}
