package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.domain.user.dto.AdminUserResponseDTO;
import com.mincho.herb.domain.user.dto.SortInfoDTO;
import com.mincho.herb.domain.user.dto.UserListSearchCondition;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.entity.UserStatusEnum;
import com.mincho.herb.domain.user.repository.user.UserAdminRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자용 사용자 관리 서비스 구현체
 * <p>
 * 사용자 목록 조회, 상태 변경, 권한 변경, 탈퇴 처리 등의 기능을 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {

    private final UserAdminRepository userAdminRepository;
    private final UserService userService;

    /**
     * 관리자 사용자 목록 조회
     *
     * @param condition   검색 조건 (이메일, 닉네임, 가입일 등)
     * @param sortInfoDTO 정렬 조건
     * @param pageable    페이지네이션 정보
     * @return AdminUserResponseDTO 사용자 목록과 페이징 정보
     */
    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDTO getUserList(UserListSearchCondition condition, SortInfoDTO sortInfoDTO, Pageable pageable) {
        return userAdminRepository.getUserInfo(condition, sortInfoDTO, pageable);
    }

    /**
     * 사용자의 상태를 업데이트
     *
     * @param email  사용자 이메일
     * @param status 새로운 상태 값 (예: ACTIVE, INACTIVE, DELETED)
     */
    @Override
    @Transactional
    public void updateUserStatus(String email, String status) {
        UserEntity userEntity = userService.getUserByEmail(email);
        userEntity.setStatus(UserStatusEnum.valueOf(status));
        userAdminRepository.updateUserStatus(userEntity);
    }

    /**
     * 사용자를 '탈퇴' 상태로 처리
     *
     * @param email 사용자 이메일
     */
    @Override
    @Transactional
    public void deleteUser(String email) {
        UserEntity userEntity = userService.getUserByEmail(email);
        userEntity.setStatus(UserStatusEnum.DELETED);
        userAdminRepository.deleteUser(userEntity);
    }

    /**
     * 사용자의 역할(Role)을 업데이트
     *
     * @param email 사용자 이메일
     * @param role  변경할 권한 (user, admin)
     * @throws CustomHttpException 권한값이 유효하지 않을 경우 400 예외 발생
     */
    @Override
    @Transactional
    public void updateUserRole(String email, String role) {
        if (role.equals("user") || role.equals("admin")) {
            String updatedRole = role.equals("user") ? "ROLE_USER" : "ROLE_ADMIN";
            UserEntity userEntity = userService.getUserByEmail(email);
            userEntity.setRole(updatedRole);
            userAdminRepository.updateUserRole(userEntity);
        } else {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 권한변경 요청입니다.");
        }
    }
}
