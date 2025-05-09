package com.mincho.herb.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password
    ) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        log.info("host:{}, port:{}, username:{}, password:{}",host, port, username, password);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);


        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");       // 이메일 전송 프로토콜 설정
        props.put("mail.smtp.auth", "true");                // SMTP 인증 활성화
        props.put("mail.smtp.starttls.enable", "true");     // TLS(암호화) 활성화
        props.put("mail.debug", "true"); // 디버깅

        return mailSender;
    }
}
