package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.user.UserAdminService;
import com.mincho.herb.domain.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "유저 관리", description = "관리자 유저 관리 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class UserAdminController {

    private final UserAdminService userAdminService;

    /** 유저 목록 조회 */
    @Operation(summary = "유저 목록 조회", description = "관리자 유저 목록 조회 API")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserList(
            @Parameter(description = "검색어(이메일, 닉네임)") @RequestParam(required = false) String keyword,
            @Parameter(description = "계정 상태 필터(ACTIVE, INACTIVE, SUSPENDED, WITHDRAWN)") @RequestParam(required = false) String status,
            @Parameter(description = "정렬 기준(email, nickname, status, createdAt)") @RequestParam(required = false) String sort,
            @Parameter(description = "정렬 방향(asc, desc)") @RequestParam(required = false) String order,
            @Parameter(description = "검색 시작일 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "검색 종료일 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "페이징 정보") Pageable pageable
            ){


        SortInfoDTO sortInfoDTO = SortInfoDTO.builder()
                .sort(sort)
                .order(order)
                .build();

        UserListSearchCondition condition = UserListSearchCondition.builder()
                .keyword(keyword)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return ResponseEntity.ok(userAdminService.getUserList(condition, sortInfoDTO, pageable));
    }

    /** 유저 상태 변경 */
    @Operation(summary = "유저 상태 변경", description = "관리자 유저 상태 변경 API")
    @PatchMapping("/users/{uuid}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(
            @Parameter(description = "유저 상태 변경 요청 정보") @RequestBody @Valid UpdateUserStatusRequestDTO updateUserStatusRequestDTO
            ){
        userAdminService.updateUserStatus(updateUserStatusRequestDTO.getEmail(), updateUserStatusRequestDTO.getStatus());
        return ResponseEntity.ok().build();
    }


    /** 유저 권한 변경 */
    @Operation(summary = "유저 권한 변경", description = "관리자 유저 권한 변경 API")
    @PatchMapping("/users/{uuid}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @Parameter(description = "유저 권한 변경 요청 정보") @RequestBody @Valid UpdateUserRoleRequestDTO updateUserRoleRequestDTO
    ){
        userAdminService.updateUserRole(updateUserRoleRequestDTO.getEmail(), updateUserRoleRequestDTO.getRole());
        return ResponseEntity.noContent().build();
    }

    /** 유저 삭제(탈퇴) */
    @Operation(summary = "유저 삭제(탈퇴)", description = "관리자 유저 삭제(탈퇴) API")
    @DeleteMapping("/users/{uuid}/entire")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "유저 삭제 요청 정보") @RequestBody @Valid UpdateUserEntireRequestDTO updateUserEntireRequestDTO
            ){
        userAdminService.deleteUser(updateUserEntireRequestDTO.getEmail());
        return ResponseEntity.noContent().build();
    }
}
