package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.entity.MemberEntity;

public interface UserRepository {
    Member save(Member member);
    void updatePasswordByEmail(String password, String email); // 비밀번호 재설정
    void deleteByEmail(String email); // 회원탈퇴

    boolean existsByEmail(String email);
    boolean existsByEmailAndProviderIsNull(String email);

    MemberEntity findByEmail(String email);

    MemberEntity findByEmailOrNull(String email);

}
