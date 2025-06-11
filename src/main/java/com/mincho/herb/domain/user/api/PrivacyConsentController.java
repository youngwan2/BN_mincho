package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.privacyConsent.PrivacyConsentService;
import com.mincho.herb.domain.user.dto.PrivacyConsentRequestDTO;
import com.mincho.herb.domain.user.dto.PrivacyConsentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 개인정보 수집 동의 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Privacy Consent", description = "개인정보 수집 동의 API")
public class PrivacyConsentController {

    private final PrivacyConsentService privacyConsentService;

    /**
     * 개인정보 수집 동의 저장
     */
    @PostMapping("/privacy-consent")
    @Operation(
            summary = "개인정보 수집 동의 저장",
            description = "사용자의 개인정보 수집 동의 정보를 저장합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<PrivacyConsentResponseDTO> savePrivacyConsent(
            @Valid @RequestBody PrivacyConsentRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String clientIp = getClientIp(request);

        PrivacyConsentResponseDTO responseDTO = privacyConsentService.saveConsent(requestDTO, email, clientIp);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * 개인정보 수집 동의 조회
     */
    @GetMapping("/privacy-consent")
    @Operation(
            summary = "개인정보 수집 동의 조회",
            description = "현재 사용자의 개인정보 수집 동의 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<PrivacyConsentResponseDTO> getPrivacyConsent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        PrivacyConsentResponseDTO responseDTO = privacyConsentService.getCurrentUserConsent(email);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 개인정보 수집 동의 업데이트
     */
    @PutMapping("/privacy-consent")
    @Operation(
            summary = "개인정보 수집 동의 업데이트",
            description = "사용자의 개인정보 수집 동의 정보를 업데이트합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<PrivacyConsentResponseDTO> updatePrivacyConsent(
            @Valid @RequestBody PrivacyConsentRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String clientIp = getClientIp(request);

        PrivacyConsentResponseDTO responseDTO = privacyConsentService.updateConsent(requestDTO, email, clientIp);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 마케팅 동의 여부 조회
     */
    @GetMapping("/marketing-consent")
    @Operation(
            summary = "마케팅 동의 여부 조회",
            description = "현재 사용자의 마케팅 정보 수집 동의 여부를 조회합니다.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Map<String, Boolean>> getMarketingConsent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean hasConsent = privacyConsentService.hasMarketingConsent(email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("marketingConsent", hasConsent);

        return ResponseEntity.ok(response);
    }

    /**
     * 클라이언트 IP 주소 가져오기
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
