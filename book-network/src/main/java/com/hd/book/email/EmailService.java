package com.hd.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender; // email 发送器
    private final SpringTemplateEngine templateEngine; // 需要发送 HTML 模板 来自org.thymeleaf.spring6

    /*
    * 发送电子邮件可能有点繁重或很耗时，所以不想阻塞用户并让用户等待, 直到电子邮件发送完毕，
    * 所以我们需要异步发送这封邮件 使用@Async
    * 但是当使用异步注解时，需要在启动类添加 @EnableAsync 注解 以启用异步
    * */
    @Async
    public void sendEmail(
            String to, // 收件人
            String username,
            EmailTemplateName emailTemplate, // 指定使用的 HTML 模板
            String confirmationUrl, // 确认 URL
            String activationCode, // 激活码
            String subject // 邮件主题
    ) throws MessagingException {
        String templateName;
        if (emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplate.getName();
        }

        // 配置邮件发送器
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        // 传递邮件模板（即 HTML 模板）的参数
        Map<String , Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activationCode", activationCode);

        Context context = new Context(); // 来自 org.thymeleaf.context
        context.setVariables(properties);

        // 添加电子邮件的属性
        messageHelper.setFrom("contact@handi.com");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);

        // 处理模板
        String template = templateEngine.process(templateName, context);

        // 邮件文本内容
        messageHelper.setText(template, true); // 传递一个标志来表明这是否是一个 HTML 模板

        mailSender.send(mimeMessage);
    }
}
