package com.mincho.herb.domain.report.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandleReportRequestDTO {
    private String status;
    private String handleTitle;
    private String handleMemo;
    private String handleAt;
}
