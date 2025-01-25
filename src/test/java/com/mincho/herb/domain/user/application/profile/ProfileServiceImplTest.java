package com.mincho.herb.domain.user.application.profile;

import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.dto.RequestProfileDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.profile.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
    }

    @Test
    void updateProfile() {
        // Given
        String email = "test@example.com";
        RequestProfileDTO requestProfileDTO = new RequestProfileDTO();
        requestProfileDTO.setNickname("newNickname");
        requestProfileDTO.setIntroduction("newIntroduction");
        requestProfileDTO.setAvatarUrl("http://example.com/avatar.jpg");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);

        Profile profile = Profile.builder()
                .id(1L)
                .nickname("newNickname")
                .introduction("newIntroduction")
                .avatarUrl("http://example.com/avatar.jpg")
                .build();

        // Mock 동작 정의
        when(userService.findUserByEmail(email)).thenReturn(userEntity.toModel());

        // When
        profileService.updateProfile(requestProfileDTO, email);

        // Then
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).updateProfile(profileCaptor.capture(), eq(userEntity));

        Profile capturedProfile = profileCaptor.getValue();
        assertEquals("newNickname", capturedProfile.getNickname());
        assertEquals("newIntroduction", capturedProfile.getIntroduction());
        assertEquals("http://example.com/avatar.jpg", capturedProfile.getAvatarUrl());
    }
}
