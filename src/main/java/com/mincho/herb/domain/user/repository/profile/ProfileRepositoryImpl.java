package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.ProfileSummaryDTO;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.entity.QProfileEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileJpaRepository profileJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProfileEntity saveProfile(ProfileEntity profileEntity) {
        return profileJpaRepository.save(profileEntity);
    }

    // 프로필 수정
    @Override
    public void updateProfile(Profile profile, UserEntity user) {
        profileJpaRepository.updateProfile(profile.getNickname(), profile.getIntroduction(), profile.getAvatarUrl(), user.getId());
    }


    /** 프로필 조회
     *
     * @param member 사용자 엔티티
     * @return 프로필 엔티티
     */
    @Override
    public ProfileEntity findProfileByUser(UserEntity member) {
        Long userId = member.getId();

        log.info("profile-userId: {}",userId);

        ProfileEntity profileEntity = profileJpaRepository.findProfileByUser(userId);

        if(profileEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "프로파일 정보가 존재하지 않습니다.");
        }
        return profileEntity;
    }


    /**
     * 프로필 요약 정보 조회
     *
     * @param userId 사용자 ID
     * */
    @Override
    public ProfileSummaryDTO findProfileByUserId(Long userId) {
        QProfileEntity profile = QProfileEntity.profileEntity;
        return jpaQueryFactory
                .select(Projections.constructor(ProfileSummaryDTO.class,
                        profile.nickname,
                        profile.introduction,
                        profile.avatarUrl
                        ))
                .from(profile)
                .where(profile.user.id.eq(userId))
                .fetchOne();
    }

    /** * 프로필 삭제
     *
     * @param user 사용자 엔티티
     */
    @Override
    public void deleteByUser(UserEntity user) {
        profileJpaRepository.deleteByUser(user);
    }

}
