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

/**
 * {@code EmailServiceImpl} 클래스는 회원가입 및 비밀번호 재설정을 위한 이메일 인증 기능을 담당합니다.
 * <p>
 * 주요 기능은 다음과 같습니다:
 * <ul>
 *     <li>이메일 인증 코드 발송</li>
 *     <li>인증 코드 유효성 검증</li>
 *     <li>비밀번호 재설정용 임시 비밀번호 발송</li>
 *     <li>도메인의 MX 레코드 검증</li>
 * </ul>
 *
 * 이메일 인증 번호는 Redis에 일정 시간(기본 5분) 동안 저장되며, 인증이 성공하면 자동으로 제거됩니다.
 *
 * @author YoungWan Kim
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final CommonUtils commonUtils;
    private final String senderEmail;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * EmailServiceImpl의 생성자입니다.
     *
     * @param userRepository 사용자 정보를 조회하기 위한 레포지토리
     * @param redisTemplate  Redis 캐시 처리용 템플릿
     * @param javaMailSender 이메일 발송을 위한 JavaMailSender
     * @param commonUtils    공통 유틸리티 클래스
     * @param senderEmail    발신자 이메일 주소 (환경변수에서 주입)
     */
    public EmailServiceImpl(
            UserRepository userRepository,
            RedisTemplate<String, Object> redisTemplate,
            JavaMailSender javaMailSender,
            CommonUtils commonUtils,
            @Value("${spring.main.sender.email}") String senderEmail) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.javaMailSender = javaMailSender;
        this.commonUtils = commonUtils;
        this.senderEmail = senderEmail;
    }

    /**
     * 회원가입 시 이메일로 발송된 인증 코드가 일치하는지 검증합니다.
     *
     * @param verificationRequestDTO 사용자 이메일 및 입력한 인증 코드
     * @return 인증 성공 여부
     */
    @Override
    public boolean emailVerification(VerificationRequestDTO verificationRequestDTO) {
        String value = (String) redisTemplate.opsForValue().get(verificationRequestDTO.getEmail());
        log.info("input code: {}, auth code: {}", verificationRequestDTO.getCode(), value);
        boolean isVer = verificationRequestDTO.getCode().equals(value);

        if (isVer) {
            redisTemplate.delete(verificationRequestDTO.getEmail());
        }
        return isVer;
    }

    /**
     * 비밀번호 재설정 시 이메일로 발송된 인증 코드가 일치하는지 검증합니다.
     *
     * @param verificationRequestDTO 사용자 이메일 및 입력한 인증 코드
     * @return 인증 성공 시 true 반환
     * @throws CustomHttpException 인증 실패 시 예외 발생
     */
    @Override
    public boolean emailVerificationForReset(VerificationRequestDTO verificationRequestDTO) {
        String value = (String) redisTemplate.opsForValue().get(verificationRequestDTO.getEmail());
        log.info("input code: {}, auth code: {}", verificationRequestDTO.getCode(), value);

        boolean isVer = verificationRequestDTO.getCode().equals(value);

        if (isVer) {
            redisTemplate.delete(verificationRequestDTO.getEmail());
            return true;
        } else {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "인증번호가 일치하지 않습니다.");
        }
    }

    /**
     * 회원가입 시 이메일 인증 코드를 생성하여 메일로 전송합니다.
     *
     * @param toMail 수신자 이메일
     * @throws MessagingException 이메일 발송 실패 시
     * @throws CustomHttpException 이미 존재하는 사용자거나 도메인이 유효하지 않을 경우
     */
    @Override
    public void sendVerificationCodeForSignUp(String toMail) throws MessagingException {
        boolean isUser = userRepository.existsByEmail(toMail);

        if (isUser) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 요청입니다. 이전 단계를 완료 후 요청해주세요");
        }

        boolean isValidMx = this.validateMx(toMail.split("@")[1]);

        if (isValidMx) {
            String authCode = commonUtils.createAuthCode(5);
            MimeMessage message = createMail(toMail, authCode);
            javaMailSender.send(message);

            redisTemplate.opsForValue().set(toMail, authCode, 300, TimeUnit.SECONDS);
        } else {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효한 도메인이 아닙니다.");
        }
    }

    /**
     * 비밀번호 재설정 시 인증 코드를 이메일로 발송합니다.
     *
     * @param toMail 수신자 이메일
     * @throws MessagingException 이메일 발송 실패 시
     * @throws CustomHttpException 유효하지 않은 도메인이거나 소셜 로그인 계정일 경우
     */
    @Override
    public void sendVerificationCodeForReset(String toMail) throws MessagingException {
        boolean isValidMx = this.validateMx(toMail.split("@")[1]);

        if (isValidMx) {
            boolean isLocal = userRepository.existsByEmailAndProviderIsNull(toMail);

            if (!isLocal) {
                throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "소셜 로그인 계정은 비밀번호 재설정을 이용할 수 없습니다.");
            }

            String authCode = commonUtils.createAuthCode(5);
            MimeMessage message = createMail(toMail, authCode);
            javaMailSender.send(message);

            redisTemplate.opsForValue().set(toMail, authCode, 300, TimeUnit.SECONDS);
        } else {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효한 도메인이 아닙니다.");
        }
    }

    /**
     * 임시 비밀번호를 생성하여 이메일로 발송합니다.
     *
     * @param toMail 수신자 이메일
     * @return 생성된 임시 비밀번호 문자열
     * @throws MessagingException 이메일 발송 실패 시
     */
    @Override
    public String sendResetPassword(String toMail) throws MessagingException {
        String authCode = commonUtils.createAuthCode(12);
        MimeMessage message = createMailForResetPassword(toMail, authCode);
        javaMailSender.send(message);
        return authCode;
    }

    /**
     * 이메일 도메인의 MX 레코드를 검사하여 유효한 메일 도메인인지 확인합니다.
     *
     * @param domain 이메일 도메인 (예: gmail.com)
     * @return 유효하면 true, 아니면 false
     */
    @Override
    public boolean validateMx(String domain) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            return attr != null;
        } catch (NamingException e) {
            return false;
        }
    }

    /**
     * 이메일 인증용 HTML 포맷의 MimeMessage를 생성합니다.
     *
     * @param email    수신자 이메일
     * @param authCode 인증 코드
     * @return 생성된 MimeMessage
     * @throws MessagingException 메시지 생성 실패 시
     */
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

    /**
     * 비밀번호 재설정용 HTML 포맷의 MimeMessage를 생성합니다.
     *
     * @param email    수신자 이메일
     * @param authCode 임시 비밀번호
     * @return 생성된 MimeMessage
     * @throws MessagingException 메시지 생성 실패 시
     */
    private MimeMessage createMailForResetPassword(String email, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("재설정용 임시 비밀번호");

        String body = "<html lang='ko'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>재설정용 임시 비밀번호</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; text-align: center;'>" +
                "<div style='width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); padding: 30px; text-align: center;'>" +
                "<h3 style='font-size: 24px; color: #333333; margin-bottom: 20px; font-weight: bold;'>Mincho 사이트 인증 번호</h3>" +
                "<p style='font-size: 18px; color: #555555;'>재설정된 비밀번호는 아래와 같습니다..</p>" +
                "<div style='font-size: 48px; color: #4CAF50; font-weight: bold; background-color: #f1f8e9; padding: 20px; border-radius: 10px; display: inline-block; margin: 20px 0;'>" +
                "<h1 style='margin: 0;'>" + authCode + "</h1>" +
                "</div>" +
                "<p style='font-size: 16px; color: #777777; margin-top: 30px;'>이메일 인증에 감사드립니다. 로그인 후 꼭 안전한 비밀번호로 재설정해주세요.</p>" +
                "<p style='font-size: 18px; color: #666666; margin-top: 10px;'>감사합니다.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        message.setText(body, "UTF-8", "html");
        return message;
    }
}
