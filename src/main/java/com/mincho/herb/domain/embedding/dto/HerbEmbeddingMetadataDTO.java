package com.mincho.herb.domain.embedding.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HerbEmbeddingMetadataDTO {
        private Long id;
        private String bneNm;     // 학명 + 과명 (한방명)
        private String hbdcNm;    // 본초명
        private String cntntSj;   // 약초명 (ex: 해바라기)
}
