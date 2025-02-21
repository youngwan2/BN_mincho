package com.mincho.herb.domain.user.api;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.user.application.email.EmailService;
import com.mincho.herb.domain.user.dto.EmailRequestDTO;
import com.mincho.herb.domain.user.dto.VerificationRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
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
    private  final CommonUtils commonUtils;


    // 인증번호 발송
    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@Valid @RequestBody EmailRequestDTO emailRequestDTO, BindingResult result) {

        log.info("email:{}",emailRequestDTO);
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        try {
            emailService.sendVerificationCode(emailRequestDTO.getEmail());
        } catch (MessagingException ex) {
            return new ErrorResponse().getResponse(500, "인증번호 발송에 실패하였습니다.", HttpErrorType.INTERNAL_SERVER_ERROR);
        }
        return new SuccessResponse<>().getResponse(200, "통과 되었습니다.", HttpSuccessType.OK);
    }

    // 인증번호 검증
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> emailVerification(@Valid @RequestBody VerificationRequestDTO verificationRequestDTO, BindingResult result) {

        if(result.hasErrors()){
             return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
         }

        boolean isVer = emailService.emailVerification(verificationRequestDTO);
        if(isVer){
            return new SuccessResponse<>().getResponse(200, "통과 되었습니다.", HttpSuccessType.OK);
        }

        return new ErrorResponse().getResponse(409, "인증 실패하였습니다.", HttpErrorType.CONFLICT);

    }
}


