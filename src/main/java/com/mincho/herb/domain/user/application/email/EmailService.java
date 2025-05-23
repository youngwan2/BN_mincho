package com.mincho.herb.domain.user.application.email;

import com.mincho.herb.domain.user.dto.VerificationRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface EmailService {

    MimeMessage createMail(String email, String authCode) throws MessagingException;
    boolean emailVerification(VerificationRequestDTO verificationRequestDTO);
    boolean emailVerificationForReset(VerificationRequestDTO verificationRequestDTO);
    void sendVerificationCodeForSignUp(String toMail) throws MessagingException;
    void sendVerificationCodeForReset(String email) throws MessagingException;
    String sendResetPassword(String toMail) throws  MessagingException;
    boolean validateMx(String domain);
}
