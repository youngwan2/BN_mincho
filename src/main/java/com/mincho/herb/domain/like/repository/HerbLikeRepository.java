package com.mincho.herb.domain.like.repository;

import com.mincho.herb.domain.like.entity.HerbLikeEntity;
import com.mincho.herb.domain.user.entity.UserEntity;

public interface HerbLikeRepository {

    Boolean existsByMemberIdAndHerbId(Long memberId, Long herbId);
    void insertHerbLike(HerbLikeEntity herbLikeEntity);
    void deleteByMemberIdAndHerbId(Long memberId, Long herbId);
    int countByHerbId(Long herbId);
    void deleteByUser(UserEntity member);
}
