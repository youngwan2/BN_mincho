package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileJpaRepository profileJpaRepository; // 의도: jpa 를 외부에서 주입하여 JPA 의존성을 약화

    @Override
    public Profile saveProfile(ProfileEntity profileEntity) {
        ProfileEntity profile =profileJpaRepository.save(profileEntity);
        log.info("조회 프로필: {}", profile);

        return null;
    }

    @Override
    public void updateProfile(Profile profile, UserEntity user) {
        profileJpaRepository.updateProfile(profile.getNickname(), profile.getIntroduction(), profile.getAvatarUrl(), user.getId());
    }

    @Override
    public Profile findProfileByUser(User user) {
        Long userId = UserEntity.toEntity(user).getId();
        log.info("userId: {}",userId);
        ProfileEntity profileEntity = profileJpaRepository.findProfileByUser(userId);
        if(profileEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "프로파일 정보가 존재하지 않습니다.");
        }
        return profileEntity.toModel() ;
    }
}
