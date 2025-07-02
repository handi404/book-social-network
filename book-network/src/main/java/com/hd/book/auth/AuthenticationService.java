package com.hd.book.auth;

import com.hd.book.email.EmailService;
import com.hd.book.email.EmailTemplateName;
import com.hd.book.role.RoleRepository;
import com.hd.book.user.Token;
import com.hd.book.user.TokenRepository;
import com.hd.book.user.User;
import com.hd.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

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
