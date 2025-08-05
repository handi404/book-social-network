package com.hd.book.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Service
@RequiredArgsConstructor
// 为了每次用户发送请求时，此过滤器被触发并执行, 扩展 OncePerRequestFilter
// 正如名称所示，每次请求都会进行一次过滤。
public class JwtAuthFilter {// extends OncePerRequestFilter {

    /*private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // 可以拦截每个请求 并从请求中提取数据
            @NonNull HttpServletResponse response, // 响应
            @NonNull FilterChain filterChain // 过滤器链，将包含需要执行的其他过滤器的列表
    ) throws ServletException, IOException {
        // 对于此路径的请求，无需执行将要实现的所有流程或所有代码
        if (request.getServletPath().contains("/api/v1/auth")) {
            // 调用 filterChain 执行过滤器，将请求和响应传递给下一个过滤器
            filterChain.doFilter(request, response);
            return;
        }

        // 需要在标头中传递 JWT 身份验证令牌
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        *//*
        * Check JWT token (检查是否有 JWT 令牌)
        * *//*
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            // 不要忘记调用return，无需执行剩下的部分
            return;
        }

        // 令牌存在，提取令牌
        // 为什么是位置 7，因为如果用 "Bearer " 来数，令牌从位置7开始
        jwt = authHeader.substring(7);
        // 从令牌中提取主题(userEmail)
        userEmail = jwtService.extractUsername(jwt);

        *//*
        * 检查用户是否尚未通过身份验证 如果用户已通过,不需要执行所有检查和设置或更新SecurityContextHolder
        * 当 Authentication 为空时意味着用户尚未通过身份验证 用户尚未连接
        * *//*
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            *//*
            * 检查令牌是否有效（令牌是否属于用户以及令牌是否过期）若有效更新 SecurityContextHolder
            * *//*
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // SecurityContextHolder 需要这个对象来更新
                // 创建用户时我们没有 credentials(凭据)，用null传递
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // 给它提供更多详细信息
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 更新 SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }*/
}
