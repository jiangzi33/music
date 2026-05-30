package com.example.music.intergration;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    public void sendMail(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendHtmlMail(String to, String subject, String htmlContent) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 第二个参数true表示需要创建multipart消息，以支持复杂内容
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(sender);
        helper.setTo(to);
        helper.setSubject(subject);
        // 第二个参数true表示内容为HTML格式
        helper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }
}
