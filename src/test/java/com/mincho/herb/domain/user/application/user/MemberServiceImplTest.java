package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.LoginRequestDTO;
import com.mincho.herb.domain.user.dto.RegisterRequestDTO;
import com.mincho.herb.domain.user.repository.user.UserRepositoryImpl;
import com.mincho.herb.infra.auth.JwtAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MemberServiceImplTest {

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserRepositoryImpl userRepository;


    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtAuthProvider jwtAuthProvider;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // Mockito annotations 초기화
        MockitoAnnotations.openMocks(this);
    }


    /* 회원가입 */
    @Test
    void register_ShouldSaveUser_WhenValidRequest() {
        // Given
        String encodedPassword = "encodedPassword123!";
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encodedPassword);

        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("test@example.com");
        registerRequestDTO.setPassword("testPassword123!");

        // When
        userService.register(registerRequestDTO);

        // Then
        // ArgumentCaptor로 save 메서드에 전달된 인자를 캡처
        ArgumentCaptor<Member> userCaptor = ArgumentCaptor.forClass(Member.class);
        verify(userRepository).save(userCaptor.capture());

        // 캡처한 User 객체의 내용을 확인
        Member capturedMember = userCaptor.getValue();

        // 원하는 값으로 비교
        assertEquals(registerRequestDTO.getEmail(), capturedMember.getEmail());
        assertEquals(encodedPassword, capturedMember.getPassword());
    }

    /* 유저 중복 확인*/
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

    /* 로그인 */
    @Test
    void login_ShouldCreateToken_WhenValidRequest() {
        // Given
        String email = "test@example.com";
        String password = "password";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        String mockAccessToken = "mockAccessToken";
        String mockRefreshToken = "mockRefreshToken";

        // 목 객체 설정
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        when(jwtAuthProvider.generateToken(mockAuthentication, 60 * 60 * 10 * 1000L))
                .thenReturn(mockAccessToken);
        when(jwtAuthProvider.generateToken(mockAuthentication, 60 * 60 * 24 * 30 * 1000L))
                .thenReturn(mockRefreshToken);

        // When
        Map<String, String> result = userService.login(loginRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(mockAccessToken, result.get("access"));
        assertEquals(mockRefreshToken, result.get("refresh"));

        // Verify
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtAuthProvider).generateToken(mockAuthentication, 60 * 60 * 10 * 1000L);
        verify(jwtAuthProvider).generateToken(mockAuthentication, 60 * 60 * 24 * 30 * 1000L);
    }

    @Test
    void login_WhenAuthFailure_ThrowBadCredentialException(){
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("자격증명 실패"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> userService.login(loginRequestDTO));

        // Verify
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtAuthProvider, never()).generateToken(any(), anyLong());
    }

    @Test
    void deleteUser_UserExists() {
        // given
        String email ="test12@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when
        userService.deleteUser(email);

        // then
        verify(userRepository, times(1)).deleteByEmail(email);
    }

    @Test
    void deleteUser_UserNotFound() {
        // given
        String email ="test12@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // when & then
        CustomHttpException exception = assertThrows(CustomHttpException.class, () -> {
            userService.deleteUser(email);
        });

        assertEquals(HttpErrorCode.RESOURCE_NOT_FOUND, exception.getHttpErrorCode());
        assertEquals("유저 정보를 찾을 수 없습니다.", exception.getMessage());
    }
}
