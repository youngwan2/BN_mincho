package com.mincho.herb.domain.user.api;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.ValidationUtil;
import com.mincho.herb.domain.user.application.email.EmailService;
import com.mincho.herb.domain.user.dto.RequestVerification;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private  final ValidationUtil validationUtil;


    // 인증번호 발송
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam("email") String email) {
        try {
            emailService.sendVerificationCode(email);
        } catch (MessagingException ex) {
            throw new CustomHttpException(HttpErrorCode.INTERNAL_SERVER_ERROR,"메일 전송 중 서버 측에서 문제가 발생하였습니다.");
        }
        return ResponseEntity.ok("이메일 인증 코드가 발송되었습니다.");
    }

    // 인증번호 검증
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> emailVerification(@Valid @RequestBody RequestVerification requestVerification, BindingResult result) {

        if(result.hasErrors()){
             return new ErrorResponse().getResponse(400, validationUtil.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
         }

        boolean isVer = emailService.emailVerification(requestVerification);
        if(isVer){
            return new SuccessResponse<>().getResponse(200, "통과 되었습니다.", HttpSuccessType.OK);
        }

        return new ErrorResponse().getResponse(409, "인증 실패하였습니다.", HttpErrorType.CONFLICT);

    }
}


