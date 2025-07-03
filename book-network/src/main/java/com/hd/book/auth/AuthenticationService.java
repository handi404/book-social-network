package com.hd.book.auth;

import com.hd.book.email.EmailService;
import com.hd.book.email.EmailTemplateName;
import com.hd.book.role.RoleRepository;
import com.hd.book.security.JwtService;
import com.hd.book.user.Token;
import com.hd.book.user.TokenRepository;
import com.hd.book.user.User;
import com.hd.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    /*
    * 注册
    * */
    public void register(@Valid RegistrationRequest request) throws MessagingException {
        // 得到名为USER的Role, 分配给用户
        var userRole = roleRepository.findByName("USER")
                // todo - 更好的异常处理
                .orElseThrow(() -> new IllegalStateException("USER Role 尚未初始化"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 安全：在将密码保存到数据库之前对其进行编码
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);

        // 发送验证电子邮件
        sendValidationEmail(user);
    }

    /*
    * 身份验证
    * */
    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {

        // 在 JwtAuthFilter 中，用户通过身份验证，利用 UsernamePasswordAuthenticationToken 对象更新 SecurityContextHolder
        /*
        * 验证，用户名密码正确则返回 Authentication 类型对象
        * 用户名或密码错误，抛出异常
        * */
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 使用声明和用户信息生成 JWT
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal(); // User 实现了 Principal 接口
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /*
    * 发送验证电子邮件
    * */
    private void sendValidationEmail(User user) throws MessagingException {
        // 生成并保存激活token
        var newToken = generateAndSaveActivationToken(user);

        // 发送电子邮件
        emailService.sendEmail(
                user.getEmail(), // 收件人
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT, // 指定HTML模板
                activationUrl, // 确认Url
                newToken, // 激活码
                "Account activation" // 邮件主题
        );
    }

    private String generateAndSaveActivationToken(User user) {
        // 生成token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // 加15分钟
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    /*
    * 生成激活码
    * */
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        // 为了生成一个随机值，不应该使用 Random, 需要使用SecureRandom(安全随机)
        // 安全随机将确保生成的值是加密安全
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            // 0..9 随机索引
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

}
