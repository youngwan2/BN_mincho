package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
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

    // 프로필 수정
    @Override
    public void updateProfile(Profile profile, MemberEntity user) {
        profileJpaRepository.updateProfile(profile.getNickname(), profile.getIntroduction(), profile.getAvatarUrl(), user.getId());
    }

    // 사용자 정보로 프로필 조회
    @Override
    public ProfileEntity findProfileByUser(MemberEntity member) {
        Long userId = member.getId();

        log.info("profile-userId: {}",userId);

        ProfileEntity profileEntity = profileJpaRepository.findProfileByUser(userId);

        if(profileEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "프로파일 정보가 존재하지 않습니다.");
        }
        return profileEntity;
    }

    @Override
    public void deleteByMember(MemberEntity member) {
        profileJpaRepository.deleteByMember(member);
    }
}
