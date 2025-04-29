package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.entity.MemberEntity;
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

        MemberEntity memberEntity = userRepository.findByEmail(email);

        if(memberEntity == null){
            throw new NullPointerException("존재하지 않는 이메일입니다.");
        }

        log.info("loadUserGyUsername User:{}", memberEntity.toModel());
        return new CustomUserDetails(memberEntity.toModel(), null);
    }
}
