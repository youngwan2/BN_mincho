package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.email.EmailService;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.dto.EmailRequestDTO;
import com.mincho.herb.domain.user.dto.VerificationRequestDTO;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "이메일", description = "이메일 인증 관련 API")
@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final UserService userService;


    /** 인증번호 발송 */
    @Operation(summary = "인증번호 발송", description = "이메일로 인증번호 발송 API (type: register|reset)")
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(
            @Parameter(description = "인증번호를 발송할 이메일 정보") @Valid @RequestBody EmailRequestDTO emailRequestDTO,
            @Parameter(description = "인증 타입 (register: 회원가입, reset: 비밀번호 재설정)") @RequestParam("type") String type) {

        log.info("email:{}",emailRequestDTO);
        try {
            // reset: 비밀번호 재설정
            if(type.equals("reset")) {
                emailService.sendVerificationCodeForReset(emailRequestDTO.getEmail());
            }

            // register: 회원가입
            if(type.equals("register")) {
                emailService.sendVerificationCodeForSignUp(emailRequestDTO.getEmail());
            }


        } catch (MessagingException ex) {
            return new ErrorResponse().getResponse(500, "인증번호 발송에 실패하였습니다.", HttpErrorType.INTERNAL_SERVER_ERROR);
        }

        return new SuccessResponse<>().getResponse(200, "해당 이메일로 인증번호를 발송하였습니다. 확인 후 인증번호를 입력해주세요.", HttpSuccessType.OK);
    }

    /** 인증번호 검증 */
    @Operation(summary = "인증번호 검증", description = "이메일 인증번호 검증 API (type: register|reset)")
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> emailVerification(
            @Parameter(description = "이메일과 인증번호 정보") @Valid @RequestBody VerificationRequestDTO verificationRequestDTO,
            @Parameter(description = "인증 타입 (register: 회원가입, reset: 비밀번호 재설정)") @RequestParam("type") String type
            ) {

        boolean isVer = false;

        // 회원가입 시 인증번호 검증
        if(type.equals("register")){
            isVer = emailService.emailVerification(verificationRequestDTO);
        }

        // 비밀번호 재설정 시 인증번호 검증
        if(type.equals("reset")) {
            isVer = emailService.emailVerificationForReset(verificationRequestDTO);

            try {
                String newPassword = emailService.sendResetPassword(verificationRequestDTO.getEmail()); // 새 비밀번호를 유저에게
                userService.updatePassword(verificationRequestDTO.getEmail(), newPassword );

            } catch (MessagingException e) {
                log.error("메시징 처리 예외: {}", e.getMessage());
                throw new CustomHttpException(HttpErrorCode.INTERNAL_SERVER_ERROR, "서버 측 로그를 확인해주세요.");
            }
        }

        if(isVer){
            return new SuccessResponse<>().getResponse(200, "통과 되었습니다.", HttpSuccessType.OK);
        }

        return new ErrorResponse().getResponse(409, "인증 실패하였습니다.", HttpErrorType.CONFLICT);
    }
}
