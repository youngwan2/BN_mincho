package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserGyUsername email:{}", email);

        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null){
            throw new NullPointerException("존재하지 않는 이메일입니다.");
        }

        log.info("loadUserGyUsername User:{}", userEntity.toModel());
        return new CustomUserDetails(userEntity.toModel(), null);
    }
}
