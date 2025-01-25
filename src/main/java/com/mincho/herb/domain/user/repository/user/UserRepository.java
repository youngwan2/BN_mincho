package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.domain.User;

public interface UserRepository {
    User save(User user);
    void updatePasswordByEmail(String password, String email);
    void deleteByEmail(String email); // 회원탈퇴

    boolean existsByEmail(String email);

    User findByEmail(String email);

}
