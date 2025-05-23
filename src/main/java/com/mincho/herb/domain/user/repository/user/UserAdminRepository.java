package com.mincho.herb.domain.user.repository.user;

import com.mincho.herb.domain.user.dto.AdminUserResponseDTO;
import com.mincho.herb.domain.user.dto.SortInfoDTO;
import com.mincho.herb.domain.user.dto.UserListSearchCondition;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;

public interface UserAdminRepository {

    void save(UserEntity userEntity);

    // nickname=민초&status=ACTIVE&page=0&size=20&sort=createdAt,desc
    // 유저 정보 조회
    AdminUserResponseDTO getUserInfo(UserListSearchCondition condition, SortInfoDTO sortInfoDTO, Pageable pageable);

    // 유저 상태 변경
    void updateUserStatus(UserEntity userEntity);

    // 유저 권한 변경
    void updateUserRole(UserEntity userEntity);
    // 유저 삭제
    void deleteUser(UserEntity userEntity);

}
