package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.UserStatisticsDTO;
import com.mincho.herb.domain.user.entity.UserEntity;

public interface UserRepository {
    User save(User user);
    void updatePasswordByEmail(String password, String email); // 비밀번호 재설정
    void deleteByEmail(String email); // 회원탈퇴

    boolean existsByEmail(String email);
    boolean existsByEmailAndProviderIsNull(String email);

    UserEntity findByEmail(String email);

    UserEntity findByEmailOrNull(String email);


    UserStatisticsDTO findUserStatics();

}
