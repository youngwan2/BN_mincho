package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private Long id;
    private String content;
    private Long writerId;
    private String writer;
    private String avatarUrl; // 작성자의 아바타 URL
    private Boolean isAdopted; // 채택 유무
    private Boolean isMine;
    private LocalDateTime createdAt;
    private List<String> images;
    private Long likeCount; // 좋아요 개수
    private Long dislikeCount; // 싫어요 개수
    private String userReaction; // 현재 사용자의 반응 (LIKE, DISLIKE, 또는 null)

    public AnswerDTO(Long id, String content, Long writerId, String writer, String avatarUrl, Boolean isAdopted, Boolean isMine, LocalDateTime createdAt, List<String> images) {
        this.id = id;
        this.content = content;
        this.writerId = writerId;
        this.writer = writer;
        this.avatarUrl = avatarUrl;
        this.isAdopted = isAdopted;
        this.isMine = isMine;
        this.createdAt = createdAt;
        this.images = images;
        this.likeCount = 0L;
        this.dislikeCount = 0L;
        this.userReaction = null;
    }
}
