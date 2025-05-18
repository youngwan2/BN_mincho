package com.mincho.herb.global.io;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.main.sender.email}")
    private String senderEmail;


    public void sendEmail(String title, String content, String username, String toMail) throws MessagingException {
        MimeMessage message = createMessage(title, content, username, toMail);
        javaMailSender.send(message);

    }

    private MimeMessage createMessage(String title, String content, String username, String toMail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, toMail);
        message.setSubject(title);

        String body = "<html lang='ko'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>" + title + "</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; text-align: center;'>" +
                "<div style='width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); padding: 30px;'>" +
                "<h3 style='font-size: 24px; color: #333333; margin-bottom: 20px; font-weight: bold;'>" + title + "</h3>" +
                "<p style='font-size: 18px; color: #555555;'>" + username + "님, 아래 내용을 확인해주세요.</p>" +
                "<div style='font-size: 18px; color: #333333; background-color: #f9f9f9; padding: 20px; border-radius: 10px; margin: 20px 0;'>" +
                content +
                "</div>" +
                "<p style='font-size: 16px; color: #777777; margin-top: 30px;'>감사합니다. Mincho 팀 드림</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        message.setText(body, "UTF-8", "html");
        return message;
    }
}
