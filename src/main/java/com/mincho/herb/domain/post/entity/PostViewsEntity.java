package com.mincho.herb.domain.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "PostViews")
@Data
@Builder
public class PostViewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long viewCount;

    @OneToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
}
