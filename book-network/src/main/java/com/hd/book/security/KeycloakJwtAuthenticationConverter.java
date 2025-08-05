package com.hd.book.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        return new JwtAuthenticationToken(
                source,
                Stream.concat(
                        new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                        extractResourceRoles(source).stream()
                ).collect(Collectors.toSet())
        );
    }

    // JWT 令牌中提取这个 resource_access 对象，然后提取 account 对象，
    // 然后提取 roles 列表，最后将每个 roles 中的每个 role 转换为 spring role
    public Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        var resourceAccess = new HashMap<>(jwt.getClaim("resource_access"));
        var eternal = (Map<String, List<String>>) resourceAccess.get("account");
        var roles = eternal.get("roles");

        return roles.stream()
                //  spring 角色默认是 `ROLE_[角色名称]`，并且若角色名称中有 `-`，需替换为 `_`
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                .collect(Collectors.toSet());
    }
}
