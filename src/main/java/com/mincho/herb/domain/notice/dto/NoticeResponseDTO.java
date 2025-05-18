package com.mincho.herb.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDTO {
    private List<NoticeDTO> notices;
    private Long totalCount;


}
