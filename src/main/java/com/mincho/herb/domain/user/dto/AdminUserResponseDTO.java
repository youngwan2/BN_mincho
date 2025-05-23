package com.mincho.herb.domain.user.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminUserResponseDTO {
    private List<AdminUserListDTO> users;
    private Long totalCount;
}
