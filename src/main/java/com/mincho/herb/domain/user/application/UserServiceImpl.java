package com.mincho.herb.domain.user.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;
import com.mincho.herb.domain.user.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
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
                .build();

        userRepository.save(user);
    }

    // 유저 중복 체크
    @Override
    public boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO) {
        return userRepository.existsByEmail(duplicateCheckDTO.getEmail());
    }


}
