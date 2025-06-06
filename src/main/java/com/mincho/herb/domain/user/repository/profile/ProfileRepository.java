package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.ProfileSummaryDTO;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.entity.UserEntity;

public interface ProfileRepository {
    ProfileEntity saveProfile(ProfileEntity profileEntity);
    void updateProfile(Profile profile, UserEntity user);
    ProfileEntity findProfileByUser(UserEntity User);
    void deleteByUser(UserEntity User);

    ProfileSummaryDTO findProfileByUserId(Long userId);

}
