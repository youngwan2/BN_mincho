package com.mincho.herb.domain.report.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSortDTO {
    private String sort; // 정렬 기준 (예: "createdAt", "status")
    private String Order; // 정렬 순서 ("asc" 또는 "desc")
}
