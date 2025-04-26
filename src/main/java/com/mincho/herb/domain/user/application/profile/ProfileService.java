package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;

public interface ProfileService {
    void updateProfile(ProfileRequestDTO profileRequestDTO, String email);
    ProfileResponseDTO getUserProfile(String email);
    Profile insertProfile( Member member);
    void updateProfileImage(String imgUrl);


}
