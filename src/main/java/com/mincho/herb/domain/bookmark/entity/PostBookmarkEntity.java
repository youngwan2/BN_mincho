package com.mincho.herb.domain.bookmark.entity;

import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "post_bookmark",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "post_id"})
       })
public class PostBookmarkEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    public static PostBookmarkEntity of(UserEntity user, PostEntity post) {
        return PostBookmarkEntity.builder()
                .user(user)
                .post(post)
                .build();
    }
}
