package com.mincho.herb.domain.admin.dto.notice;


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
