package com.mincho.herb.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserListSearchCondition {
    private String keyword;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
