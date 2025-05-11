package com.mincho.herb.domain.like.application;

import com.mincho.herb.domain.like.dto.LikeHerbResponseDTO;

public interface HerbLikeService {
        LikeHerbResponseDTO addHerbLike(Long herbId);
        void deleteHerbLike(Long herbId);
        int countByHerbId(Long herbId);
        Boolean isHerbLiked(Long herbId);

}
