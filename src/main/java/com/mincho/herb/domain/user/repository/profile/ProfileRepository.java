package com.mincho.herb.domain.user.repository.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.entity.ProfileEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;

public interface ProfileRepository {
    Profile saveProfile(ProfileEntity profileEntity);
    void updateProfile(Profile profile, MemberEntity user);
    ProfileEntity findProfileByUser(Member member);

}
