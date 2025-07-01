package com.hd.book.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // 基于角色的身份验证 启用方法安全
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

     private final JwtAuthFilter jwtAuthFilter;
     private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults())
                // 禁用 csrf
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // requestMatchers：可以传递一个字符串列表或一个模式列表，这将代表应用程序白名单
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        // 其余都需经过身份验证
                        .anyRequest().authenticated()
                )
                // 每个请求都应该经过身份验证，这意味着我们不应该存储身份验证状态或会话状态, 所以session应该是无状态的
                .sessionManagement(session -> session
                        // session 创建规则，STATELESS(无状态)
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 告诉 spring 使用哪个身份验证提供程序
                .authenticationProvider(authenticationProvider)
                // 添加自定义过滤器，并指定在哪个过滤器之前
                // 实现 JWT 身份验证过滤器时，我们检查所有内容，然后更新 SecurityContextHolder，之后将调用用户名密码身份验证过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}
