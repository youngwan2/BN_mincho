package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.persistence.*;

@Entity
public class QnaReactionEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private QnaEntity qna;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private QnaReactionType type;
}

