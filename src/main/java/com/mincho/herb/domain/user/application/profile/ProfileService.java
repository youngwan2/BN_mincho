package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.dto.RequestProfileDTO;

public interface ProfileService {
    void updateProfile(RequestProfileDTO requestProfileDTO, String email);
    Profile getUserProfile(String email);
    Profile insertProfile( Member member);
}
