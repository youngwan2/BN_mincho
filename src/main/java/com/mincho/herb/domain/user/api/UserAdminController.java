package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.user.UserAdminService;
import com.mincho.herb.domain.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class UserAdminController {

    private final UserAdminService userAdminService;

    // 유저 목록 조회
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Pageable pageable
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

    // 유저 상태 변경
    @PatchMapping("/users/{uuid}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(
            @RequestBody @Valid UpdateUserStatusRequestDTO updateUserStatusRequestDTO
            ){
        userAdminService.updateUserStatus(updateUserStatusRequestDTO.getEmail(), updateUserStatusRequestDTO.getStatus());
        return ResponseEntity.ok().build();
    }

    // 유저 권한 변경
    @PatchMapping("/users/{uuid}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @RequestBody @Valid UpdateUserRoleRequestDTO updateUserRoleRequestDTO
    ){
        userAdminService.updateUserRole(updateUserRoleRequestDTO.getEmail(), updateUserRoleRequestDTO.getRole());
        return ResponseEntity.noContent().build();
    }
    // 유저 삭제(탈퇴)
    @DeleteMapping("/users/{uuid}/entire")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @RequestBody @Valid UpdateUserEntireRequestDTO updateUserEntireRequestDTO
            ){
        userAdminService.deleteUser(updateUserEntireRequestDTO.getEmail());
        return ResponseEntity.noContent().build();
    }
}
