package com.mincho.herb.domain.notice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeSearchConditionDTO {
    private String keyword;
    private String category;
    private Boolean pinned;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

}
