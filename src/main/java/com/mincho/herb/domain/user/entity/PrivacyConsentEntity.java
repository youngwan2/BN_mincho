package com.mincho.herb.domain.user.entity;

import com.mincho.herb.domain.user.domain.PrivacyConsent;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 개인정보 수집 및 마케팅 정보 동의에 대한 엔티티
 */
@Entity
@Table(name = "privacy_consent")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyConsentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "essential_info_consent", nullable = false)
    private Boolean essentialInfoConsent; // 필수 정보 수집 동의 여부

    @Column(name = "optional_info_consent", nullable = false)
    private Boolean optionalInfoConsent; // 선택 정보 수집 동의 여부

    @Column(name = "automatic_info_consent", nullable = false)
    private Boolean automaticInfoConsent; // 자동 수집 정보 동의 여부

    @Column(name = "marketing_consent", nullable = false)
    private Boolean marketingConsent; // 마케팅 정보 수집 동의 여부

    @CreationTimestamp
    @Column(name = "consent_date_time", nullable = false)
    private LocalDateTime consentDateTime; // 동의 일시

    @Column(name = "consent_ip", length = 50)
    private String consentIp; // 동의 IP

    /**
     * 도메인 객체로 변환
     */
    public PrivacyConsent toDomain() {
        return PrivacyConsent.builder()
                .id(this.id)
                .userId(this.user.getId())
                .essentialInfoConsent(this.essentialInfoConsent)
                .optionalInfoConsent(this.optionalInfoConsent)
                .automaticInfoConsent(this.automaticInfoConsent)
                .marketingConsent(this.marketingConsent)
                .consentDateTime(this.consentDateTime)
                .consentIp(this.consentIp)
                .build();
    }

    /**
     * 엔티티 객체 생성
     */
    public static PrivacyConsentEntity fromDomain(PrivacyConsent privacyConsent, UserEntity user) {
        return PrivacyConsentEntity.builder()
                .user(user)
                .essentialInfoConsent(privacyConsent.getEssentialInfoConsent())
                .optionalInfoConsent(privacyConsent.getOptionalInfoConsent())
                .automaticInfoConsent(privacyConsent.getAutomaticInfoConsent())
                .marketingConsent(privacyConsent.getMarketingConsent())
                .consentIp(privacyConsent.getConsentIp())
                .build();
    }

    /**
     * 동의 정보 업데이트
     */
    public void updateConsent(Boolean essentialInfoConsent, Boolean optionalInfoConsent,
                              Boolean automaticInfoConsent, Boolean marketingConsent,
                              String consentIp) {
        if (essentialInfoConsent != null) this.essentialInfoConsent = essentialInfoConsent;
        if (optionalInfoConsent != null) this.optionalInfoConsent = optionalInfoConsent;
        if (automaticInfoConsent != null) this.automaticInfoConsent = automaticInfoConsent;
        if (marketingConsent != null) this.marketingConsent = marketingConsent;
        if (consentIp != null) this.consentIp = consentIp;
    }
}
