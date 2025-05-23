package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.domain.user.dto.AdminUserResponseDTO;
import com.mincho.herb.domain.user.dto.SortInfoDTO;
import com.mincho.herb.domain.user.dto.UserListSearchCondition;
import org.springframework.data.domain.Pageable;

public interface UserAdminService {


    // 사용자 목록 조회
    AdminUserResponseDTO getUserList(UserListSearchCondition condition, SortInfoDTO sortInfoDTO, Pageable pageable);
    // 사용자 상태 변경
    void updateUserStatus(String email, String status);
    // 사용자 삭제
    void deleteUser(String email);
    // 사용자 권한 변경
    void updateUserRole(String userId, String role);


}
