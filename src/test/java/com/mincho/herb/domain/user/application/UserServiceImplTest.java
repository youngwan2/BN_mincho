package com.mincho.herb.domain.user.application;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;
import com.mincho.herb.domain.user.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserServiceImplTest {

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // Mockito annotations 초기화
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void register_ShouldSaveUser_WhenValidRequest() {
        // Given
        String encodedPassword = "encodedPassword123!";
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encodedPassword);

        RequestRegisterDTO requestRegisterDTO = new RequestRegisterDTO();
        requestRegisterDTO.setNickname("testUser");
        requestRegisterDTO.setEmail("test@example.com");
        requestRegisterDTO.setPassword("testPassword123!");

        // When
        userService.register(requestRegisterDTO);

        // Then
        // ArgumentCaptor로 save 메서드에 전달된 인자를 캡처
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        // 캡처한 User 객체의 내용을 확인
        User capturedUser = userCaptor.getValue();

        // 원하는 값으로 비교
        assertEquals(requestRegisterDTO.getNickname(), capturedUser.getNickname());
        assertEquals(requestRegisterDTO.getEmail(), capturedUser.getEmail());
        assertEquals(encodedPassword, capturedUser.getPassword());
    }

    @Test
    void dueCheck_whenEmailExists_returnsTrue() {
        // Given
        String email = "test@example.com";
        DuplicateCheckDTO duplicateCheckDTO = new DuplicateCheckDTO();
        duplicateCheckDTO.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true); // Mock 설정


        // When
        boolean result = userService.dueCheck(duplicateCheckDTO);

        // Then
        assertTrue(result); // 기대 결과가 true인지 확인
        verify(userRepository, times(1)).existsByEmail(email); // 메서드 호출 여부 확인
    }

    @Test
    void dueCheck_whenEmailDoesNotExist_returnsFalse() {
        // Given
        String email = "test@example.com";
        DuplicateCheckDTO duplicateCheckDTO = new DuplicateCheckDTO();
        duplicateCheckDTO.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(false); // Mock 설정

        // When
        boolean result = userService.dueCheck(duplicateCheckDTO);

        // Then
        assertFalse(result); // 기대 결과가 false인지 확인
        verify(userRepository, Mockito.times(1)).existsByEmail(email); // 메서드 호출 여부 확인
    }
}
