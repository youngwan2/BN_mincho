package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{
    private final UserJpaRepository userJpaRepository; // 의도: jpa 를 외부에서 주입하여 JPA 의존성을 약화


    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.toEntity(user)).toModel();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }



    @Override
    @Transactional
    public void deleteByEmail(String email) { // 회원탈퇴
        userJpaRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public void updatePasswordByEmail(String password, String email) {
        userJpaRepository.updatePassword(password, email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

}
