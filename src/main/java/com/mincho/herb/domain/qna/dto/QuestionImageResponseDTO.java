package com.mincho.herb.domain.qna.dto;


import lombok.Data;

import java.util.List;

@Data
public class QuestionImageResponseDTO {
    private List<String> imageUrls;
}
