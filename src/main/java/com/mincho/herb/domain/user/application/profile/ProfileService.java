package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
import com.mincho.herb.domain.user.dto.ProfileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    void updateProfile(ProfileRequestDTO profileRequestDTO, String email);
    ProfileResponseDTO getUserProfile(String email);
    Profile insertProfile( User user);
    void updateProfileImage(MultipartFile imageFile);


}
