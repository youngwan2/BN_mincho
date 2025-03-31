package com.mincho.herb.domain.user.application.email;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.user.dto.VerificationRequestDTO;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService{

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final CommonUtils commonUtils;
    private final String senderEmail;
    private final RedisTemplate<String, Object> redisTemplate;

    public EmailServiceImpl(
            UserRepository userRepository, RedisTemplate<String,Object> redisTemplate,
            JavaMailSender javaMailSender,
            CommonUtils commonUtils,
            @Value("${main.sender.email}") String senderEmail) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.javaMailSender = javaMailSender;
        this.commonUtils = commonUtils;
        this.senderEmail = senderEmail;
    }

    @Override
    public MimeMessage createMail(String email, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("이메일 인증");
        String body = "<html lang='ko'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>이메일 인증</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; text-align: center;'>" +
                "<div style='width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); padding: 30px; text-align: center;'>" +
                "<h3 style='font-size: 24px; color: #333333; margin-bottom: 20px; font-weight: bold;'>Mincho 사이트 인증 번호</h3>" +
                "<p style='font-size: 18px; color: #555555;'>요청하신 인증 번호는 아래와 같습니다.</p>" +
                "<div style='font-size: 48px; color: #4CAF50; font-weight: bold; background-color: #f1f8e9; padding: 20px; border-radius: 10px; display: inline-block; margin: 20px 0;'>" +
                "<h1 style='margin: 0;'>" + authCode + "</h1>" +
                "</div>" +
                "<p style='font-size: 16px; color: #777777; margin-top: 30px;'>이메일 인증에 감사드립니다.</p>" +
                "<p style='font-size: 18px; color: #666666; margin-top: 10px;'>감사합니다.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 이메일 인증
    @Override
    public boolean emailVerification(VerificationRequestDTO verificationRequestDTO) {
        String value = (String) redisTemplate.opsForValue().get(verificationRequestDTO.getEmail());
        log.info("input code: {}, auth code: {}", verificationRequestDTO.getCode(), value);
        boolean isVer = verificationRequestDTO.getCode().equals(value);
        
        // 인증 성공 시 레디스에서 키 제거
        if(isVer){
            redisTemplate.delete(verificationRequestDTO.getEmail());
        }
        return isVer ;
    }

    // 이메일 인증 코드 발송
    @Override
    public void sendVerificationCode(String toMail) throws MessagingException {

         boolean isUser = userRepository.existsByEmail(toMail);

         if(isUser){
             throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"잘못된 요청입니다. 이전 단계를 완료 후 요청해주세요");
         }

        boolean isValidMx = this.validateMx(toMail.split("@")[1]);
        if(isValidMx){
            String authCode = commonUtils.createAuthCode(5);

            MimeMessage message = createMail(toMail, authCode);
            javaMailSender.send(message);

            // 이메일 인증번호 5분 간 캐싱
            redisTemplate.opsForValue().set(toMail, authCode, 300, TimeUnit.SECONDS); // 5분 간 유효

        } else {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"유효한 도메인이 아닙니다.");
        }

    }

    // reference: https://velog.io/@danielyang-95/%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9C%A0%ED%9A%A8%EC%84%B1-%EA%B2%80%EC%A6%9D-by-MX-%EB%A0%88%EC%BD%94%EB%93%9C
    @Override
    public boolean validateMx(String domain) {
        try {
                Hashtable<String, String> env = new Hashtable<>();
                env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
                DirContext ictx = new InitialDirContext(env);
                Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
                Attribute attr = attrs.get("MX");

                if (attr == null) {
                    return false;
                }

                return true;
            } catch (NamingException e) {
                return false;
            }
    }
}
