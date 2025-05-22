package com.mincho.herb.domain.report.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 신고 생성 요청을 처리하기 위한 DTO 클래스입니다.
 * 사용자가 게시글 또는 댓글 등의 대상에 대해 신고할 때 사용됩니다.
 *
 * <p>유효성 검사 어노테이션이 포함되어 있어, 컨트롤러에서 @Valid와 함께 사용할 수 있습니다.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestDTO {

    /**
     * 신고 대상의 고유 ID입니다.
     * 예: 게시글 ID, 댓글 ID 등
     */
    private Long targetId;

    /**
     * 신고 대상의 타입입니다.
     * 예: "POST", "COMMENT"
     *
     * <p>비어 있을 수 없습니다.</p>
     */
    @NotEmpty(message = "신고 대상(게시글, 댓글 등)은 필수입니다.")
    private String targetType;

    /**
     * 신고 사유 요약입니다.
     * 예: "욕설", "도배", "음란물"
     *
     * <p>2자 이상 20자 이하로 작성해야 합니다.</p>
     */
    @Size(min = 2, max = 20, message = "신고 사유는 2자 이상 20자 이하로 작성해야 합니다.")
    private String reasonSummary;

    /**
     * 신고 사유에 대한 상세 설명입니다.
     * 사용자에게 더 구체적인 신고 내용을 입력받기 위한 필드입니다.
     *
     * <p>10자 이상 500자 이하로 작성해야 합니다.</p>
     */
    @Size(min = 10, max = 500, message = "신고 사유 상세는 10자 이상 500자 이하로 작성해야 합니다.")
    private String reason;
}
