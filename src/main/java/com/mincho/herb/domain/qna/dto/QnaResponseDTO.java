package com.mincho.herb.domain.qna.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaResponseDTO {
    List<QnaSummaryDTO> qnas;
    Long totalCount;
}
