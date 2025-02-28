package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.repository.profile.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;



    // 프로필 수정
    @Override
    @Transactional
    public void updateProfile(ProfileRequestDTO profileRequestDTO, String email) {
        MemberEntity memberEntity = MemberEntity.toEntity(userService.findUserByEmail(email)) ;
        Profile newProfile = ProfileEntity.toEntity(Profile.withChangeProfile(profileRequestDTO), memberEntity).toModel();

        profileRepository.updateProfile(newProfile, memberEntity);
    }

    // 프로필 조회
    @Override
    public ProfileResponseDTO getUserProfile(String email) {
        Member member = userService.findUserByEmail(email);
        if(member == null){
            throw  new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "회원이 아닙니다.");
        }

        ProfileEntity profileEntity=  profileRepository.findProfileByUser(member);

        return ProfileResponseDTO.builder()
                .nickname(profileEntity.getNickname())
                .avatarUrl(profileEntity.getAvatarUrl())
                .introduction(profileEntity.getIntroduction())
                .build();
    }

    // 프로필 생성
    @Override
    public Profile insertProfile(Member member) {
        Profile profile = Profile.builder()
                .nickname(null)
                .introduction(null)
                .avatarUrl(null)
                .build();
        MemberEntity memberEntity = MemberEntity.toEntity(member);

        ProfileEntity profileEntity = ProfileEntity.toEntity(profile, memberEntity);
        return profileRepository.saveProfile(profileEntity);
    }
}
