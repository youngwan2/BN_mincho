package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.domain.bookmark.repository.herbBookmark.HerbBookmarkRepository;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.repository.CommentRepository;
import com.mincho.herb.domain.like.repository.HerbLikeRepository;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postLike.PostLikeRepository;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.LoginRequestDTO;
import com.mincho.herb.domain.user.dto.RegisterRequestDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.profile.ProfileRepository;
import com.mincho.herb.domain.user.repository.refreshToken.RefreshTokenRepository;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.infra.auth.JwtAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthProvider jwtAuthProvider;
    private final UserRepository userRepository;
    private final HerbBookmarkRepository herbBookmarkRepository;
    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final HerbLikeRepository herbLikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PostRepository postRepository;



    
    // 회원가입
    @Override
    @Transactional
    public User register(RegisterRequestDTO registerDTO) {
        DuplicateCheckDTO duplicateCheckDTO = new DuplicateCheckDTO(registerDTO.getEmail());
        boolean hasUser = dueCheck(duplicateCheckDTO);
        if(hasUser){
            throw new CustomHttpException(HttpErrorCode.CONFLICT,"이미 존재하는 유저입니다.");
        }
        String encodePw = bCryptPasswordEncoder.encode(registerDTO.getPassword());

        User user = User.builder()
                .email(registerDTO.getEmail())
                .password(encodePw)
                .role("ROLE_USER")
                .build();

        return userRepository.save(user);
    }

    // 유저 중복 체크
    @Override
    public boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO) {
        return userRepository.existsByEmail(duplicateCheckDTO.getEmail());
    }

    // 비밀번호 일치 유무 체크
    @Override
    public boolean checkPassword(String email, String rawPassword) {
        UserEntity User  = userRepository.findByEmail(email);

        if(User.getProviderId() == null) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "소셜 로그인 유저는 이용할 수 없습니다.");
        }

        String password = User.getPassword();
        return bCryptPasswordEncoder.matches(rawPassword, password );
    }

    // 로그인
    @Override
    public Map<String, String> login(LoginRequestDTO loginRequestDTO) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authToken);


            // 토큰 생성
            String accessToken = jwtAuthProvider.generateToken(authentication, 60 * 60 * 10* 1000L);
            String refreshToken = jwtAuthProvider.generateToken(authentication, 60 * 60 * 24 * 30 * 1000L);
            UserEntity userEntity = userRepository.findByEmail(loginRequestDTO.getEmail());
            if(userEntity == null){
                throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "유저 정보를 찾을 수 없습니다.");
            }

            refreshTokenRepository.saveRefreshToken(refreshToken, userEntity);

            Map<String, String> map = new HashMap<>();
            map.put("access", accessToken);
            map.put("refresh", refreshToken);

            log.info("인증 성공: {}", authentication.getAuthorities().iterator().next().getAuthority());
            return map;
    }

    // 회원탈퇴
    @Override
    @Transactional
    public void deleteUser(String email) {
        boolean hasUser =userRepository.existsByEmail(email);
        if(!hasUser) throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        UserEntity userEntity = userRepository.findByEmail(email);

        // 연관 테이블 정리
        herbBookmarkRepository.deleteByUser(userEntity);
        profileRepository.deleteByUser(userEntity);
        refreshTokenRepository.deleteByUser(userEntity);
        herbLikeRepository.deleteByUser(userEntity);
        postLikeRepository.deleteByUser(userEntity);

        List<PostEntity> postEntities = postRepository.findAllByUser(userEntity);
        List<CommentEntity> commentEntities = commentRepository.findAllByUser(userEntity);

        // 댓글과 연관관계 끊기
        for(CommentEntity commentEntity : commentEntities){
            commentEntity.setUser(null);
        }

        // 게시글과 연관관계 끊기
        for (PostEntity postEntity : postEntities) {
            postEntity.setUser(null);
        }

        // 제일 마지막 유저 탈퇴
        userRepository.deleteByEmail(email);
    }

    // 비밀번호 수정
    @Override
    @Transactional
    public void updatePassword(String email, String password) {
        String encodePw = bCryptPasswordEncoder.encode(password);
        userRepository.updatePasswordByEmail(encodePw, email);
    }

    // 유저 조회
    @Override
    public UserEntity getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        }
        return userEntity;
    }

    @Override
    public UserEntity getUserByEmailOrNull(String email) {
        return userRepository.findByEmailOrNull(email);
    }

    // 모든 브라우저 로그아웃
    @Override
    @Transactional
    public void logoutAll(Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "유저 정보를 찾을 수 없습니다.");
        }

        refreshTokenRepository.removeRefreshTokenAllByUserId(userEntity.getId());
    }

    
    // 로그인 유무 체크
    @Override
    public boolean isLogin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email.contains("@");
    }
}
