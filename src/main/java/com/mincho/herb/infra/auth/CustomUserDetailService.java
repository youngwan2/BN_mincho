package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.repository.UserRepositoryImpl;
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
        log.info("loadUserGyUsername:{}", email);

        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new NullPointerException("존재하지 않는 이메일입니다.");
        }
        log.info("loadUserGyUsername User:{}", user);

        return new CustomUserDetails(user);
    }
}
