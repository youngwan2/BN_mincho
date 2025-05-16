package com.mincho.herb.domain.report.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestDTO {

    private Long targetId;

    @NotEmpty(message = "신고 대상(게시글, 댓글 등)은 필수입니다.")
    private String targetType;

    @Size(min=2, max=20, message = "신고 사유는 2자 이상 20자 이하로 작성해야 합니다.")
    private String reasonSummary;
    @Size(min=10, max=500, message = "신고 사유는 10자 이상 500자 이하로 작성해야 합니다.")
    private String reason;
}
