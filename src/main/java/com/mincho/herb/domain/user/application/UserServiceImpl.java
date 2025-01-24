package com.mincho.herb.domain.user.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestLoginDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;
import com.mincho.herb.domain.user.repository.UserRepositoryImpl;
import com.mincho.herb.infra.auth.JwtAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthProvider jwtAuthProvider;
    private final UserRepositoryImpl userRepository;

    @Override
    public void register(RequestRegisterDTO registerDTO) {

        DuplicateCheckDTO duplicateCheckDTO = new DuplicateCheckDTO(registerDTO.getEmail());
        boolean hasUser = dueCheck(duplicateCheckDTO);
        if(hasUser){
            throw new CustomHttpException(HttpErrorCode.CONFLICT,"이미 존재하는 유저입니다.");
        }
        String encodePw = bCryptPasswordEncoder.encode(registerDTO.getPassword());

        User user = User.builder()
                .nickname(registerDTO.getNickname())
                .email(registerDTO.getEmail())
                .password(encodePw)
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    // 유저 중복 체크
    @Override
    public boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO) {
        return userRepository.existsByEmail(duplicateCheckDTO.getEmail());
    }

    // 로그인
    @Override
    public Map<String, String> login(RequestLoginDTO requestLoginDTO) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(requestLoginDTO.getEmail(), requestLoginDTO.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authToken);

            log.info("인증 성공: {}", authentication.getAuthorities().iterator().next().getAuthority());

            // 토큰 생성
            String accessToken = jwtAuthProvider.generateToken(authentication, 60 * 60 * 10* 1000L);
            String refreshToken = jwtAuthProvider.generateToken(authentication, 60 * 60 * 24 * 30 * 1000L);

            Map<String, String> map = new HashMap<>();
            map.put("access", accessToken);
            map.put("refresh", refreshToken);

            return map;
    }

    // 회원탈퇴
    @Override
    public void deleteUser(String email) {
        boolean hasUser =userRepository.existsByEmail(email);

        if(!hasUser) throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        userRepository.deleteByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
