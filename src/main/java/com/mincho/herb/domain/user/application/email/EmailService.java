package com.mincho.herb.domain.user.application.email;

import com.mincho.herb.domain.user.dto.RequestVerification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface EmailService {

    MimeMessage createMail(String email, String authCode) throws MessagingException;
    boolean emailVerification(RequestVerification requestVerification);
    void sendVerificationCode(String toMail) throws MessagingException;
}
