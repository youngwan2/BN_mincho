package com.mincho.herb.domain.like.application;

public interface HerbLikeService {
        void addHerbLike(Long herbId);
        void deleteHerbLike(Long herbId);
        int countByHerbId(Long herbId);
        Boolean isHerbLiked(Long herbId);

}
