package com.mincho.herb.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserStatusRequestDTO {
    private String email;
    private String status;
}
