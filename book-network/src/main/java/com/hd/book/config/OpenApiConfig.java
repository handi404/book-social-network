package com.hd.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

// 定义
@OpenAPIDefinition(
        // 信息
        info = @Info(
                // 联系人信息
                contact = @Contact(
                        name = "handi",
                        email = "contact@handi.com",
                        url = "https://handi404.github.io"
                ),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification",
                version = "1.0",
                // 许可证
                license = @License(
                        name = "Licence name",
                        url = "https://some-url.com"
                ),
                // 服务条款
                termsOfService = "Terms of service"
        ),
        // servers 是一个列表
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8088/api/v1"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://handi404.github.io"
                )
        },
        // 安全, 可以传递安全要求列表
        security = {
                // 安全要求
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
// 安全方案
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
