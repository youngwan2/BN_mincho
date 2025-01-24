package com.mincho.herb.domain.user.repository;

import com.mincho.herb.domain.user.domain.User;

public interface UserRepository {
    void save(User user);
    boolean existsByEmail(String email);
    User findByEmail(String email);
    void deleteByEmail(String email); // 회원탈퇴
}
