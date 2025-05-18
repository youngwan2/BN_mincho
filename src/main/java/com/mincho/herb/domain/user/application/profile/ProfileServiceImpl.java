package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.repository.profile.ProfileRepository;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.infra.auth.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final S3Service s3Service;



    // 프로필 수정
    @Override
    @Transactional
    public void updateProfile(ProfileRequestDTO profileRequestDTO, String email) {
        MemberEntity memberEntity = userService.getUserByEmail(email) ;
        Profile newProfile = ProfileEntity.toEntity(Profile.withChangeProfile(profileRequestDTO), memberEntity).toModel();

        profileRepository.updateProfile(newProfile, memberEntity);
    }

    // 프로필 조회
    @Override
    public ProfileResponseDTO getUserProfile(String email) {
        MemberEntity member = userService.getUserByEmail(email);

        ProfileEntity profileEntity=  profileRepository.findProfileByUser(member);

        return ProfileResponseDTO.builder()
                .nickname(profileEntity.getNickname())
                .avatarUrl(profileEntity.getAvatarUrl())
                .introduction(profileEntity.getIntroduction())
                .isSocial(member.getProviderId() != null)
                .build();
    }

    // 프로필 이미지 업로드
    @Override
    @Transactional
    public void updateProfileImage(MultipartFile imageFile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"요청 권한이 없습니다. 로그인 후 다시시도 해주세요.");
        }

        MemberEntity member = userService.getUserByEmail(email);
        ProfileEntity profileEntity = profileRepository.findProfileByUser(member);

        String imageUrl = s3Service.upload(imageFile, "users/"+member.getId()+"/"); // s3 이미지 업로드
        profileEntity.setAvatarUrl(imageUrl);
        profileRepository.updateProfile(profileEntity.toModel(), member);

    }

    // 프로필 생성
    @Override
    @Transactional
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
