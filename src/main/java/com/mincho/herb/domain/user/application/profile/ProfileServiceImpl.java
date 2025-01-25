package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.RequestProfileDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
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
    public void updateProfile(RequestProfileDTO requestProfileDTO, String email) {
        UserEntity userEntity = UserEntity.toEntity(userService.findUserByEmail(email)) ;
        Profile newProfile = ProfileEntity.toEntity(Profile.withChangeProfile(requestProfileDTO), userEntity).toModel();

        profileRepository.updateProfile(newProfile, userEntity);
    }

    // 프로필 조회
    @Override
    public Profile getUserProfile(String email) {
        User user = userService.findUserByEmail(email);
        if(user == null){
            throw  new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "회원이 아닙니다.");
        }
        return profileRepository.findProfileByUser(user);
    }

    // 프로필 생성
    @Override
    public Profile insertProfile(User user) {
        Profile profile = Profile.builder()
                .nickname(null)
                .introduction(null)
                .avatarUrl(null)
                .build();
        UserEntity userEntity = UserEntity.toEntity(user);

        ProfileEntity profileEntity = ProfileEntity.toEntity(profile, userEntity);
        return profileRepository.saveProfile(profileEntity);
    }
}
