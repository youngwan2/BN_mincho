package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.repository.profile.ProfileRepository;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Override
    public  OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}",oAuth2User.getAttributes());

        // registrationId == provider ex. Google
        String provider = userRequest.getClientRegistration().getRegistrationId();

        CustomOAuth2User customOAuth2UserInfo = null;

        // 구글 로그인
        if(provider.equals("google")){

            customOAuth2UserInfo = new GoogleUserDetailsCustom(oAuth2User.getAttributes());
            log.info("구글 로그인:{}", customOAuth2UserInfo);

        }

        String providerId = customOAuth2UserInfo.getProviderId();
        String email = customOAuth2UserInfo.getEmail();
        String name = customOAuth2UserInfo.getName();

        log.info("providerId:{}, email:{}, name:{}", providerId, email, name); // 여기까지 문제 없음

        MemberEntity memberEntity = memberRepository.findByEmail2(email);
        
        // 유저 정보 저장
        if (memberEntity == null) {
            memberEntity = MemberEntity.builder()
                            .profile(ProfileEntity.toEntity(
                                    Profile.builder()
                                    .nickname(name)
                                    .build(),
                                    memberEntity
                            ))
                            .providerId(providerId)
                            .email(email)
                            .provider(customOAuth2UserInfo.getProvider())
                            .role("ROLE_USER")
                            .build();
           Member member = memberRepository.save(memberEntity.toModel());

           // 프로필 저장
            ProfileEntity profileEntity = new ProfileEntity();
            profileEntity.setMember(MemberEntity.toEntity(member));
            profileEntity.setNickname(name);

            profileRepository.saveProfile(profileEntity);
        }

        return new CustomUserDetails(memberEntity.toModel(), oAuth2User.getAttributes());
    }
}