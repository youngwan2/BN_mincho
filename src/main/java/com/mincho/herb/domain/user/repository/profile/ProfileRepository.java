package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.ProfileEntity;

public interface ProfileRepository {
    Profile saveProfile(ProfileEntity profileEntity);
    void updateProfile(Profile profile, MemberEntity user);
    ProfileEntity findProfileByUser(MemberEntity member);
    void deleteByMember(MemberEntity member);

}
