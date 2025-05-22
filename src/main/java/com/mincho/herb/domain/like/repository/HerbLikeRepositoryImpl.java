package com.mincho.herb.domain.like.repository;

import com.mincho.herb.domain.like.entity.HerbLikeEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HerbLikeRepositoryImpl implements HerbLikeRepository{

    private final HerbLikeJpaRepository herbLikeJpaRepository;

    @Override
    public Boolean existsByMemberIdAndHerbId(Long memberId, Long herbId) {
        return herbLikeJpaRepository.existsByUserIdAndHerbId(memberId, herbId) > 0;
    }

    @Override
    @Transactional
    public void insertHerbLike(HerbLikeEntity herbLikeEntity) {
        herbLikeJpaRepository.save(herbLikeEntity);
    }

    @Override
    @Transactional
    public void deleteByMemberIdAndHerbId(Long memberId, Long herbId) {
        herbLikeJpaRepository.deleteByUserIdAndHerbId(memberId, herbId);
    }

    @Override
    public int countByHerbId(Long herbId) {
        return herbLikeJpaRepository.countByHerbId(herbId);
    }

    @Override
    public void deleteByUser(UserEntity member) {
        herbLikeJpaRepository.deleteByUser(member);
    }
}
