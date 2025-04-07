package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.entity.MemberEntity;

public interface UserRepository {
    Member save(Member member);
    void updatePasswordByEmail(String password, String email);
    void deleteByEmail(String email); // 회원탈퇴

    boolean existsByEmail(String email);

    MemberEntity findByEmail(String email);

    MemberEntity findByEmail2(String email);

}
