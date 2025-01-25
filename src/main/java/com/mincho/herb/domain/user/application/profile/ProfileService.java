package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.RequestProfileDTO;

public interface ProfileService {
    void updateProfile(RequestProfileDTO requestProfileDTO, String email);
    Profile getUserProfile(String email);
    Profile insertProfile( User user);
}
